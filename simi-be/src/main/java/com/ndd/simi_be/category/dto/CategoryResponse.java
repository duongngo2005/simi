package com.ndd.simi_be.category.dto;

import lombok.*;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class CategoryResponse {
    private Long id;
    private String name;
    private String slug;
    private Long parentId;
    private List<CategoryResponse> children;
}
