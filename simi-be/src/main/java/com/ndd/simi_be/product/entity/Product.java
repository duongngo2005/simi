package com.ndd.simi_be.product.entity;

import com.ndd.simi_be.brand.entity.Brand;
import com.ndd.simi_be.category.entity.Category;
import com.ndd.simi_be.common.entity.BaseEntity;
import com.ndd.simi_be.consignment.entity.ConsignmentItem;
import com.ndd.simi_be.product.enums.ProductCondition;
import com.ndd.simi_be.product.enums.ProductStatus;
import com.ndd.simi_be.tag.entity.Tag;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Product extends BaseEntity {
    @Column(nullable = false)
    private String name;
    private String size;
    @Column(columnDefinition = "TEXT")
    private String description;
    private String color;

    @Column(precision = 12, scale = 0)
    private BigDecimal currentPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    private Brand brand;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ProductCondition productCondition = ProductCondition.GOOD;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ProductStatus productStatus = ProductStatus.DRAFT;

    @Builder.Default
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> productImages = new ArrayList<>();

    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "product_tags",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<Tag> tags = new ArrayList<>();

    @OneToOne(mappedBy = "product")
    private ConsignmentItem consignmentItem;
}
