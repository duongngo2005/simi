package com.ndd.simi_be.product.dto.request;

import com.ndd.simi_be.product.enums.ProductCondition;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductFilterRequest {
    private String keyword;
    private Long categoryId;
    private String categorySlug;
    private Long brandId;
    private String sizeProduct;
    private String color;
    private ProductCondition productCondition;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    @Pattern(
            regexp = "createdDate|name|currentPrice",
            message = "Chỉ được sắp xếp theo giá, tên và ngày đăng"
    )
    private String sortBy = "createdDate";
    @Pattern(
            regexp = "desc|asc",
            message = "Chỉ được sắp xếp tăng dần hoặc giảm dần"
    )
    private String sortDir = "desc";
    private int size = 10;
    private int page = 0;
}