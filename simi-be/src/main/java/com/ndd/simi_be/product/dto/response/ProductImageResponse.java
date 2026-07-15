package com.ndd.simi_be.product.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ProductImageResponse {
    private String imageUrl;
    private boolean thumbnail;
}
