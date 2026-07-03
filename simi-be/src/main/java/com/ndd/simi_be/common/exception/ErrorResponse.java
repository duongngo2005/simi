package com.ndd.simi_be.common.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private int status;
    private String message;
    private String path;
    private Map<?, ?> errors;
    private LocalDateTime timestamp;
}
