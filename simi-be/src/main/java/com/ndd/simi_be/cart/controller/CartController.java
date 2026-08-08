package com.ndd.simi_be.cart.controller;

import com.ndd.simi_be.cart.dto.CartResponse;
import com.ndd.simi_be.cart.service.CartService;
import com.ndd.simi_be.common.response.ApiResponse;
import com.ndd.simi_be.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/carts")
public class CartController {
    private final CartService cartService;

    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> getMyCart(
            @AuthenticationPrincipal User user
    ){
        ApiResponse<CartResponse> response = ApiResponse.<CartResponse>builder()
                .body(cartService.getMyCart(user))
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/add/{id}")
    public ResponseEntity<ApiResponse<CartResponse>> addToCart(
            @AuthenticationPrincipal User user,
            @PathVariable("id") Long productId
    ){
        ApiResponse<CartResponse> response = ApiResponse.<CartResponse>builder()
                .body(cartService.addToCart(productId, user))
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/remove/{id}")
    public ResponseEntity<Void> removeItemFromCart(
            @AuthenticationPrincipal User user,
            @PathVariable("id") Long cartItemId
    ){
        cartService.removeItemFromCart(cartItemId, user);
        return ResponseEntity.noContent().build();
    }
}
