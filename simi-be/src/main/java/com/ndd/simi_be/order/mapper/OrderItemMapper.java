package com.ndd.simi_be.order.mapper;

import com.ndd.simi_be.order.dto.response.OrderItemResponse;
import com.ndd.simi_be.order.entity.OrderItem;
import com.ndd.simi_be.product.entity.ProductImage;

public class OrderItemMapper {
    public static OrderItemResponse toOrderItemResponse(OrderItem orderItem){
        return OrderItemResponse.builder()
                .id(orderItem.getId())
                .name(orderItem.getProduct().getName())
                .size(orderItem.getProduct().getSize())
                .brand(orderItem.getProduct().getBrand() == null ? null : orderItem.getProduct().getBrand().getName())
                .thumbnail(
                        orderItem.getProduct().getProductImages().stream()
                                .filter(ProductImage::isThumbnail)
                                .findFirst().isEmpty()
                                ? null
                                : orderItem.getProduct().getProductImages().stream()
                                .filter(ProductImage::isThumbnail)
                                .findFirst().get().getImageUrl()
                )
                .unitPrice(orderItem.getProduct().getCurrentPrice())
                .build();
    }
}
