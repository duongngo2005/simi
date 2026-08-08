package com.ndd.simi_be.cart.mapper;

import com.ndd.simi_be.cart.dto.CartItemResponse;
import com.ndd.simi_be.cart.dto.CartResponse;
import com.ndd.simi_be.cart.entity.Cart;
import com.ndd.simi_be.cart.entity.CartItem;
import com.ndd.simi_be.product.entity.ProductImage;
import com.ndd.simi_be.product.mapper.ProductImageMapper;
import com.ndd.simi_be.product.mapper.ProductMapper;

import java.util.Objects;

public class CartMapper {
    public static CartResponse toCartResponse(Cart cart){
        return CartResponse.builder()
                .cartItemResponses(
                        cart.getCartItems() == null
                        ? null
                        : cart.getCartItems().stream().map(CartMapper::toCartItemResponse).toList()
                )
                .totalItem(cart.getCartItems().size())
                .id(cart.getId())
                .build();
    }

    public static CartItemResponse toCartItemResponse(CartItem cartItem){
        return CartItemResponse.builder()
                .cartId(cartItem.getCart().getId())
                .id(cartItem.getId())
                .productSummaryResponse(ProductMapper.toProductSummaryResponse(cartItem.getProduct()))
                .build();
    }
}
