package com.ndd.simi_be.consignment.entity;

import com.ndd.simi_be.common.entity.BaseEntity;
import com.ndd.simi_be.consignment.enums.PriceScheduleStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "price_schedules")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class PriceSchedule extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "consignment_item_id")
    private ConsignmentItem consignmentItem;
    private int effectiveAfterDays;
    @Column(precision = 12, scale = 0)
    private BigDecimal price;
    private LocalDateTime appliedAt;
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private PriceScheduleStatus priceScheduleStatus = PriceScheduleStatus.PENDING;
    private int sequenceNo;
}
