package com.ndd.simi_be.order.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrderResponse {
    private OrderDetailResponse orderDetail;
    private String paymentUrl;
}
