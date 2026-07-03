package com.ndd.simi_be.category.mapper;

import com.ndd.simi_be.category.dto.CategoryResponse;
import com.ndd.simi_be.category.entity.Category;

import java.util.ArrayList;

public class CategoryMapper {
    public static CategoryResponse toCategoryResponse(Category category){
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .parentId(
                    category.getParent() == null
                        ? null
                        : category.getParent().getId()
                    )
                .children(
                    category.getChildren().isEmpty()
                        ? new ArrayList<>()
                        : category.getChildren().stream().map(CategoryMapper::toCategoryResponse).toList()
                )
                .active(category.isActive())
                .build();
    }
}
