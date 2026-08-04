package com.ndd.simi_be.category.dto;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class CategoryResponse {
    private Long id;
    private String name;
    private String slug;
    private Long parentId;
    private boolean active;
}
