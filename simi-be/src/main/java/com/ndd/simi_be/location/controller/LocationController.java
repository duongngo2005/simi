package com.ndd.simi_be.location.controller;

import com.ndd.simi_be.common.response.ApiResponse;
import com.ndd.simi_be.location.dto.ProvinceResponse;
import com.ndd.simi_be.location.dto.WardResponse;
import com.ndd.simi_be.location.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/provinces")
public class LocationController {

    private final LocationService service;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProvinceResponse>>> getAllProvinces(){
        ApiResponse<List<ProvinceResponse>> response = ApiResponse.<List<ProvinceResponse>>builder()
                .status(200)
                .body(service.getAllProvinces())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{provinceCode}/{wards}")
    public ResponseEntity<ApiResponse<List<WardResponse>>> getALlWardsByProvinceCode(
            @PathVariable("provinceCode") String provinceCode
    ){
        ApiResponse<List<WardResponse>> response = ApiResponse.<List<WardResponse>>builder()
                .body(service.getAllWardsByProvince(provinceCode))
                .status(200)
                .build();

        return ResponseEntity.ok(response);
    }
}
