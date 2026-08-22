package com.ndd.simi_be.order.enums;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public enum OrderStatus {
    PENDING,
    PENDING_PAYMENT,
    PACKING,
    SHIPPING,
    COMPLETED,
    CANCELLED,
    EXPIRED;

    private static final Map<OrderStatus, Set<OrderStatus>> TRANSITIONS = new EnumMap<>(OrderStatus.class);

    static {
        TRANSITIONS.put(PENDING, EnumSet.of(PACKING, CANCELLED));
        TRANSITIONS.put(PENDING_PAYMENT, EnumSet.of(PACKING, EXPIRED, CANCELLED));
        TRANSITIONS.put(PACKING, EnumSet.of(SHIPPING, CANCELLED));
        TRANSITIONS.put(SHIPPING, EnumSet.of(COMPLETED, CANCELLED));
        TRANSITIONS.put(EXPIRED, EnumSet.noneOf(OrderStatus.class));
        TRANSITIONS.put(COMPLETED, EnumSet.noneOf(OrderStatus.class));
        TRANSITIONS.put(CANCELLED, EnumSet.noneOf(OrderStatus.class));
    }

    public boolean canTransitionTo(OrderStatus target){
        return TRANSITIONS.getOrDefault(this, EnumSet.noneOf(OrderStatus.class)).contains(target);
    }
}
