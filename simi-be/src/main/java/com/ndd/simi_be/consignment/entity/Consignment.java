package com.ndd.simi_be.consignment.entity;

import com.ndd.simi_be.common.entity.BaseEntity;
import com.ndd.simi_be.consignment.enums.ConsignmentStatus;
import com.ndd.simi_be.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Entity
@Table(name = "consignments")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Consignment extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "consignor_id", nullable = false)
    private User consignor;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "received_by_id", nullable = false)
    private User receivedBy;
    private LocalDateTime startDate;
    private LocalDateTime expiryDate;
    @Column(columnDefinition = "TEXT")
    private String note;
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private ConsignmentStatus consignmentStatus = ConsignmentStatus.DRAFT;
    private LocalDateTime settledAt;
    private LocalDateTime closedAt;

    @OneToMany(mappedBy = "consignment")
    @Builder.Default
    private List<ConsignmentItem> consignmentItems = new ArrayList<>();
}
