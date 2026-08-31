package com.ndd.simi_be.order.service;

import com.ndd.simi_be.common.exception.BadRequestException;
import com.ndd.simi_be.common.exception.ForbiddenException;
import com.ndd.simi_be.consignment.entity.ConsignmentItem;
import com.ndd.simi_be.consignment.enums.ConsignmentItemStatus;
import com.ndd.simi_be.consignment.repository.ConsignmentItemRepository;
import com.ndd.simi_be.location.repository.ProvinceRepository;
import com.ndd.simi_be.location.repository.WardRepository;
import com.ndd.simi_be.order.dto.request.OrderItemRequest;
import com.ndd.simi_be.order.dto.request.OrderRequest;
import com.ndd.simi_be.order.dto.request.CreatePosOrderRequest;
import com.ndd.simi_be.order.entity.Order;
import com.ndd.simi_be.order.entity.OrderItem;
import com.ndd.simi_be.order.enums.OrderChannel;
import com.ndd.simi_be.order.enums.OrderStatus;
import com.ndd.simi_be.order.repository.OrderRepository;
import com.ndd.simi_be.payment.entity.Payment;
import com.ndd.simi_be.payment.enums.PaymentMethod;
import com.ndd.simi_be.payment.enums.PaymentStatus;
import com.ndd.simi_be.payment.provider.vnpay.VnPayPaymentProvider;
import com.ndd.simi_be.payment.repository.PaymentRepository;
import com.ndd.simi_be.payment.service.PaymentService;
import com.ndd.simi_be.product.entity.Product;
import com.ndd.simi_be.product.enums.ProductStatus;
import com.ndd.simi_be.user.entity.User;
import com.ndd.simi_be.user.enums.Role;
import com.ndd.simi_be.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private ProvinceRepository provinceRepository;
    @Mock private WardRepository wardRepository;
    @Mock private OrderItemService orderItemService;
    @Mock private PaymentService paymentService;
    @Mock private UserRepository userRepository;
    @Mock private ConsignmentItemRepository consignmentItemRepository;
    @Mock private ApplicationEventPublisher eventPublisher;
    @Mock private PaymentRepository paymentRepository;
    @Mock private VnPayPaymentProvider vnPayPaymentProvider;
    @Mock private OrderReservationReleaseService reservationReleaseService;

    @InjectMocks private OrderService orderService;

    @Test
    void getOrderDetailRejectsAnotherCustomer() {
        User owner = user(1L, Role.CUSTOMER);
        User otherCustomer = user(2L, Role.CUSTOMER);
        Order order = order(owner, OrderStatus.PENDING);
        when(orderRepository.findById(10L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.getOrderDetail(10L, otherCustomer))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void getOrderDetailAllowsOwner() {
        User owner = user(1L, Role.CUSTOMER);
        Order order = order(owner, OrderStatus.PENDING);
        when(orderRepository.findById(10L)).thenReturn(Optional.of(order));

        assertThat(orderService.getOrderDetail(10L, owner).getId()).isEqualTo(10L);
    }

    @Test
    void createOrderRejectsCashForOnlineCheckout() {
        OrderRequest request = orderRequest(PaymentMethod.CASH, List.of(itemRequest(11L)));

        assertThatThrownBy(() -> orderService.createOrder(request, user(1L, Role.CUSTOMER), "127.0.0.1"))
                .isInstanceOf(BadRequestException.class);

        verify(orderRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void createOrderRejectsDuplicateProducts() {
        OrderRequest request = orderRequest(PaymentMethod.COD, List.of(itemRequest(11L), itemRequest(11L)));

        assertThatThrownBy(() -> orderService.createOrder(request, user(1L, Role.CUSTOMER), "127.0.0.1"))
                .isInstanceOf(BadRequestException.class);

        verify(orderRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void createOrderRejectsClientProvidedDiscount() {
        OrderRequest request = orderRequest(PaymentMethod.COD, List.of(itemRequest(11L)));
        request.setDiscount(BigDecimal.valueOf(0.5));

        assertThatThrownBy(() -> orderService.createOrder(request, user(1L, Role.CUSTOMER), "127.0.0.1"))
                .isInstanceOf(BadRequestException.class);

        verify(orderRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void changeStatusRejectsPackingForUnpaidOnlineOrder() {
        Order order = order(user(1L, Role.CUSTOMER), OrderStatus.PENDING_PAYMENT);
        order.setPayments(new ArrayList<>(List.of(payment(order, PaymentMethod.ONLINE, PaymentStatus.PENDING))));
        when(orderRepository.findById(10L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.changeStatus(OrderStatus.PACKING, 10L))
                .isInstanceOf(BadRequestException.class);

        verify(orderRepository, never()).save(order);
    }

    @Test
    void createPosOrderRejectsAnyNonCashPaymentMethod() {
        CreatePosOrderRequest request = CreatePosOrderRequest.builder()
                .paymentMethod(PaymentMethod.ONLINE)
                .orderItemRequests(List.of(itemRequest(11L)))
                .discount(BigDecimal.ZERO)
                .recipientName("Walk-in customer")
                .recipientPhone("0900000000")
                .build();

        assertThatThrownBy(() -> orderService.createPosOrder(request, user(2L, Role.STAFF)))
                .isInstanceOf(BadRequestException.class);

        verify(orderRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void createPosOrderCreatesCompletedCashOrder() {
        CreatePosOrderRequest request = CreatePosOrderRequest.builder()
                .paymentMethod(PaymentMethod.CASH)
                .orderItemRequests(List.of(itemRequest(11L)))
                .discount(BigDecimal.ZERO)
                .recipientName("Walk-in customer")
                .recipientPhone("0900000000")
                .build();
        Product product = Product.builder()
                .name("Jacket")
                .currentPrice(BigDecimal.valueOf(100_000))
                .build();

        when(userRepository.findByPhoneNumber("0900000000")).thenReturn(Optional.empty());
        when(orderRepository.save(org.mockito.ArgumentMatchers.any(Order.class))).thenAnswer(invocation -> {
            Order savedOrder = invocation.getArgument(0);
            if (savedOrder.getId() == null) {
                savedOrder.setId(20L);
            }
            return savedOrder;
        });
        when(orderItemService.createPosOrderItem(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(Order.class)))
                .thenAnswer(invocation -> {
                    Order savedOrder = invocation.getArgument(1);
                    return OrderItem.builder()
                            .order(savedOrder)
                            .product(product)
                            .unitPrice(product.getCurrentPrice())
                            .build();
                });
        when(paymentService.createPayment(org.mockito.ArgumentMatchers.any(Order.class), org.mockito.ArgumentMatchers.eq(PaymentMethod.CASH)))
                .thenAnswer(invocation -> {
                    Order savedOrder = invocation.getArgument(0);
                    return Payment.builder()
                            .order(savedOrder)
                            .amount(savedOrder.getFinalAmount())
                            .paymentMethod(PaymentMethod.CASH)
                            .paymentStatus(PaymentStatus.PAID)
                            .build();
                });

        var response = orderService.createPosOrder(request, user(2L, Role.STAFF));

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository, org.mockito.Mockito.times(2)).save(orderCaptor.capture());
        Order savedOrder = orderCaptor.getAllValues().getLast();
        assertThat(response.getOrderStatus()).isEqualTo(OrderStatus.COMPLETED.name());
        assertThat(savedOrder.getOrderChannel()).isEqualTo(OrderChannel.IN_STORE);
        assertThat(savedOrder.getFinalAmount()).isEqualByComparingTo("100000");
        assertThat(savedOrder.getPayments().getFirst().getPaymentMethod()).isEqualTo(PaymentMethod.CASH);
        assertThat(savedOrder.getPayments().getFirst().getPaymentStatus()).isEqualTo(PaymentStatus.PAID);
    }

    @Test
    void changeStatusRejectsCompletionForUnpaidOnlineOrder() {
        Order order = order(user(1L, Role.CUSTOMER), OrderStatus.SHIPPING);
        order.setPayments(new ArrayList<>(List.of(payment(order, PaymentMethod.ONLINE, PaymentStatus.PENDING))));
        when(orderRepository.findById(10L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.changeStatus(OrderStatus.COMPLETED, 10L))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void changeStatusRejectsCancellingPaidOnlineOrderAfterPacking() {
        Order order = order(user(1L, Role.CUSTOMER), OrderStatus.PACKING);
        order.setPayments(new ArrayList<>(List.of(payment(order, PaymentMethod.ONLINE, PaymentStatus.PAID))));
        when(orderRepository.findById(10L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.changeStatus(OrderStatus.CANCELLED, 10L))
                .isInstanceOf(BadRequestException.class);

        verify(reservationReleaseService, never()).release(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
    }

    @Test
    void completingCodOrderMarksPaymentProductAndItemAsSold() {
        Order order = order(user(1L, Role.CUSTOMER), OrderStatus.SHIPPING);
        Payment payment = payment(order, PaymentMethod.COD, PaymentStatus.PENDING);
        order.setPayments(new ArrayList<>(List.of(payment)));

        Product product = Product.builder().productStatus(ProductStatus.RESERVED).build();
        product.setId(55L);
        OrderItem orderItem = OrderItem.builder().order(order).product(product).unitPrice(BigDecimal.valueOf(100_000)).build();
        order.setOrderItems(new ArrayList<>(List.of(orderItem)));

        ConsignmentItem consignmentItem = ConsignmentItem.builder()
                .consignmentItemStatus(ConsignmentItemStatus.RESERVED)
                .product(product)
                .build();

        when(orderRepository.findById(10L)).thenReturn(Optional.of(order));
        when(consignmentItemRepository.findByProduct(product)).thenReturn(Optional.of(consignmentItem));

        orderService.changeStatus(OrderStatus.COMPLETED, 10L);

        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.COMPLETED);
        assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.PAID);
        assertThat(product.getProductStatus()).isEqualTo(ProductStatus.SOLD);
        assertThat(consignmentItem.getConsignmentItemStatus()).isEqualTo(ConsignmentItemStatus.SOLD);
        verify(orderRepository).save(order);
    }

    private OrderRequest orderRequest(PaymentMethod method, List<OrderItemRequest> items) {
        return OrderRequest.builder()
                .paymentMethod(method)
                .orderItemRequests(items)
                .discount(BigDecimal.ZERO)
                .build();
    }

    private OrderItemRequest itemRequest(Long productId) {
        return OrderItemRequest.builder().productId(productId).build();
    }

    private User user(Long id, Role role) {
        User user = User.builder().role(role).build();
        user.setId(id);
        return user;
    }

    private Order order(User customer, OrderStatus status) {
        Order order = Order.builder()
                .customer(customer)
                .recipientName("Customer")
                .recipientPhone("0900000000")
                .orderChannel(OrderChannel.ONLINE)
                .orderStatus(status)
                .build();
        order.setId(10L);
        return order;
    }

    private Payment payment(Order order, PaymentMethod method, PaymentStatus status) {
        return Payment.builder()
                .order(order)
                .amount(BigDecimal.valueOf(100_000))
                .paymentMethod(method)
                .paymentStatus(status)
                .build();
    }
}
