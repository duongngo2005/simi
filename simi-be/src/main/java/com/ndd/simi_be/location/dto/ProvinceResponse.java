package com.ndd.simi_be.location.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProvinceResponse {
    private String code;
    private String name;
    private String fullName;
}
