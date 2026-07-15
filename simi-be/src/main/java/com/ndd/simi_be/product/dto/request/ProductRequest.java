package com.ndd.simi_be.product.dto.request;

import com.ndd.simi_be.product.enums.ProductCondition;
import com.ndd.simi_be.product.enums.ProductStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {
    @NotBlank(message = "Tên sản phẩm không được để trống")
    private String name;
    @NotNull(message = "Danh mục không được để trống")
    private Long categoryId;
    private Long brandId;
    private String size;
    private String description;
    private String color;
    @Builder.Default
    private ProductCondition productCondition = ProductCondition.GOOD;
    @Builder.Default
    private ProductStatus productStatus = ProductStatus.DRAFT;
    @Builder.Default
    private List<String> tagNames = new ArrayList<>();
}
