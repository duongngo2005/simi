package com.ndd.simi_be.location.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "provinces")
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@Builder
public class Province {
    @Id
    @Column(length = 20)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String fullName;
}
