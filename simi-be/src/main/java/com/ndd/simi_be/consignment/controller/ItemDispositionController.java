package com.ndd.simi_be.consignment.controller;

import com.ndd.simi_be.common.response.ApiResponse;
import com.ndd.simi_be.consignment.dto.request.ItemDispositionFilterRequest;
import com.ndd.simi_be.consignment.dto.request.ProcessItemDispositionsRequest;
import com.ndd.simi_be.consignment.dto.response.ItemDispositionResponse;
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
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<Page<ItemDispositionResponse>>> searchItemDisposition(
            @Valid @ModelAttribute ItemDispositionFilterRequest filterRequest
    ) {
        return ResponseEntity.ok(
                ApiResponse.<Page<ItemDispositionResponse>>builder()
                        .status(200)
                        .body(itemDispositionService.searchItemDisposition(filterRequest))
                        .build()
        );
    }

    @GetMapping("/consignment/{consignmentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<ItemDispositionResponse>>> getAllByConsignmentId(
            @PathVariable Long consignmentId
    ) {
        return ResponseEntity.ok(
                ApiResponse.<List<ItemDispositionResponse>>builder()
                        .status(200)
                        .body(
                                itemDispositionService
                                        .getAllItemDispositionByConsignmentId(consignmentId)
                        )
                        .build()
        );
    }

    @PatchMapping("/confirm-return")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<Void> confirmReturn(
            @Valid @RequestBody ProcessItemDispositionsRequest request,
            @AuthenticationPrincipal User user
    ) {
        itemDispositionService.confirmReturn(request.getItemDispositionIds(), user);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/confirm-donation")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<Void> confirmDonation(
            @Valid @RequestBody ProcessItemDispositionsRequest request,
            @AuthenticationPrincipal User user
    ) {
        itemDispositionService.confirmDonation(request.getItemDispositionIds(), user);
        return ResponseEntity.noContent().build();
    }
}