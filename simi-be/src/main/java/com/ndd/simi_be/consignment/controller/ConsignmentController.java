package com.ndd.simi_be.consignment.controller;

import com.ndd.simi_be.common.response.ApiResponse;
import com.ndd.simi_be.consignment.dto.request.ConsignmentItemRequest;
import com.ndd.simi_be.consignment.dto.request.ConsignmentRequest;
import com.ndd.simi_be.consignment.dto.response.ConsignmentItemResponse;
import com.ndd.simi_be.consignment.dto.response.ConsignmentResponse;
import com.ndd.simi_be.consignment.service.ConsignmentService;
import com.ndd.simi_be.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/consignments")
public class ConsignmentController {

    private final ConsignmentService consignmentService;

    @PostMapping
    public ResponseEntity<ApiResponse<ConsignmentResponse>> createConsignment(
            @Valid @RequestBody ConsignmentRequest request,
            @AuthenticationPrincipal User user
    ){
        ApiResponse<ConsignmentResponse> response = ApiResponse.<ConsignmentResponse>builder()
                .status(201)
                .message("Tạo lô hàng thành công")
                .body(consignmentService.createConsignment(request, user.getId()))
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{id}")
    public ResponseEntity<ApiResponse<ConsignmentItemResponse>> createConsignmentItem(
            @Valid @RequestPart("data")ConsignmentItemRequest request,
            @RequestPart(value = "thumbnail")MultipartFile thumbnail,
            @RequestPart(value = "images", required = false)List<MultipartFile> images,
            @PathVariable("id") Long consignmentId
    ){
        ApiResponse<ConsignmentItemResponse> response =
                ApiResponse.<ConsignmentItemResponse>builder()
                        .message("Tạo item thành công")
                        .status(201)
                        .body(consignmentService.createConsignmentItem(
                                request, thumbnail, images, consignmentId
                        ))
                        .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ConsignmentResponse>>> getAllConsignments(){
        ApiResponse<List<ConsignmentResponse>> response =
                ApiResponse.<List<ConsignmentResponse>>builder()
                        .status(200)
                        .body(consignmentService.getAllConsignments())
                        .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/active")
    public ResponseEntity<ApiResponse<ConsignmentResponse>> activeConsignment(
            @PathVariable("id") Long consignmentId
    ){
        ApiResponse<ConsignmentResponse> response = ApiResponse.<ConsignmentResponse>builder()
                .status(200)
                .message("Kích hoạt lô hàng thành công")
                .body(consignmentService.activeConsignment(consignmentId))
                .build();
        return ResponseEntity.ok(response);
    }
}
