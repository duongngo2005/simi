package com.ndd.simi_be.ai.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiChatResponse {
    private String reply;
    private List<ProductSuggestionResponse> suggestedProducts;
}