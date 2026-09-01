package com.ndd.simi_be.consignment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class CreateConsignmentRequest {
    @NotBlank(message = "Số điện thoại khách hàng không được để trống")
    @Pattern(regexp = "^[0-9]{10,11}$", message = "Số điện thoại phải gồm 10 hoặc 11 chữ số")
    private String consignorPhone;
    private String note;
}
