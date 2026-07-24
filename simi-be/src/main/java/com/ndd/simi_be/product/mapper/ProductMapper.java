package com.ndd.simi_be.product.mapper;

import com.ndd.simi_be.product.dto.response.ProductDetailResponse;
import com.ndd.simi_be.product.dto.response.ProductSummaryResponse;
import com.ndd.simi_be.product.entity.Product;
import com.ndd.simi_be.product.entity.ProductImage;
import com.ndd.simi_be.tag.mapper.TagMapper;

public class ProductMapper {
    public static ProductDetailResponse toProductResponse(Product product){
        return ProductDetailResponse.builder()
                .createdDate(product.getCreatedDate())
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

    public static ProductSummaryResponse toProductSummaryResponse(Product product){
        return ProductSummaryResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .size(product.getSize())
                .currentPrice(product.getCurrentPrice())
                .brandName(
                        product.getBrand() == null
                        ? null
                        : product.getBrand().getName()
                )
                .productCondition(product.getProductCondition().name())
                .productStatus(product.getProductStatus())
                .thumbnail(
                        product.getProductImages().stream()
                                .filter(ProductImage::isThumbnail)
                                .map(ProductImage::getImageUrl)
                                .findFirst()
                                .orElse(null)
                )
                .build();
    }
}
