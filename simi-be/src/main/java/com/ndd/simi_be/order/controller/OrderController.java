package com.ndd.simi_be.order.controller;

import com.ndd.simi_be.common.response.ApiResponse;
import com.ndd.simi_be.order.dto.request.OrderFilterRequest;
import com.ndd.simi_be.order.dto.request.CreatePosOrderRequest;
import com.ndd.simi_be.order.dto.request.OrderRequest;
import com.ndd.simi_be.order.dto.response.OrderDetailResponse;
import com.ndd.simi_be.order.dto.response.OrderSummaryResponse;
import com.ndd.simi_be.order.enums.OrderStatus;
import com.ndd.simi_be.order.service.OrderService;
import com.ndd.simi_be.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

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

    @GetMapping("/shipping-fee")
    public ResponseEntity<ApiResponse<BigDecimal>> estimateShippingFee(
            @RequestParam String provinceCode,
            @RequestParam BigDecimal subtotalAmount
    ){
        return ResponseEntity.ok(
                ApiResponse.<BigDecimal>builder()
                        .body(orderService.calculateShippingFee(provinceCode, subtotalAmount))
                        .build()
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<Page<OrderSummaryResponse>>> searchOrder(
            @Valid @ModelAttribute OrderFilterRequest filterRequest
    ) {
        ApiResponse<Page<OrderSummaryResponse>> response =
                ApiResponse.<Page<OrderSummaryResponse>>builder()
                        .status(200)
                        .body(orderService.searchOrder(filterRequest))
                        .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/pos")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<OrderDetailResponse>> createPosOrder(
            @RequestBody CreatePosOrderRequest request,
            @AuthenticationPrincipal User acceptedBy
    ){
        ApiResponse<OrderDetailResponse> response = ApiResponse.<OrderDetailResponse>builder()
                .message("Tạo đơn hàng thành công")
                .status(201)
                .body(orderService.createPosOrder(request, acceptedBy))
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<Void> changeOrderStatus(
            @PathVariable Long orderId,
            @RequestParam OrderStatus status
    ){
        orderService.changeStatus(status, orderId);
        return ResponseEntity.noContent().build();
    }
}
