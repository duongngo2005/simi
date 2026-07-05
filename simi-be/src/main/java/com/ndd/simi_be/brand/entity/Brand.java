package com.ndd.simi_be.brand.entity;

import com.ndd.simi_be.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "brands")
public class Brand extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String name;
    @Column(nullable = false, unique = true)
    private String slug;
    @Column(columnDefinition = "TEXT")
    private String description;
    @Builder.Default
    private boolean active = true;
}
