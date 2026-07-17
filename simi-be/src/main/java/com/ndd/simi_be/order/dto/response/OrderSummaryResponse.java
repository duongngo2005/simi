package com.ndd.simi_be.order.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderSummaryResponse {
    private Long id;
    private String orderStatus;
    private BigDecimal finalAmount;
    private LocalDateTime createdDate;

    private String firstItemName;
    private String firstItemThumbnail;
    private int totalItem;
}
