package com.ndd.simi_be.category.dto;

import lombok.Getter;

@Getter
public class UpdateCategoryRequest {
    private String name;
    private String slug;
    private Long parentId;
    private Boolean active;
}
