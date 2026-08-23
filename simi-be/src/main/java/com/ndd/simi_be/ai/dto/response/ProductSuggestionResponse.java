package com.ndd.simi_be.ai.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSuggestionResponse {
    private Long id;
    private String name;
    private BigDecimal price;
    private String thumbnailUrl;
    private String brandName;
    private String size;
}