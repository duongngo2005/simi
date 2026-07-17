package com.ndd.simi_be.order.controller;

import com.ndd.simi_be.common.response.ApiResponse;
import com.ndd.simi_be.order.dto.request.OrderRequest;
import com.ndd.simi_be.order.dto.response.OrderDetailResponse;
import com.ndd.simi_be.order.service.OrderService;
import com.ndd.simi_be.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/orders")
@RestController
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderDetailResponse>> createOrder(
            @Valid @RequestBody OrderRequest request,
            @AuthenticationPrincipal User user
    ){
        ApiResponse<OrderDetailResponse> response = ApiResponse.<OrderDetailResponse>builder()
                .message("Tạo đơn hàng thành công")
                .status(201)
                .body(orderService.createOrder(request, user))
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


}
