package com.ndd.simi_be.location.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "wards")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Ward {
    @Id
    @Column(length = 20)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, name = "province_code", length = 20)
    private String provinceCode;
}
