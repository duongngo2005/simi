package com.ndd.simi_be.consignment.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceScheduleRequest {
    @NotNull(message = "Số ngày áp dụng không được để trống")
    private int effectiveAfterDays;
    @NotNull(message = "Giá tiền không được để trống")
    private BigDecimal price;
}
