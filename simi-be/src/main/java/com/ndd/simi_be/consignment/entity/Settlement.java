package com.ndd.simi_be.consignment.entity;

import com.ndd.simi_be.common.entity.BaseEntity;
import com.ndd.simi_be.payment.enums.PaymentMethod;
import com.ndd.simi_be.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "settlements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Settlement extends BaseEntity {
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "consignment_id")
    private Consignment consignment;

    @JoinColumn(nullable = false, name = "processed_by_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User processedBy;

    @Column(precision = 12, scale = 0)
    private BigDecimal totalSoldAmount;
    @Column(precision = 12, scale = 0)
    private BigDecimal totalCommissionAmount;
    @Column(precision = 12, scale = 0)
    private BigDecimal netAmount;
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod = PaymentMethod.BANK_TRANSFER;
    private String bankName;
    private String accountNumber;
    private String accountHolder;

    @Column(nullable = false)
    private String proofImageUrl;

    @Builder.Default
    private LocalDateTime settledAt = LocalDateTime.now();

    @OneToMany(mappedBy = "settlement")
    @Builder.Default
    private List<ConsignmentItem> settlementItems = new ArrayList<>();
}
