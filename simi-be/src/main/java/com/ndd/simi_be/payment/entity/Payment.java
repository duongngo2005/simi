package com.ndd.simi_be.payment.entity;

import com.ndd.simi_be.common.entity.BaseEntity;
import com.ndd.simi_be.order.entity.Order;
import com.ndd.simi_be.payment.enums.PaymentMethod;
import com.ndd.simi_be.payment.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Payment extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Order order;

    @Column(nullable = false, precision = 12, scale = 0)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;
    private LocalDateTime paidAt;
    private LocalDateTime refundedAt;
    private String transactionId;
}