package com.ndd.simi_be.cart.entity;

import com.ndd.simi_be.common.entity.BaseEntity;
import com.ndd.simi_be.product.entity.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "cart_items")
public class CartItem extends BaseEntity {

    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Cart cart;

    @JoinColumn(nullable = false)
    @ManyToOne
    private Product product;
}
