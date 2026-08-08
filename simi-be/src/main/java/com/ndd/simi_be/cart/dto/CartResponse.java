package com.ndd.simi_be.cart.dto;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Builder
@Data
public class CartResponse {
    private long id;
    private int totalItem;
    @Builder.Default
    private List<CartItemResponse> cartItemResponses = new ArrayList<>();
}
