package com.ndd.simi_be.payment.dto;

import com.ndd.simi_be.payment.enums.PaymentMethod;
import com.ndd.simi_be.payment.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private Long id;
    private Long orderId;
    private PaymentStatus paymentStatus;
    private BigDecimal amount;
    private LocalDateTime paidAt;
    private PaymentMethod paymentMethod;
}
