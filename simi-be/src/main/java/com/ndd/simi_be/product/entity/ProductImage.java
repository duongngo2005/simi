package com.ndd.simi_be.product.entity;

import com.ndd.simi_be.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_images")
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductImage extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Product product;
    @Column(nullable = false)
    private String imageUrl;
    @Column(nullable = false)
    private String imagePublicId;

    @Builder.Default
    private boolean thumbnail = false;
}
