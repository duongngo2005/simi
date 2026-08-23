package com.ndd.simi_be.ai.dto.request;

import com.ndd.simi_be.product.enums.Gender;
import com.ndd.simi_be.product.enums.ProductCondition;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiSearchFilter {
    @Builder.Default
    private List<Gender> genders = new ArrayList<>();
    @Builder.Default
    private List<String> colors = new ArrayList<>();
    @Builder.Default
    private List<String> sizes = new ArrayList<>();
    @Builder.Default
    private List<String> brandNames = new ArrayList<>();
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private ProductCondition condition;


    @Builder.Default
    private List<String> itemKeywords = new ArrayList<>();
    @Builder.Default
    private List<String> materials = new ArrayList<>();
    @Builder.Default
    private List<String> occasions = new ArrayList<>();
    @Builder.Default
    private List<String> styles = new ArrayList<>();
    @Builder.Default
    private List<String> feelings = new ArrayList<>();
    @Builder.Default
    private List<String> fits = new ArrayList<>();


    @Builder.Default
    private List<String> excludedKeywords = new ArrayList<>();
}