package com.ndd.simi_be.consignment.entity;

import com.ndd.simi_be.common.entity.BaseEntity;
import com.ndd.simi_be.consignment.enums.ItemDispositionStatus;
import com.ndd.simi_be.consignment.enums.ItemDispositionType;
import com.ndd.simi_be.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "item_dispositions")
public class ItemDisposition extends BaseEntity {
    @OneToOne(optional = false)
    @JoinColumn(name = "consignment_item_id", nullable = false, unique = true)
    private ConsignmentItem consignmentItem;

    @Enumerated(EnumType.STRING)
    private ItemDispositionType itemDispositionType;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ItemDispositionStatus itemDispositionStatus = ItemDispositionStatus.PENDING;

    private LocalDateTime pickupDeadline;
    private LocalDateTime consentedAt;
    private LocalDateTime processedAt;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "processed_by")
    private User processedBy;

    @Column(columnDefinition = "TEXT")
    private String note;
}
