package com.ndd.simi_be.payment.service;

import com.ndd.simi_be.common.exception.BadRequestException;
import com.ndd.simi_be.consignment.enums.ConsignmentItemStatus;
import com.ndd.simi_be.consignment.repository.ConsignmentItemRepository;
import com.ndd.simi_be.email.dto.OrderConfirmationEmailData;
import com.ndd.simi_be.order.entity.Order;
import com.ndd.simi_be.order.entity.OrderItem;
import com.ndd.simi_be.order.enums.OrderStatus;
import com.ndd.simi_be.order.event.OrderPaidEvent;
import com.ndd.simi_be.order.repository.OrderRepository;
import com.ndd.simi_be.payment.dto.PaymentVerifyResult;
import com.ndd.simi_be.payment.entity.Payment;
import com.ndd.simi_be.payment.enums.PaymentMethod;
import com.ndd.simi_be.payment.enums.PaymentStatus;
import com.ndd.simi_be.payment.enums.PaymentVerificationStatus;
import com.ndd.simi_be.payment.provider.vnpay.VnPayPaymentProvider;
import com.ndd.simi_be.payment.repository.PaymentRepository;
import com.ndd.simi_be.product.entity.Product;
import com.ndd.simi_be.product.entity.ProductImage;
import com.ndd.simi_be.product.enums.ProductStatus;
import com.ndd.simi_be.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final ConsignmentItemRepository consignmentItemRepository;
    private final VnPayPaymentProvider vnPayPaymentProvider;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public Payment createPayment(Order order, PaymentMethod method){

        if (method == PaymentMethod.COD){
            Payment payment = Payment.builder()
                    .order(order)
                    .amount(order.getFinalAmount())
                    .paymentMethod(method)
                    .paymentStatus(PaymentStatus.PENDING)
                    .build();

            return paymentRepository.save(payment);
        } else if (method == PaymentMethod.CASH) {
            Payment payment = Payment.builder()
                    .order(order)
                    .amount(order.getFinalAmount())
                    .paymentMethod(method)
                    .paymentStatus(PaymentStatus.PAID)
                    .build();

            return paymentRepository.save(payment);
        } else {
            throw new BadRequestException("Chưa hỗ trợ thanh toán online");
        }
    }


    @Transactional
    public Map<String, String> handleVnPayIpn(Map<String, String> params){
        Map<String, String> response = new HashMap<>();

        PaymentVerifyResult verifyResult = vnPayPaymentProvider.verifyCallback(params);
        if (!verifyResult.isValidChecksum()){
            log.warn("VNPay IPN: Sai checksum. TxnRef: {}", params.get("vnp_TxnRef"));
            response.put("RspCode", "97");
            response.put("Message", "Invalid Checksum");
            return response;
        }

        Payment payment = paymentRepository.findByGatewayTransactionRefForUpdate(
                verifyResult.getGatewayTxnRef()).orElse(null);
        if (payment == null){
            log.warn("VNPay IPN: Không tìm thấy Payment. Ref: {}", verifyResult.getGatewayTxnRef());
            response.put("RspCode", "01");
            response.put("Message", "Order not found");
            return response;
        }

        if (payment.getAmount().longValue() != verifyResult.getAmount()) {
            log.warn("VNPay IPN: Sai số tiền. DB={}, Gateway={}",
                    payment.getAmount(), verifyResult.getAmount());
            response.put("RspCode", "04");
            response.put("Message", "Invalid Amount");
            return response;
        }

        if (payment.getPaymentStatus() == PaymentStatus.PAID){
            log.info("VNPay INP: Payment #{} PAID", payment.getId());
            response.put("RspCode", "02");
            response.put("Message", "Order already confirmed");
            return response;
        }

        Order order = payment.getOrder();

        payment.setGatewayTransactionId(verifyResult.getGatewayTransactionNo());
        payment.setGatewayBankCode(verifyResult.getGatewayBankCode());
        payment.setGatewayResponseCode(verifyResult.getGatewayResponseCode());

        if (verifyResult.getVerificationStatus() == PaymentVerificationStatus.SUCCESS){
            boolean isInactiveAttempt = payment.getPaymentStatus() == PaymentStatus.CANCELLED
                    || payment.getPaymentStatus() == PaymentStatus.FAILED;
            boolean isExpiredOrder = order.getOrderStatus() == OrderStatus.EXPIRED;

            if (isExpiredOrder || isInactiveAttempt){
                log.error("CRITICAL ANOMALY: SUCCESS từ VNPay cho Payment #{} (status={}) hoặc Order #{} (status={}). Amount={}, TxnRef={}",
                        payment.getId(), payment.getPaymentStatus(), order.getId(), order.getOrderStatus(), payment.getAmount(), verifyResult.getGatewayTxnRef());
                paymentRepository.save(payment);
                response.put("RspCode", "00");
                response.put("Message", "Confirm Success (Anomaly Logged)");
                return response;
            }

            payment.setPaymentStatus(PaymentStatus.PAID);
            payment.setPaidAt(LocalDateTime.now());
            order.setOrderStatus(OrderStatus.PACKING);

            for (OrderItem item : order.getOrderItems()){
                Product product = item.getProduct();
                product.setProductStatus(ProductStatus.SOLD);

                consignmentItemRepository.findByProduct(product)
                        .ifPresent(ci -> ci.setConsignmentItemStatus(ConsignmentItemStatus.SOLD));
            }

            User customer = order.getCustomer();
            if (customer != null && customer.getEmail() != null) {
                OrderConfirmationEmailData emailData = OrderConfirmationEmailData.builder()
                        .orderId(order.getId())
                        .recipientEmail(customer.getEmail())
                        .recipientName(order.getRecipientName())
                        .recipientPhone(order.getRecipientPhone())
                        .addressDetail(order.getAddressDetail())
                        .ward(order.getWard())
                        .province(order.getProvince())
                        .paymentMethod(payment.getPaymentMethod().name())
                        .createdDate(order.getCreatedDate())
                        .subtotalAmount(order.getSubtotalAmount())
                        .shippingFee(order.getShippingFee())
                        .finalAmount(order.getFinalAmount())
                        .items(order.getOrderItems().stream()
                                .map(oi -> {
                                    Product product = oi.getProduct();
                                    String thumbnailUrl = product.getProductImages().stream()
                                            .filter(ProductImage::isThumbnail)
                                            .map(ProductImage::getImageUrl)
                                            .findFirst()
                                            .orElse(null);
                                    return OrderConfirmationEmailData.ItemData.builder()
                                            .productName(product.getName())
                                            .size(product.getSize())
                                            .color(product.getColor())
                                            .thumbnailUrl(thumbnailUrl)
                                            .unitPrice(oi.getUnitPrice())
                                            .build();
                                })
                                .toList()
                        )
                        .build();
                eventPublisher.publishEvent(new OrderPaidEvent(this, emailData));
            }

            log.info("VNPay IPN: Đơn hàng #{} thanh toán thành công!", order.getId());
        } else if (verifyResult.getVerificationStatus() == PaymentVerificationStatus.FAILED) {
            payment.setPaymentStatus(PaymentStatus.FAILED);
            log.info("VNPay IPN: Payment #{} thất bại. Order #{} giữ PENDING_PAYMENT.",
                    payment.getId(), order.getId());
        } else if (verifyResult.getVerificationStatus() == PaymentVerificationStatus.PENDING) {
            log.info("VNPay IPN: Giao dịch #{} đang chờ xử lý tại ngân hàng.", payment.getId());
            paymentRepository.save(payment);
            response.put("RspCode", "00");
            response.put("Message", "Transaction Pending");
            return response;
        } else {
            log.warn("⚠️ VNPay IPN: Giao dịch #{} nghi ngờ (Code: {}). Giữ PENDING để đối soát.",
                    payment.getId(), verifyResult.getGatewayTransactionStatus());
            paymentRepository.save(payment);
            response.put("RspCode", "00");
            response.put("Message", "Transaction Marked for Review");
            return response;
        }

        paymentRepository.save(payment);
        orderRepository.save(order);

        response.put("RspCode", "00");
        response.put("Message", "Confirm Success");
        return response;
    }
}
