package com.ndd.simi_be.order.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@Builder
public class OrderItemRequest {
    @NotNull(message = "Bạn chưa chọn sản phẩm")
    private Long productId;
}
