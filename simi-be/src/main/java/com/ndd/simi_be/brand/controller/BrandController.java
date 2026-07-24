package com.ndd.simi_be.brand.controller;

import com.ndd.simi_be.brand.dto.BrandResponse;
import com.ndd.simi_be.brand.dto.CreateBrandRequest;
import com.ndd.simi_be.brand.dto.UpdateBrandRequest;
import com.ndd.simi_be.brand.service.BrandService;
import com.ndd.simi_be.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/brands")
@RequiredArgsConstructor
public class BrandController {
    private final BrandService brandService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BrandResponse>> createBrand(
            @Valid @RequestBody CreateBrandRequest request
    ){
        ApiResponse<BrandResponse> response = ApiResponse.<BrandResponse>builder()
                .status(201)
                .message("Tạo brand thành công")
                .body(brandService.createBrand(request))
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BrandResponse>> updateBrand(
            @Valid @RequestBody UpdateBrandRequest request,
            @PathVariable Long id
    ){
        ApiResponse<BrandResponse> response = ApiResponse.<BrandResponse>builder()
                .status(200)
                .message("Update brand thành công")
                .body(brandService.updateBrand(request, id))
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BrandResponse>>> getAllBrands(){
        ApiResponse<List<BrandResponse>> response = ApiResponse.<List<BrandResponse>>builder()
                .status(200)
                .body(brandService.getAllBrands())
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> softDeleteBrand(@PathVariable Long id){
        brandService.softDeleteBrand(id);
        return ResponseEntity.noContent().build();
    }
}
