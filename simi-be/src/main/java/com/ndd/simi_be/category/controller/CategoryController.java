package com.ndd.simi_be.category.controller;

import com.ndd.simi_be.category.dto.CategoryResponse;
import com.ndd.simi_be.category.dto.CategoryTreeResponse;
import com.ndd.simi_be.category.dto.CreateCategoryRequest;
import com.ndd.simi_be.category.dto.UpdateCategoryRequest;
import com.ndd.simi_be.category.service.CategoryService;
import com.ndd.simi_be.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CategoryTreeResponse>> createCategory(
            @Valid @RequestBody CreateCategoryRequest request
    ){
        ApiResponse<CategoryTreeResponse> apiResponse = ApiResponse.<CategoryTreeResponse>builder()
                .body(categoryService.createCategory(request))
                .status(201)
                .message("Tạo danh mục thành công")
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CategoryTreeResponse>> updateCategory(
            @Valid @RequestBody UpdateCategoryRequest request,
            @PathVariable Long id
    ){
        ApiResponse<CategoryTreeResponse> apiResponse = ApiResponse.<CategoryTreeResponse>builder()
                .body(categoryService.updateCategory(request, id))
                .message("Cập nhật danh mục thành công")
                .status(200)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/tree")
    public ResponseEntity<ApiResponse<List<CategoryTreeResponse>>> getCategoryTree(){
        ApiResponse<List<CategoryTreeResponse>> apiResponse
                = ApiResponse.<List<CategoryTreeResponse>>builder()
                .body(categoryService.getCategoryTree())
                .status(200)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories(){
        ApiResponse<List<CategoryResponse>> apiResponse
                = ApiResponse.<List<CategoryResponse>>builder()
                .body(categoryService.getAllCategories())
                .status(200)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> softDeleteCategory(@PathVariable Long id){
        categoryService.softDeleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
