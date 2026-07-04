package com.ndd.simi_be.brand.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class UpdateBrandRequest {
    private String name;
    private String description;
    private Boolean active;
}
