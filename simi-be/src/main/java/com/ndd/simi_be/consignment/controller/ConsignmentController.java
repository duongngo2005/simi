package com.ndd.simi_be.consignment.controller;

import com.ndd.simi_be.common.response.ApiResponse;
import com.ndd.simi_be.consignment.dto.request.CreateConsignmentRequest;
import com.ndd.simi_be.consignment.dto.request.UpdateConsignmentRequest;
import com.ndd.simi_be.consignment.dto.response.ConsignmentResponse;
import com.ndd.simi_be.consignment.service.ConsignmentService;
import com.ndd.simi_be.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/consignments")
public class ConsignmentController {

    private final ConsignmentService consignmentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<ConsignmentResponse>> createConsignment(
            @Valid @RequestBody CreateConsignmentRequest request,
            @AuthenticationPrincipal User user
    ){
        ApiResponse<ConsignmentResponse> response = ApiResponse.<ConsignmentResponse>builder()
                .status(201)
                .message("Tạo lô hàng thành công")
                .body(consignmentService.createConsignment(request, user.getId()))
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<ConsignmentResponse>>> getAllConsignments(){
        ApiResponse<List<ConsignmentResponse>> response =
                ApiResponse.<List<ConsignmentResponse>>builder()
                        .status(200)
                        .body(consignmentService.getAllConsignments())
                        .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
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

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<ConsignmentResponse>> getConsignmentById(
            @PathVariable("id") Long consignmentId
    ){
        ApiResponse<ConsignmentResponse> response =
                ApiResponse.<ConsignmentResponse>builder()
                        .status(200)
                        .body(consignmentService.getConsignmentById(consignmentId))
                        .build();

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<ConsignmentResponse>> updateConsignment(
            @RequestBody UpdateConsignmentRequest request,
            @PathVariable("id") Long consignmentId
    ){
        ApiResponse<ConsignmentResponse> response =
                ApiResponse.<ConsignmentResponse>builder()
                        .status(200)
                        .message("Cập nhật lô hàng thành công")
                        .body(consignmentService.updateConsignment(request, consignmentId))
                        .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<Void> deleteConsignment(
            @PathVariable("id") Long consignmentId
    ){
        consignmentService.deleteConsignment(consignmentId);

        return ResponseEntity.noContent().build();
    }
}
