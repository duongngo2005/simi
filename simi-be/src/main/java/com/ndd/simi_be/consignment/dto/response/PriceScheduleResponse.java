package com.ndd.simi_be.consignment.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class PriceScheduleResponse {
    private Long id;
    private int effectiveAfterDays;
    private BigDecimal price;
    private LocalDateTime appliedAt;
    private String status;
}
