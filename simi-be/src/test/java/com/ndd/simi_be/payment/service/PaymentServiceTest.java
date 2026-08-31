package com.ndd.simi_be.payment.service;

import com.ndd.simi_be.order.entity.Order;
import com.ndd.simi_be.order.entity.OrderItem;
import com.ndd.simi_be.order.enums.OrderStatus;
import com.ndd.simi_be.order.repository.OrderRepository;
import com.ndd.simi_be.payment.dto.PaymentVerifyResult;
import com.ndd.simi_be.payment.entity.Payment;
import com.ndd.simi_be.payment.enums.PaymentMethod;
import com.ndd.simi_be.payment.enums.PaymentStatus;
import com.ndd.simi_be.payment.enums.PaymentVerificationStatus;
import com.ndd.simi_be.payment.provider.vnpay.VnPayPaymentProvider;
import com.ndd.simi_be.payment.repository.PaymentRepository;
import com.ndd.simi_be.product.entity.Product;
import com.ndd.simi_be.product.enums.ProductStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock private PaymentRepository paymentRepository;
    @Mock private OrderRepository orderRepository;
    @Mock private VnPayPaymentProvider vnPayPaymentProvider;
    @Mock private ApplicationEventPublisher eventPublisher;

    @InjectMocks private PaymentService paymentService;

    @Test
    void successfulIpnMovesOrderToPackingButKeepsInventoryReservedUntilDelivery() {
        Product product = Product.builder().productStatus(ProductStatus.RESERVED).build();
        Order order = Order.builder().orderStatus(OrderStatus.PENDING_PAYMENT).build();
        order.setOrderItems(List.of(OrderItem.builder()
                .order(order)
                .product(product)
                .unitPrice(BigDecimal.valueOf(100_000))
                .build()));
        Payment payment = onlinePayment(order, LocalDateTime.now().plusMinutes(5));

        when(vnPayPaymentProvider.verifyCallback(any())).thenReturn(successfulVerification());
        when(paymentRepository.findByGatewayTransactionRefForUpdate("SIMI_PAY_1"))
                .thenReturn(Optional.of(payment));

        Map<String, String> response = paymentService.handleVnPayIpn(Map.of());

        assertThat(response).containsEntry("RspCode", "00");
        assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.PAID);
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.PACKING);
        assertThat(product.getProductStatus()).isEqualTo(ProductStatus.RESERVED);
        verify(orderRepository).save(order);
    }

    @Test
    void lateSuccessfulIpnDoesNotReactivateAnExpiredPaymentAttempt() {
        Order order = Order.builder().orderStatus(OrderStatus.PENDING_PAYMENT).build();
        Payment payment = onlinePayment(order, LocalDateTime.now().minusMinutes(1));

        when(vnPayPaymentProvider.verifyCallback(any())).thenReturn(successfulVerification());
        when(paymentRepository.findByGatewayTransactionRefForUpdate("SIMI_PAY_1"))
                .thenReturn(Optional.of(payment));

        Map<String, String> response = paymentService.handleVnPayIpn(Map.of());

        assertThat(response).containsEntry("RspCode", "02");
        assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.PENDING_PAYMENT);
        verify(orderRepository, never()).save(order);
    }

    private Payment onlinePayment(Order order, LocalDateTime expiresAt) {
        return Payment.builder()
                .order(order)
                .amount(BigDecimal.valueOf(100_000))
                .paymentMethod(PaymentMethod.ONLINE)
                .paymentStatus(PaymentStatus.PENDING)
                .gatewayTransactionRef("SIMI_PAY_1")
                .expiresAt(expiresAt)
                .build();
    }

    private PaymentVerifyResult successfulVerification() {
        return PaymentVerifyResult.builder()
                .isValidChecksum(true)
                .verificationStatus(PaymentVerificationStatus.SUCCESS)
                .gatewayTxnRef("SIMI_PAY_1")
                .gatewayTransactionNo("123456")
                .gatewayBankCode("NCB")
                .gatewayResponseCode("00")
                .gatewayTransactionStatus("00")
                .amount(100_000)
                .build();
    }
}
