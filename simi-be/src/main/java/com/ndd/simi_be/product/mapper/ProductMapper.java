package com.ndd.simi_be.product.mapper;

import com.ndd.simi_be.product.dto.ProductResponse;
import com.ndd.simi_be.product.entity.Product;
import com.ndd.simi_be.tag.mapper.TagMapper;

public class ProductMapper {
    public static ProductResponse toProductResponse(Product product){
        return ProductResponse.builder()
                .id(product.getId())
                .color(product.getColor())
                .name(product.getName())
                .size(product.getSize())
                .productCondition(product.getProductCondition().name())
                .category(product.getCategory().getId())
                .brand(
                        product.getBrand() == null
                        ? null
                        : product.getBrand().getId()
                )
                .description(product.getDescription())
                .status(product.getProductStatus().name())
                .productImageResponses(
                        product.getProductImages().stream().map(ProductImageMapper::toProductImageResponse).toList()
                )
                .tagResponses(
                        product.getTags().stream().map(TagMapper::toTagResponse).toList()
                )
                .currentPrice(product.getCurrentPrice())
                .build();
    }
}
