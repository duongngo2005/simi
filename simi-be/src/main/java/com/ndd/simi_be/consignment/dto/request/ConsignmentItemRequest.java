package com.ndd.simi_be.consignment.dto.request;
import com.ndd.simi_be.product.dto.request.ProductRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
public class ConsignmentItemRequest {
    @DecimalMin(value = "0.10", message = "Hoa hồng không được bé hơn 10%")
    @DecimalMax(value = "0.60", message = "Hoa hồng không được lớn hơn 60%")
    private BigDecimal commissionRate = new BigDecimal("0.30");

    @Valid
    @NotNull
    private List<PriceScheduleRequest> priceScheduleRequests;

    @Valid
    @NotNull
    private ProductRequest productRequest;
}
