package com.ndd.simi_be.payment.service;

import com.ndd.simi_be.common.exception.ResourceNotFoundException;
import com.ndd.simi_be.consignment.entity.Consignment;
import com.ndd.simi_be.consignment.entity.ConsignmentItem;
import com.ndd.simi_be.consignment.enums.ConsignmentItemStatus;
import com.ndd.simi_be.consignment.repository.ConsignmentItemRepository;
import com.ndd.simi_be.order.entity.Order;
import com.ndd.simi_be.order.entity.OrderItem;
import com.ndd.simi_be.order.enums.OrderStatus;
import com.ndd.simi_be.order.repository.OrderRepository;
import com.ndd.simi_be.payment.dto.PaymentRequest;
import com.ndd.simi_be.payment.dto.PaymentResponse;
import com.ndd.simi_be.payment.entity.Payment;
import com.ndd.simi_be.payment.enums.PaymentStatus;
import com.ndd.simi_be.payment.mapper.PaymentMapper;
import com.ndd.simi_be.payment.repository.PaymentRepository;
import com.ndd.simi_be.product.entity.Product;
import com.ndd.simi_be.product.enums.ProductStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final ConsignmentItemRepository consignmentItemRepository;

    @Transactional
    public PaymentResponse createPayment(PaymentRequest request){

        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng"));

        Payment payment = Payment.builder()
                .order(order)
                .amount(order.getFinalAmount())
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        return PaymentMapper.toPaymentResponse(paymentRepository.save(payment));
    }

    @Transactional
    public void simulatePayment(Long paymentId, boolean success){
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin thanh toán"));

        if (success){
            payment.setPaymentStatus(PaymentStatus.PAID);
            payment.setPaidAt(LocalDateTime.now());

            Order order = payment.getOrder();
            order.setOrderStatus(OrderStatus.CONFIRMED);

            for (OrderItem orderItem : order.getOrderItems()){
                Product product = orderItem.getProduct();
                product.setProductStatus(ProductStatus.SOLD);

                ConsignmentItem consignmentItem = consignmentItemRepository.findByProduct(product)
                        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm"));

                consignmentItem.setConsignmentItemStatus(ConsignmentItemStatus.SOLD);
            }
        }else {
            payment.setPaymentStatus(PaymentStatus.FAILED);

            Order order = payment.getOrder();
            order.setOrderStatus(OrderStatus.CANCELLED);

            for (OrderItem orderItem : order.getOrderItems()){
                Product product = orderItem.getProduct();
                product.setProductStatus(ProductStatus.AVAILABLE);
            }
        }
    }
}
