package com.ndd.simi_be.tag.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CreateTagRequest {
    @NotBlank(message = "Tên tag không được để trống")
    private String name;
}
