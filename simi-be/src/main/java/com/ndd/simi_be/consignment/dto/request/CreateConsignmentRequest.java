package com.ndd.simi_be.consignment.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CreateConsignmentRequest {
    @NotNull(message = "Số điện thoại khách hàng không được để trống")
    private String consignorPhone;
    private String note;
}
