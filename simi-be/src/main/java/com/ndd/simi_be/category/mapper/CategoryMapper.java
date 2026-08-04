package com.ndd.simi_be.category.mapper;

import com.ndd.simi_be.category.dto.CategoryResponse;
import com.ndd.simi_be.category.dto.CategoryTreeResponse;
import com.ndd.simi_be.category.entity.Category;

import java.util.ArrayList;

public class CategoryMapper {
    public static CategoryTreeResponse toCategoryTreeResponse(Category category){
        return CategoryTreeResponse.builder()
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
                        : category.getChildren().stream().map(CategoryMapper::toCategoryTreeResponse).toList()
                )
                .active(category.isActive())
                .build();
    }

    public static CategoryResponse toCategoryResponse(Category category){
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .parentId(
                        category.getParent() == null
                                ? null
                                : category.getParent().getId()
                )
                .active(category.isActive())
                .slug(category.getSlug())
                .build();
    }
}
