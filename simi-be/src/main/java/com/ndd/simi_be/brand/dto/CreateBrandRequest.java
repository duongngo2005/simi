package com.ndd.simi_be.brand.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CreateBrandRequest {
    @NotBlank(message = "Tên brand không được để trống")
    private String name;
    private String description;
}
