package com.ndd.simi_be.order.enums;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrderStatusTest {

    @Test
    void pendingAllowsOnlyPackingOrCancellation() {
        assertThat(OrderStatus.PENDING.canTransitionTo(OrderStatus.PACKING)).isTrue();
        assertThat(OrderStatus.PENDING.canTransitionTo(OrderStatus.CANCELLED)).isTrue();
        assertThat(OrderStatus.PENDING.canTransitionTo(OrderStatus.SHIPPING)).isFalse();
        assertThat(OrderStatus.PENDING.canTransitionTo(OrderStatus.COMPLETED)).isFalse();
    }

    @Test
    void pendingPaymentAllowsOnlyExpiryOrCancellation() {
        assertThat(OrderStatus.PENDING_PAYMENT.canTransitionTo(OrderStatus.EXPIRED)).isTrue();
        assertThat(OrderStatus.PENDING_PAYMENT.canTransitionTo(OrderStatus.CANCELLED)).isTrue();
        assertThat(OrderStatus.PENDING_PAYMENT.canTransitionTo(OrderStatus.PACKING)).isFalse();
        assertThat(OrderStatus.PENDING_PAYMENT.canTransitionTo(OrderStatus.COMPLETED)).isFalse();
    }

    @Test
    void packingAllowsOnlyShipping() {
        assertThat(OrderStatus.PACKING.canTransitionTo(OrderStatus.SHIPPING)).isTrue();
        assertThat(OrderStatus.PACKING.canTransitionTo(OrderStatus.COMPLETED)).isFalse();
        assertThat(OrderStatus.PACKING.canTransitionTo(OrderStatus.CANCELLED)).isFalse();
    }

    @Test
    void shippingAllowsOnlyCompletion() {
        assertThat(OrderStatus.SHIPPING.canTransitionTo(OrderStatus.COMPLETED)).isTrue();
        assertThat(OrderStatus.SHIPPING.canTransitionTo(OrderStatus.PACKING)).isFalse();
        assertThat(OrderStatus.SHIPPING.canTransitionTo(OrderStatus.CANCELLED)).isFalse();
    }

    @Test
    void completedIsTerminal() {
        assertThat(OrderStatus.COMPLETED.canTransitionTo(OrderStatus.PENDING)).isFalse();
        assertThat(OrderStatus.COMPLETED.canTransitionTo(OrderStatus.CANCELLED)).isFalse();
    }

    @Test
    void cancelledIsTerminal() {
        assertThat(OrderStatus.CANCELLED.canTransitionTo(OrderStatus.PENDING)).isFalse();
        assertThat(OrderStatus.CANCELLED.canTransitionTo(OrderStatus.PACKING)).isFalse();
    }

    @Test
    void expiredIsTerminal() {
        assertThat(OrderStatus.EXPIRED.canTransitionTo(OrderStatus.PENDING_PAYMENT)).isFalse();
        assertThat(OrderStatus.EXPIRED.canTransitionTo(OrderStatus.PACKING)).isFalse();
    }
}
