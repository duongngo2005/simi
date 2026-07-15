package com.ndd.simi_be.product.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductSummaryResponse {
    private Long id;
    private String name;
    private String brandName;
    private BigDecimal currentPrice;
    private String size;
    private String productCondition;
    private List<ProductImageResponse> productImageResponses;
}
