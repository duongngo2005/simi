package com.ndd.simi_be.consignment.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CreateConsignmentRequest {
    @NotNull(message = "Khách hàng không được để trống")
    private Long consignorId;
    private String note;
}
