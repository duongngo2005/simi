package com.ndd.simi_be.consignment.dto.response;

import com.ndd.simi_be.product.dto.response.ProductDetailResponse;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class ConsignmentItemResponse {
    private Long id;
    private BigDecimal commissionRate;
    private String status;

    private List<PriceScheduleResponse> priceScheduleResponses;
    private ProductDetailResponse productDetailResponse;
    private ItemDispositionResponse itemDispositionResponse;
}
