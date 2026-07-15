package com.ndd.simi_be.location.mapper;

import com.ndd.simi_be.location.dto.ProvinceResponse;
import com.ndd.simi_be.location.dto.WardResponse;
import com.ndd.simi_be.location.entity.Province;
import com.ndd.simi_be.location.entity.Ward;

public class LocationMapper {
    public static ProvinceResponse toProvinceResponse(Province province){
        return ProvinceResponse.builder()
                .code(province.getCode())
                .name(province.getName())
                .fullName(province.getFullName())
                .build();
    }

    public static WardResponse toWardResponse(Ward ward){
        return WardResponse.builder()
                .code(ward.getCode())
                .name(ward.getName())
                .fullName(ward.getFullName())
                .build();
    }
}
