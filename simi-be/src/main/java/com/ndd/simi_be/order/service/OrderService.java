package com.ndd.simi_be.order.service;

import com.ndd.simi_be.common.exception.BadRequestException;
import com.ndd.simi_be.common.exception.ResourceNotFoundException;
import com.ndd.simi_be.consignment.entity.ConsignmentItem;
import com.ndd.simi_be.consignment.enums.ConsignmentItemStatus;
import com.ndd.simi_be.consignment.repository.ConsignmentItemRepository;
import com.ndd.simi_be.location.entity.Province;
import com.ndd.simi_be.location.entity.Ward;
import com.ndd.simi_be.location.repository.ProvinceRepository;
import com.ndd.simi_be.location.repository.WardRepository;
import com.ndd.simi_be.order.dto.request.OrderFilterRequest;
import com.ndd.simi_be.order.dto.request.OrderItemRequest;
import com.ndd.simi_be.order.dto.request.OrderOfflineRequest;
import com.ndd.simi_be.order.dto.request.OrderRequest;
import com.ndd.simi_be.order.dto.response.OrderDetailResponse;
import com.ndd.simi_be.order.dto.response.OrderSummaryResponse;
import com.ndd.simi_be.order.entity.Order;
import com.ndd.simi_be.order.entity.OrderItem;
import com.ndd.simi_be.order.enums.OrderChannel;
import com.ndd.simi_be.order.enums.OrderStatus;
import com.ndd.simi_be.order.mapper.OrderMapper;
import com.ndd.simi_be.order.repository.OrderItemRepository;
import com.ndd.simi_be.order.repository.OrderRepository;
import com.ndd.simi_be.order.specification.OrderSpecification;
import com.ndd.simi_be.payment.entity.Payment;
import com.ndd.simi_be.payment.enums.PaymentMethod;
import com.ndd.simi_be.payment.service.PaymentService;
import com.ndd.simi_be.user.entity.User;
import com.ndd.simi_be.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Transactional
    public OrderDetailResponse createOrder(OrderRequest request, User customer){

        Province province = provinceRepository.findById(request.getProvince())
                .orElseThrow(() -> new ResourceNotFoundException("Tỉnh/thành phố không hợp lệ"));

        Ward ward = wardRepository.findById(request.getWard())
                .orElseThrow(() -> new ResourceNotFoundException("Xã/phường không hợp lệ"));

        if (!ward.getProvinceCode().equals(province.getCode())){
            throw new BadRequestException("Xã/phường không thuộc tỉnh");
        }

        Order order = Order.builder()
                .customer(customer)
                .recipientName(request.getRecipientName())
                .recipientPhone(request.getRecipientPhone())
                .ward(ward.getFullName())
                .province(province.getFullName())
                .addressDetail(request.getAddressDetail())
                .discount(request.getDiscount())
                .orderChannel(OrderChannel.ONLINE)
                .orderStatus(OrderStatus.PENDING)
                .build();
        orderRepository.save(order);

        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderItemRequest itemRequest : request.getOrderItemRequests()){
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

        BigDecimal discount = request.getDiscount() != null ? request.getDiscount() : BigDecimal.ZERO;
        BigDecimal finalAmount
                = (subtotalAmount.multiply(BigDecimal.ONE.subtract(discount))).add(shippingFee);
        order.setFinalAmount(finalAmount);

        if (request.getPaymentMethod() == PaymentMethod.COD) {
            Payment payment = paymentService.createPayment(order, request.getPaymentMethod());
            order.getPayments().add(payment);
            order.setOrderStatus(OrderStatus.PACKING);
        }

        return OrderMapper.toOrderDetailResponse(order);
    }

    public BigDecimal calculateShippingFee(String provinceCode, BigDecimal subtotalAmount){
        if ("79".equals(provinceCode)){
            if (subtotalAmount.compareTo(BigDecimal.valueOf(150000)) < 0){
                return BigDecimal.valueOf(30000);
            }
            if (subtotalAmount.compareTo(BigDecimal.valueOf(250000)) < 0){
                return BigDecimal.valueOf(25000);
            }
            if (subtotalAmount.compareTo(BigDecimal.valueOf(500000)) < 0){
                return BigDecimal.valueOf(20000);
            }
            return BigDecimal.ZERO;
        }else {
            if (subtotalAmount.compareTo(BigDecimal.valueOf(150000)) < 0){
                return BigDecimal.valueOf(35000);
            }
            if (subtotalAmount.compareTo(BigDecimal.valueOf(250000)) < 0){
                return BigDecimal.valueOf(30000);
            }
            if (subtotalAmount.compareTo(BigDecimal.valueOf(500000)) < 0){
                return BigDecimal.valueOf(25000);
            }
            if (subtotalAmount.compareTo(BigDecimal.valueOf(1000000)) < 0){
                return BigDecimal.valueOf(20000);
            }
            return BigDecimal.ZERO;
        }
    }

    public Page<OrderSummaryResponse> searchOrder(OrderFilterRequest filterRequest){
        LocalDateTime fromDate = null;
        if (filterRequest.getFromDate() != null){
            fromDate = filterRequest.getFromDate().atStartOfDay();
        }

        LocalDateTime toDate = null;
        if (filterRequest.getToDate() != null){
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
    public OrderDetailResponse createOrderOffline(
            OrderOfflineRequest request,
            User acceptedBy
    ){
        if (request.getPaymentMethod() == PaymentMethod.COD){
            throw new BadRequestException("Không thể chọn phương thức COD khi mua trực tiếp");
        }

        User customer = userRepository.findByPhoneNumber(request.getRecipientPhone()).orElse(null);

        if (request.getPaymentMethod() == PaymentMethod.CASH){
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
                    .discount(request.getDiscount())
                    .build();

            order = orderRepository.save(order);

            List<OrderItem> orderItems = new ArrayList<>();
            for (OrderItemRequest itemRequest : request.getOrderItemRequests()){
                OrderItem orderItem = orderItemService.createOrderItemOffline(itemRequest, order);
                orderItems.add(orderItem);
            }
            order.setOrderItems(orderItems);

            BigDecimal subtotalAmount = orderItems.stream()
                    .map(OrderItem::getUnitPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            order.setSubtotalAmount(subtotalAmount);

            BigDecimal discount = request.getDiscount() != null ? request.getDiscount() : BigDecimal.ZERO;
            BigDecimal finalAmount
                    = subtotalAmount.multiply(BigDecimal.ONE.subtract(discount));
            order.setFinalAmount(finalAmount);

            Payment payment = paymentService.createPayment(order, PaymentMethod.CASH);
            order.getPayments().add(payment);
            order.setOrderStatus(OrderStatus.COMPLETED);

            for (OrderItem orderItem : orderItems){
                ConsignmentItem consignmentItem = consignmentItemRepository.findByProduct(orderItem.getProduct())
                        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chi tiết lô hàng "));

                consignmentItem.setConsignmentItemStatus(ConsignmentItemStatus.SOLD);
                consignmentItemRepository.save(consignmentItem);
            }

            return OrderMapper.toOrderDetailResponse(orderRepository.save(order));
        }

        throw new BadRequestException("Chưa hỗ trợ thanh toán ngân hàng / ví điện tử");
    }

    @Transactional
    public void changeStatus(OrderStatus status, Long orderId){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng"));

        OrderStatus currentStatus = order.getOrderStatus();

        if (currentStatus == status){
            return;
        }

        if (!currentStatus.canTransitionTo(status)){
            throw new BadRequestException(
                    String.format("Không thể chuyển trạng thái đơn hàng từ %s sang %s", currentStatus, status)
            );
        }

        order.setOrderStatus(status);
        orderRepository.save(order);
    }
}
