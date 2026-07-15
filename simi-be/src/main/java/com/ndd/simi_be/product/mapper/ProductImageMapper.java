package com.ndd.simi_be.product.mapper;

import com.ndd.simi_be.product.dto.response.ProductImageResponse;
import com.ndd.simi_be.product.entity.ProductImage;

public class ProductImageMapper {
    public static ProductImageResponse toProductImageResponse(ProductImage productImage){
        return ProductImageResponse.builder()
                .imageUrl(productImage.getImageUrl())
                .thumbnail(productImage.isThumbnail())
                .build();
    }
}
