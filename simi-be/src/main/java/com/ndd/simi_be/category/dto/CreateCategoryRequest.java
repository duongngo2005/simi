package com.ndd.simi_be.category.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CreateCategoryRequest {
    @NotBlank(message = "Tên danh mục không được để trống")
    private String name;
    private Long parentId;
}
