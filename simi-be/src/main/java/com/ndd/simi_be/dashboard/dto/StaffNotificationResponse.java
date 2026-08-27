package com.ndd.simi_be.dashboard.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class StaffNotificationResponse {
    private Long id;
    private boolean read;
    private LocalDateTime createdDate;

    private Long orderId;
    private String recipientName;
    private String orderStatus;
    private String paymentMethod;
    private BigDecimal finalAmount;
}