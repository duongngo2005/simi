package com.ndd.simi_be.cart.dto;

import com.ndd.simi_be.product.dto.response.ProductImageResponse;
import com.ndd.simi_be.product.dto.response.ProductSummaryResponse;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class CartItemResponse {
    private Long id;
    private ProductSummaryResponse productSummaryResponse;
    private Long cartId;
}
