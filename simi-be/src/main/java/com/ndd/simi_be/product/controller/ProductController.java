package com.ndd.simi_be.product.controller;

import com.ndd.simi_be.common.response.ApiResponse;
import com.ndd.simi_be.product.dto.request.ProductFilterRequest;
import com.ndd.simi_be.product.dto.response.ProductSummaryResponse;
import com.ndd.simi_be.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProductSummaryResponse>>> getSearchedProducts(
            @Valid @ModelAttribute ProductFilterRequest filterRequest
    ){
        ApiResponse<Page<ProductSummaryResponse>> response
                = ApiResponse.<Page<ProductSummaryResponse>>builder()
                .body(productService.searchProducts(filterRequest))
                    .build();

        return ResponseEntity.ok(response);
    }
}
