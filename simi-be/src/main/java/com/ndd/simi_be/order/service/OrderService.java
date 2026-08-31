package com.ndd.simi_be.order.service;

import com.ndd.simi_be.common.exception.BadRequestException;
import com.ndd.simi_be.common.exception.ForbiddenException;
import com.ndd.simi_be.common.exception.ResourceNotFoundException;
import com.ndd.simi_be.consignment.entity.ConsignmentItem;
import com.ndd.simi_be.consignment.enums.ConsignmentItemStatus;
import com.ndd.simi_be.consignment.repository.ConsignmentItemRepository;
import com.ndd.simi_be.email.dto.OrderConfirmationEmailData;
import com.ndd.simi_be.location.entity.Province;
import com.ndd.simi_be.location.entity.Ward;
import com.ndd.simi_be.location.repository.ProvinceRepository;
import com.ndd.simi_be.location.repository.WardRepository;
import com.ndd.simi_be.order.dto.request.CreatePosOrderRequest;
import com.ndd.simi_be.order.dto.request.OrderFilterRequest;
import com.ndd.simi_be.order.dto.request.OrderItemRequest;
import com.ndd.simi_be.order.dto.request.OrderRequest;
import com.ndd.simi_be.order.dto.response.CreateOrderResponse;
import com.ndd.simi_be.order.dto.response.OrderDetailResponse;
import com.ndd.simi_be.order.dto.response.OrderSummaryResponse;
import com.ndd.simi_be.order.entity.Order;
import com.ndd.simi_be.order.entity.OrderItem;
import com.ndd.simi_be.order.enums.OrderChannel;
import com.ndd.simi_be.order.enums.OrderStatus;
import com.ndd.simi_be.order.event.OrderCreatedEvent;
import com.ndd.simi_be.order.mapper.OrderMapper;
import com.ndd.simi_be.order.repository.OrderRepository;
import com.ndd.simi_be.order.specification.OrderSpecification;
import com.ndd.simi_be.payment.entity.Payment;
import com.ndd.simi_be.payment.enums.PaymentMethod;
import com.ndd.simi_be.payment.enums.PaymentProvider;
import com.ndd.simi_be.payment.enums.PaymentStatus;
import com.ndd.simi_be.payment.provider.vnpay.VnPayPaymentProvider;
import com.ndd.simi_be.payment.repository.PaymentRepository;
import com.ndd.simi_be.payment.service.PaymentService;
import com.ndd.simi_be.product.entity.Product;
import com.ndd.simi_be.product.entity.ProductImage;
import com.ndd.simi_be.product.enums.ProductStatus;
import com.ndd.simi_be.user.entity.User;
import com.ndd.simi_be.user.repository.UserRepository;
import com.ndd.simi_be.user.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ndd.simi_be.order.event.StaffOrderCreatedEvent;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProvinceRepository provinceRepository;
    private final WardRepository wardRepository;
    private final OrderItemService orderItemService;
    private final PaymentService paymentService;
    private final UserRepository userRepository;
    private final ConsignmentItemRepository consignmentItemRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final PaymentRepository paymentRepository;
    private final VnPayPaymentProvider vnPayPaymentProvider;
    private final OrderReservationReleaseService reservationReleaseService;


    @Transactional
    public CreateOrderResponse createOrder(OrderRequest request, User customer, String ipAddress) {
        validateOnlineCheckout(request);

        Province province = provinceRepository.findById(request.getProvince())
                .orElseThrow(() -> new ResourceNotFoundException("Tỉnh/thành phố không hợp lệ"));
        Ward ward = wardRepository.findById(request.getWard())
                .orElseThrow(() -> new ResourceNotFoundException("Xã/phường không hợp lệ"));
        if (!ward.getProvinceCode().equals(province.getCode())) {
            throw new BadRequestException("Xã/phường không thuộc tỉnh");
        }

        LocalDateTime now = LocalDateTime.now();
        Order order = Order.builder()
                .customer(customer)
                .recipientName(request.getRecipientName())
                .recipientPhone(request.getRecipientPhone())
                .ward(ward.getFullName())
                .province(province.getFullName())
                .addressDetail(request.getAddressDetail())
                .discount(BigDecimal.ZERO)
                .orderChannel(OrderChannel.ONLINE)
                .orderStatus(request.getPaymentMethod() == PaymentMethod.COD
                        ? OrderStatus.PENDING
                        : OrderStatus.PENDING_PAYMENT)
                .reservationExpiresAt(request.getPaymentMethod() == PaymentMethod.ONLINE
                        ? now.plusMinutes(30) : null)
                .build();
        order = orderRepository.save(order);

        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderItemRequest itemRequest : request.getOrderItemRequests()) {
            OrderItem orderItem = orderItemService.createOrderItem(itemRequest, order);
            orderItems.add(orderItem);
        }
        order.setOrderItems(orderItems);

        BigDecimal subtotalAmount = orderItems.stream()
                .map(OrderItem::getUnitPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setSubtotalAmount(subtotalAmount);

        BigDecimal shippingFee = calculateShippingFee(request.getProvince(), subtotalAmount);
        order.setShippingFee(shippingFee);

        BigDecimal finalAmount = subtotalAmount.add(shippingFee);
        order.setFinalAmount(finalAmount);

        String paymentUrl = null;

        if (request.getPaymentMethod() == PaymentMethod.COD) {
            Payment payment = Payment.builder()
                    .order(order)
                    .amount(order.getFinalAmount())
                    .paymentMethod(PaymentMethod.COD)
                    .paymentProvider(PaymentProvider.NONE)
                    .paymentStatus(PaymentStatus.PENDING)
                    .build();
            paymentRepository.save(payment);
            order.getPayments().add(payment);

            if (customer != null && customer.getEmail() != null) {
                OrderConfirmationEmailData emailData =
                        OrderConfirmationEmailData.builder()
                                .orderId(order.getId())
                                .recipientEmail(customer.getEmail())
                                .recipientName(customer.getFullName())
                                .recipientPhone(customer.getPhoneNumber())
                                .addressDetail(order.getAddressDetail())
                                .ward(order.getWard())
                                .province(order.getProvince())
                                .paymentMethod(request.getPaymentMethod().name())
                                .createdDate(order.getCreatedDate())
                                .subtotalAmount(order.getSubtotalAmount())
                                .shippingFee(order.getShippingFee())
                                .finalAmount(order.getFinalAmount())
                                .items(orderItems.stream().map(oi -> {
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
                                }).toList())
                                .build();
                eventPublisher.publishEvent(new OrderCreatedEvent(this, emailData));
            }
        } else if (request.getPaymentMethod() == PaymentMethod.ONLINE) {
            Payment payment = Payment.builder()
                    .order(order)
                    .amount(order.getFinalAmount())
                    .paymentMethod(PaymentMethod.ONLINE)
                    .paymentProvider(PaymentProvider.VNPAY)
                    .paymentStatus(PaymentStatus.PENDING)
                    .gatewayCreatedAt(now)
                    .expiresAt(now.plusMinutes(15))
                    .build();
            payment = paymentRepository.save(payment);

            String txnRef = String.format("SIMI_PAY_%d_%s",
                    payment.getId(), now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
            payment.setGatewayTransactionRef(txnRef);
            paymentRepository.saveAndFlush(payment);

            order.getPayments().add(payment);

            paymentUrl = vnPayPaymentProvider.createPaymentUrl(
                    payment, ipAddress, "Thanh toan don hang #" + order.getId()
            );
        }

        eventPublisher.publishEvent(new StaffOrderCreatedEvent(order.getId()));

        return CreateOrderResponse.builder()
                .orderDetail(OrderMapper.toOrderDetailResponse(order))
                .paymentUrl(paymentUrl)
                .build();
    }

    @Transactional
    public CreateOrderResponse retryPayment(Long orderId, User customer, String ipAddress){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng"));

        if (order.getCustomer() == null || !order.getCustomer().getId().equals(customer.getId())){
            throw new ForbiddenException("Bạn không có quyền thao tác trên đơn hàng này");
        }
        if (order.getOrderStatus() != OrderStatus.PENDING_PAYMENT){
            throw new BadRequestException("Đơn hàng không ở trạng thái chờ thanh toán");
        }

        LocalDateTime now = LocalDateTime.now();

        if (order.getReservationExpiresAt() != null && now.isAfter(order.getReservationExpiresAt())){
            throw new BadRequestException("Đơn hàng đã hết hạn giữ chỗ. Vui lòng đặt lại đơn mới!");
        }

        Payment activePending = paymentRepository.findByOrderId(orderId).stream()
                .filter(p -> p.getPaymentMethod() == PaymentMethod.ONLINE)
                .filter(p -> p.getPaymentStatus() == PaymentStatus.PENDING)
                .findFirst()
                .orElse(null);

        if (activePending != null
                && activePending.getExpiresAt() != null
                && now.isBefore(activePending.getExpiresAt())){
            String existingUrl = vnPayPaymentProvider.createPaymentUrl(
                    activePending, ipAddress, "Thanh toan don hang #" + orderId
            );
            return CreateOrderResponse.builder()
                    .orderDetail(OrderMapper.toOrderDetailResponse(order))
                    .paymentUrl(existingUrl)
                    .build();
        }

        if (activePending != null){
            activePending.setPaymentStatus(PaymentStatus.CANCELLED);
            paymentRepository.saveAndFlush(activePending);
        }

        Payment newAttempt = Payment.builder()
                .order(order)
                .amount(order.getFinalAmount())
                .paymentMethod(PaymentMethod.ONLINE)
                .paymentProvider(PaymentProvider.VNPAY)
                .paymentStatus(PaymentStatus.PENDING)
                .gatewayCreatedAt(now)
                .expiresAt(now.plusMinutes(15))
                .build();
        newAttempt = paymentRepository.save(newAttempt);

        String txnRef = String.format("SIMI_PAY_%d_%s",
                newAttempt.getId(), now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
        newAttempt.setGatewayTransactionRef(txnRef);
        paymentRepository.saveAndFlush(newAttempt);

        String newUrl = vnPayPaymentProvider.createPaymentUrl(
                newAttempt, ipAddress, "Thanh toan don hang #" + order.getId()
        );

        return CreateOrderResponse.builder()
                .orderDetail(OrderMapper.toOrderDetailResponse(order))
                .paymentUrl(newUrl)
                .build();
    }

    public BigDecimal calculateShippingFee(String provinceCode, BigDecimal subtotalAmount) {
        if ("79".equals(provinceCode)) {
            if (subtotalAmount.compareTo(BigDecimal.valueOf(150000)) < 0) {
                return BigDecimal.valueOf(30000);
            }
            if (subtotalAmount.compareTo(BigDecimal.valueOf(250000)) < 0) {
                return BigDecimal.valueOf(25000);
            }
            if (subtotalAmount.compareTo(BigDecimal.valueOf(500000)) < 0) {
                return BigDecimal.valueOf(20000);
            }
            return BigDecimal.ZERO;
        } else {
            if (subtotalAmount.compareTo(BigDecimal.valueOf(150000)) < 0) {
                return BigDecimal.valueOf(35000);
            }
            if (subtotalAmount.compareTo(BigDecimal.valueOf(250000)) < 0) {
                return BigDecimal.valueOf(30000);
            }
            if (subtotalAmount.compareTo(BigDecimal.valueOf(500000)) < 0) {
                return BigDecimal.valueOf(25000);
            }
            if (subtotalAmount.compareTo(BigDecimal.valueOf(1000000)) < 0) {
                return BigDecimal.valueOf(20000);
            }
            return BigDecimal.ZERO;
        }
    }

    public Page<OrderSummaryResponse> searchOrder(OrderFilterRequest filterRequest) {
        LocalDateTime fromDate = null;
        if (filterRequest.getFromDate() != null) {
            fromDate = filterRequest.getFromDate().atStartOfDay();
        }

        LocalDateTime toDate = null;
        if (filterRequest.getToDate() != null) {
            toDate = filterRequest.getToDate().atTime(23, 59, 59);
        }

        Specification<Order> specification = Specification.allOf(
                OrderSpecification.hasKeyword(filterRequest.getKeyword()),
                OrderSpecification.hasOrderChannel(filterRequest.getOrderChannel()),
                OrderSpecification.hasStatus(filterRequest.getOrderStatus()),
                OrderSpecification.hasFromDate(fromDate),
                OrderSpecification.hasToDate(toDate)
        );

        Sort sort = filterRequest.getSortDir().equalsIgnoreCase("desc")
                ? Sort.by(filterRequest.getSortBy()).descending()
                : Sort.by(filterRequest.getSortBy()).ascending();

        Pageable pageable = PageRequest.of(filterRequest.getPage(), filterRequest.getSize(), sort);

        return orderRepository.findAll(specification, pageable).map(OrderMapper::toOrderSummaryResponse);
    }

    @Transactional
    public OrderDetailResponse createPosOrder(
            CreatePosOrderRequest request,
            User acceptedBy
    ) {
        validatePosCheckout(request);

        User customer = userRepository.findByPhoneNumber(request.getRecipientPhone()).orElse(null);

        Order order = Order.builder()
                .customer(customer)
                .addressDetail(null)
                .ward(null)
                .province(null)
                .acceptedBy(acceptedBy)
                .recipientPhone(request.getRecipientPhone())
                .recipientName(request.getRecipientName())
                .shippingFee(BigDecimal.ZERO)
                .orderChannel(OrderChannel.IN_STORE)
                .orderStatus(OrderStatus.COMPLETED)
                .discount(BigDecimal.ZERO)
                .build();

        order = orderRepository.save(order);

        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderItemRequest itemRequest : request.getOrderItemRequests()) {
            OrderItem orderItem = orderItemService.createPosOrderItem(itemRequest, order);
            orderItems.add(orderItem);
        }
        order.setOrderItems(orderItems);

        BigDecimal subtotalAmount = orderItems.stream()
                .map(OrderItem::getUnitPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setSubtotalAmount(subtotalAmount);
        order.setFinalAmount(subtotalAmount);

        Payment payment = paymentService.createPayment(order, PaymentMethod.CASH);
        order.getPayments().add(payment);
        order.setCompletedAt(LocalDateTime.now());

        return OrderMapper.toOrderDetailResponse(orderRepository.save(order));
    }

    @Transactional
    public void changeStatus(OrderStatus status, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng"));

        OrderStatus currentStatus = order.getOrderStatus();

        if (currentStatus == status) {
            return;
        }

        if (!currentStatus.canTransitionTo(status)) {
            throw new BadRequestException(
                    String.format("Không thể chuyển trạng thái đơn hàng từ %s sang %s", currentStatus, status)
            );
        }

        if (status == OrderStatus.PACKING && !hasCodPayment(order) && !hasPaidOnlinePayment(order)) {
            throw new BadRequestException("Đơn online chỉ được đóng gói sau khi thanh toán thành công");
        }

        if (status == OrderStatus.COMPLETED) {
            completeOrder(order);
        }

        if (status == OrderStatus.CANCELLED) {
            reservationReleaseService.release(order, LocalDateTime.now());
        }

        order.setOrderStatus(status);
        orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public Page<OrderSummaryResponse> getMyOrders(OrderFilterRequest filterRequest, User user) {
        LocalDateTime fromDate = null;
        if (filterRequest.getFromDate() != null) {
            fromDate = filterRequest.getFromDate().atStartOfDay();
        }

        LocalDateTime toDate = null;
        if (filterRequest.getToDate() != null) {
            toDate = filterRequest.getToDate().atTime(23, 59, 59);
        }

        Specification<Order> specification = Specification.allOf(
                OrderSpecification.hasKeyword(filterRequest.getKeyword()),
                OrderSpecification.hasOrderChannel(filterRequest.getOrderChannel()),
                OrderSpecification.hasStatus(filterRequest.getOrderStatus()),
                OrderSpecification.hasFromDate(fromDate),
                OrderSpecification.hasToDate(toDate),
                OrderSpecification.hasCustomer(user)
        );

        Sort sort = filterRequest.getSortDir().equalsIgnoreCase("desc")
                ? Sort.by(filterRequest.getSortBy()).descending()
                : Sort.by(filterRequest.getSortBy()).ascending();

        Pageable pageable = PageRequest.of(filterRequest.getPage(), filterRequest.getSize(), sort);

        return orderRepository.findAll(specification, pageable).map(OrderMapper::toOrderSummaryResponse);
    }

    @Transactional(readOnly = true)
    public OrderDetailResponse getOrderDetail(Long orderId, User currentUser) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng này"));

        if (currentUser == null || (currentUser.getRole() == Role.CUSTOMER
                && (order.getCustomer() == null || !order.getCustomer().getId().equals(currentUser.getId())))) {
            throw new ForbiddenException("Bạn không có quyền xem đơn hàng này");
        }

        return OrderMapper.toOrderDetailResponse(order);
    }

    private void validateOnlineCheckout(OrderRequest request) {
        if (request.getPaymentMethod() != PaymentMethod.COD && request.getPaymentMethod() != PaymentMethod.ONLINE) {
            throw new BadRequestException("Phương thức thanh toán không hợp lệ cho đơn online");
        }

        validateDistinctItems(request.getOrderItemRequests(), "Đơn hàng phải có ít nhất một sản phẩm");
        validateNoDiscount(request.getDiscount());
    }

    private void validatePosCheckout(CreatePosOrderRequest request) {
        if (request.getPaymentMethod() != PaymentMethod.CASH) {
            throw new BadRequestException("POS hiện chỉ hỗ trợ thanh toán tiền mặt");
        }

        validateDistinctItems(request.getOrderItemRequests(), "Đơn POS phải có ít nhất một sản phẩm");
        validateNoDiscount(request.getDiscount());
    }

    private void validateDistinctItems(List<OrderItemRequest> itemRequests, String emptyMessage) {
        if (itemRequests == null || itemRequests.isEmpty()) {
            throw new BadRequestException(emptyMessage);
        }

        Set<Long> productIds = new HashSet<>();
        for (OrderItemRequest itemRequest : itemRequests) {
            if (itemRequest == null || itemRequest.getProductId() == null) {
                throw new BadRequestException("Sản phẩm không hợp lệ");
            }
            if (!productIds.add(itemRequest.getProductId())) {
                throw new BadRequestException("Không thể đặt cùng một sản phẩm nhiều lần");
            }
        }

    }

    private void validateNoDiscount(BigDecimal discount) {
        if (discount != null && discount.signum() != 0) {
            throw new BadRequestException("Hệ thống chưa hỗ trợ giảm giá");
        }
    }

    private void completeOrder(Order order) {
        if (hasCodPayment(order)) {
            for (Payment payment : order.getPayments()) {
                if (payment.getPaymentMethod() == PaymentMethod.COD
                        && payment.getPaymentStatus() == PaymentStatus.PENDING) {
                    payment.setPaymentStatus(PaymentStatus.PAID);
                    payment.setPaidAt(LocalDateTime.now());
                }
            }
        } else if (!hasPaidOnlinePayment(order)) {
            throw new BadRequestException("Đơn online chưa được thanh toán thành công");
        }

        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            product.setProductStatus(ProductStatus.SOLD);

            ConsignmentItem consignmentItem = consignmentItemRepository.findByProduct(product)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chi tiết lô hàng tương ứng"));
            consignmentItem.setConsignmentItemStatus(ConsignmentItemStatus.SOLD);
        }

        order.setCompletedAt(LocalDateTime.now());
    }

    private boolean hasCodPayment(Order order) {
        return order.getPayments().stream()
                .anyMatch(payment -> payment.getPaymentMethod() == PaymentMethod.COD);
    }

    private boolean hasPaidOnlinePayment(Order order) {
        return order.getPayments().stream()
                .anyMatch(payment -> payment.getPaymentMethod() == PaymentMethod.ONLINE
                        && payment.getPaymentStatus() == PaymentStatus.PAID);
    }
}
