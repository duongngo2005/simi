package com.ndd.simi_be.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse <T>{
    private int status;
    private String message;
    private T body;
}
