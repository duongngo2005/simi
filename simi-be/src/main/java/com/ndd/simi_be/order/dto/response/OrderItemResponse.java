package com.ndd.simi_be.order.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderItemResponse {
    private Long id;
    private String name;
    private String size;
    private String color;
    private String brand;
    private String thumbnail;
    private BigDecimal unitPrice;
}
