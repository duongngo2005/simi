package com.ndd.simi_be.location.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class WardResponse {
    private String code;
    private String fullName;
    private String name;
}
