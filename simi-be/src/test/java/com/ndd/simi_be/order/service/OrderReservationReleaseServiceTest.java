package com.ndd.simi_be.order.service;

import com.ndd.simi_be.consignment.entity.Consignment;
import com.ndd.simi_be.consignment.entity.ConsignmentItem;
import com.ndd.simi_be.consignment.enums.ConsignmentItemStatus;
import com.ndd.simi_be.consignment.enums.ConsignmentStatus;
import com.ndd.simi_be.consignment.repository.ConsignmentItemRepository;
import com.ndd.simi_be.order.entity.Order;
import com.ndd.simi_be.order.entity.OrderItem;
import com.ndd.simi_be.payment.entity.Payment;
import com.ndd.simi_be.payment.enums.PaymentMethod;
import com.ndd.simi_be.payment.enums.PaymentStatus;
import com.ndd.simi_be.product.entity.Product;
import com.ndd.simi_be.product.enums.ProductStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderReservationReleaseServiceTest {

    @Mock private ConsignmentItemRepository consignmentItemRepository;
    @InjectMocks private OrderReservationReleaseService releaseService;

    @Test
    void releaseReturnsItemToAvailableOnlyWhileConsignmentIsStillActive() {
        LocalDateTime now = LocalDateTime.now();
        Consignment consignment = Consignment.builder()
                .consignmentStatus(ConsignmentStatus.ACTIVE)
                .expiryDate(now.plusDays(1))
                .build();
        Product product = Product.builder().productStatus(ProductStatus.RESERVED).build();
        ConsignmentItem consignmentItem = ConsignmentItem.builder()
                .consignment(consignment)
                .product(product)
                .consignmentItemStatus(ConsignmentItemStatus.RESERVED)
                .build();
        Order order = orderWithReservedProduct(product);

        when(consignmentItemRepository.findByProduct(product)).thenReturn(Optional.of(consignmentItem));

        releaseService.release(order, now);

        assertThat(product.getProductStatus()).isEqualTo(ProductStatus.AVAILABLE);
        assertThat(consignmentItem.getConsignmentItemStatus()).isEqualTo(ConsignmentItemStatus.ACTIVE);
        assertThat(order.getPayments().getFirst().getPaymentStatus()).isEqualTo(PaymentStatus.CANCELLED);
    }

    @Test
    void releaseExpiresItemWhenConsignmentIsNoLongerActive() {
        LocalDateTime now = LocalDateTime.now();
        Consignment consignment = Consignment.builder()
                .consignmentStatus(ConsignmentStatus.PENDING_SETTLEMENT)
                .expiryDate(now.minusDays(1))
                .build();
        Product product = Product.builder().productStatus(ProductStatus.RESERVED).build();
        ConsignmentItem consignmentItem = ConsignmentItem.builder()
                .consignment(consignment)
                .product(product)
                .consignmentItemStatus(ConsignmentItemStatus.RESERVED)
                .build();
        Order order = orderWithReservedProduct(product);

        when(consignmentItemRepository.findByProduct(product)).thenReturn(Optional.of(consignmentItem));

        releaseService.release(order, now);

        assertThat(product.getProductStatus()).isEqualTo(ProductStatus.EXPIRED);
        assertThat(consignmentItem.getConsignmentItemStatus()).isEqualTo(ConsignmentItemStatus.EXPIRED);
    }

    private Order orderWithReservedProduct(Product product) {
        Order order = Order.builder().build();
        order.setOrderItems(List.of(OrderItem.builder()
                .order(order)
                .product(product)
                .unitPrice(BigDecimal.valueOf(100_000))
                .build()));
        order.setPayments(List.of(Payment.builder()
                .order(order)
                .amount(BigDecimal.valueOf(100_000))
                .paymentMethod(PaymentMethod.ONLINE)
                .paymentStatus(PaymentStatus.PENDING)
                .build()));
        return order;
    }
}
