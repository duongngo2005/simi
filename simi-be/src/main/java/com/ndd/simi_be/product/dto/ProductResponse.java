package com.ndd.simi_be.product.dto;

import com.ndd.simi_be.brand.dto.BrandResponse;
import com.ndd.simi_be.category.dto.CategoryResponse;
import com.ndd.simi_be.tag.dto.TagResponse;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private Long category;
    private Long brand;
    private BigDecimal currentPrice;
    private List<TagResponse> tagResponses;
    private String size;
    private String color;
    private String description;
    private String productCondition;
    private String status;
    private List<ProductImageResponse> productImageResponses;
}
