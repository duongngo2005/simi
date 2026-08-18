package com.ndd.simi_be.consignment.controller;

import com.ndd.simi_be.common.response.ApiResponse;
import com.ndd.simi_be.consignment.dto.request.ConfirmDonateItemDispositionsRequest;
import com.ndd.simi_be.consignment.dto.request.ItemDispositionFilterRequest;
import com.ndd.simi_be.consignment.dto.request.UpdateItemDispositionsTypeRequest;
import com.ndd.simi_be.consignment.dto.response.ItemDispositionResponse;
import com.ndd.simi_be.consignment.entity.ItemDisposition;
import com.ndd.simi_be.consignment.service.ItemDispositionService;
import com.ndd.simi_be.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/item-dispositions")
public class ItemDispositionController {
    private final ItemDispositionService itemDispositionService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ItemDispositionResponse>>> searchItemDisposition(
            @Valid @ModelAttribute ItemDispositionFilterRequest filterRequest
    ){
        ApiResponse<Page<ItemDispositionResponse>> response =
                ApiResponse.<Page<ItemDispositionResponse>>builder()
                        .status(200)
                        .body(itemDispositionService.searchItemDisposition(filterRequest))
                        .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/consignment/{consignmentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<ItemDispositionResponse>>> getAllItemDispositionByConsignmentId(
            @PathVariable("consignmentId") Long consignmentId
    ){
        ApiResponse<List<ItemDispositionResponse>> response = ApiResponse.<List<ItemDispositionResponse>>builder()
                .status(200)
                .body(itemDispositionService.getAllItemDispositionByConsignmentId(consignmentId))
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-item-disposition/{consignmentId}")
    public ResponseEntity<ApiResponse<List<ItemDispositionResponse>>> getMyItemDispositionsByConsignmentId(
            @PathVariable("consignmentId") Long consignmentId,
            @AuthenticationPrincipal User user
    ){
        ApiResponse<List<ItemDispositionResponse>> response = ApiResponse.<List<ItemDispositionResponse>>builder()
                .status(200)
                .body(itemDispositionService.getMyItemDispositionsByConsignmentId(consignmentId, user))
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<ItemDispositionResponse>> getItemDisposition(
            @PathVariable Long id
    ){
        ApiResponse<ItemDispositionResponse> response = ApiResponse.<ItemDispositionResponse>builder()
                .status(200)
                .body(itemDispositionService.getItemDisposition(id))
                .build();
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/updateType")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<Void> updateItemDispositionsType(
            @RequestBody UpdateItemDispositionsTypeRequest request,
            @AuthenticationPrincipal User user
    ){
        itemDispositionService.updateItemDispositionsType(user, request);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/confirm-donate")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<Void> confirmDonateItemDispositions(
            @RequestBody ConfirmDonateItemDispositionsRequest request,
            @AuthenticationPrincipal User user
    ){
        itemDispositionService.confirmDonateItemDispositions(request, user);
        return ResponseEntity.noContent().build();
    }
}
