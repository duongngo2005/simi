package com.ndd.simi_be.consignment.controller;

import com.ndd.simi_be.common.response.ApiResponse;
import com.ndd.simi_be.consignment.dto.response.SettlementPreviewResponse;
import com.ndd.simi_be.consignment.dto.response.SettlementResponse;
import com.ndd.simi_be.consignment.service.SettlementService;
import com.ndd.simi_be.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/settlements")
public class SettlementController {
    private final SettlementService settlementService;

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @PostMapping(value = "/{id}")
    public ResponseEntity<ApiResponse<SettlementResponse>> createSettlement(
            @PathVariable("id") Long consignmentId,
            @AuthenticationPrincipal User processedBy,
            @RequestParam MultipartFile file
    ){
        ApiResponse<SettlementResponse> response =
                ApiResponse.<SettlementResponse>builder()
                        .status(201)
                        .body(settlementService.createSettlement(consignmentId, processedBy, file))
                        .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @GetMapping("/preview/{id}")
    public ResponseEntity<ApiResponse<SettlementPreviewResponse>> getPreviewSettlement(
            @PathVariable("id") Long consignmentId
    ){
        ApiResponse<SettlementPreviewResponse> response =
                ApiResponse.<SettlementPreviewResponse>builder()
                        .body(settlementService.previewResponse(consignmentId))
                        .status(200)
                        .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/consignment/{consignmentId}")
    public ResponseEntity<ApiResponse<SettlementResponse>> getSettlement(
            @PathVariable("consignmentId") Long consignmentId,
            @AuthenticationPrincipal User user
    ){
        ApiResponse<SettlementResponse> response = ApiResponse.<SettlementResponse>builder()
                .body(settlementService.getSettlement(consignmentId, user))
                .status(200)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-settlements")
    public ResponseEntity<ApiResponse<List<SettlementResponse>>> getMySettlements(
            @AuthenticationPrincipal User user
    ){
        ApiResponse<List<SettlementResponse>> response = ApiResponse.<List<SettlementResponse>>builder()
                .body(settlementService.getMySettlements(user))
                .status(200)
                .build();

        return ResponseEntity.ok(response);
    }
}
