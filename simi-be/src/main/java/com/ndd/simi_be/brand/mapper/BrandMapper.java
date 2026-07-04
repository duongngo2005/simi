package com.ndd.simi_be.brand.mapper;

import com.ndd.simi_be.brand.dto.BrandResponse;
import com.ndd.simi_be.brand.entity.Brand;

public final class BrandMapper {
    public static BrandResponse toBrandResponse(Brand brand){
        return BrandResponse.builder()
                .id(brand.getId())
                .name(brand.getName())
                .slug(brand.getSlug())
                .active(brand.isActive())
                .description(brand.getDescription())
                .build();
    }
}
