package com.ndd.simi_be.dashboard.entity;

import com.ndd.simi_be.common.entity.BaseEntity;
import com.ndd.simi_be.order.entity.Order;
import com.ndd.simi_be.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "staff_notifications",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_staff_notifications_recipient_order_type",
                columnNames = {"recipient_id", "order_id", "notification_type"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaffNotification extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "notification_type", nullable = false, length = 40)
    private String notificationType;

    @Column(name = "read_at")
    private LocalDateTime readAt;
}