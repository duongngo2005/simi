package com.ndd.simi_be.tag.entity;

import com.ndd.simi_be.common.entity.BaseEntity;
import com.ndd.simi_be.product.entity.Product;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tags")
public class Tag extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String name;
    @Column(nullable = false, unique = true)
    private String slug;
    @Builder.Default
    private boolean active = true;

    @ManyToMany(mappedBy = "tags")
    @Builder.Default
    private List<Product> products = new ArrayList<>();
}
