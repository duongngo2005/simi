package com.ndd.simi_be.consignment.entity;

import com.ndd.simi_be.common.entity.BaseEntity;
import com.ndd.simi_be.consignment.enums.ConsignmentItemStatus;
import com.ndd.simi_be.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "consignment_items")
public class ConsignmentItem extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Consignment consignment;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    private Product product;

    @Column(nullable = false, precision = 3, scale = 2)
    private BigDecimal commissionRate;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private ConsignmentItemStatus consignmentItemStatus = ConsignmentItemStatus.DRAFT;

    @OneToOne(mappedBy = "consignmentItem")
    private ItemDisposition itemDisposition;

    @OneToMany(mappedBy = "consignmentItem")
    @Builder.Default
    private List<PriceSchedule> priceSchedules = new ArrayList<>();

    private LocalDateTime activatedAt;
}
