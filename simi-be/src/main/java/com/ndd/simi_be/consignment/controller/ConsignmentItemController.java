package com.ndd.simi_be.consignment.controller;

import com.ndd.simi_be.common.response.ApiResponse;
import com.ndd.simi_be.consignment.dto.request.ConsignmentItemRequest;
import com.ndd.simi_be.consignment.dto.response.ConsignmentFullDetailResponse;
import com.ndd.simi_be.consignment.dto.response.ConsignmentItemResponse;
import com.ndd.simi_be.consignment.service.ConsignmentItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/consignments")
public class ConsignmentItemController {

    private final ConsignmentItemService consignmentItemService;

    @PostMapping("/{id}/items")
    public ResponseEntity<ApiResponse<ConsignmentItemResponse>> createConsignmentItem(
            @Valid @RequestPart("data") ConsignmentItemRequest request,
            @RequestPart(value = "thumbnail") MultipartFile thumbnail,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @PathVariable("id") Long consignmentId
    ){
        ApiResponse<ConsignmentItemResponse> response =
                ApiResponse.<ConsignmentItemResponse>builder()
                        .message("Tạo item thành công")
                        .status(201)
                        .body(consignmentItemService.createConsignmentItem(
                                request, thumbnail, images, consignmentId
                        ))
                        .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}/items")
    public ResponseEntity<ApiResponse<ConsignmentFullDetailResponse>> getAllItemsByConsignmentId(
            @PathVariable("id") Long consignmentId
    ){
        ApiResponse<ConsignmentFullDetailResponse> response = ApiResponse.<ConsignmentFullDetailResponse>builder()
                .status(200)
                .body(consignmentItemService.getAllItemsByConsignmentId(consignmentId))
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{consignmentId}/items/{consignmentItemId}")
    public ResponseEntity<Void> hardDeleteConsignmentItem(
            @PathVariable Long consignmentId,
            @PathVariable Long consignmentItemId
    ){
        consignmentItemService.hardDeleteConsignmentItem(consignmentId, consignmentItemId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{consignmentId}/items/{consignmentItemId}/cancel")
    public ResponseEntity<ApiResponse<ConsignmentItemResponse>> softDeleteConsignmentItem(
            @PathVariable Long consignmentId,
            @PathVariable Long consignmentItemId
    ){
        ApiResponse<ConsignmentItemResponse> response = ApiResponse.<ConsignmentItemResponse>builder()
                .body(consignmentItemService.softDeleteConsignmentItem(consignmentId, consignmentItemId))
                .message("Xóa mềm thành công")
                .status(200)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/items/{consignmentItemId}")
    public ResponseEntity<ApiResponse<ConsignmentItemResponse>> getConsignmentItemById(
            @PathVariable("consignmentItemId") Long consignmentItemId
    ){
        ApiResponse<ConsignmentItemResponse> response
                = ApiResponse.<ConsignmentItemResponse>builder()
                .status(200)
                .body(consignmentItemService.getConsignmentItemById(consignmentItemId))
                .build();

        return ResponseEntity.ok(response);
    }
}
