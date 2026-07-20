package com.ndd.simi_be.order.entity;

import com.ndd.simi_be.common.entity.BaseEntity;
import com.ndd.simi_be.order.enums.OrderChannel;
import com.ndd.simi_be.order.enums.OrderStatus;
import com.ndd.simi_be.payment.entity.Payment;
import com.ndd.simi_be.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter @Setter
public class Order extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private User customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accepted_by")
    private User acceptedBy;

    // Khách vãng like
    @Column(nullable = false)
    private String recipientName;
    @Column(nullable = false)
    private String recipientPhone;

    private String ward;
    private String province;
    private String addressDetail;

    private BigDecimal subtotalAmount;

    private BigDecimal shippingFee;
    private BigDecimal discount;
    private BigDecimal finalAmount;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private OrderChannel orderChannel = OrderChannel.ONLINE;


    @Builder.Default
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus = OrderStatus.PENDING;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    @Builder.Default
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Payment> payments = new ArrayList<>();
}
