package com.ndd.simi_be.order.dto.request;

import com.ndd.simi_be.order.enums.OrderChannel;
import com.ndd.simi_be.order.enums.OrderStatus;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class OrderFilterRequest {
    private OrderStatus orderStatus;
    private OrderChannel orderChannel;
    private String keyword;
    @Min(0)
    private int page = 0;
    @Min(1)
    private int size = 10;

    private LocalDate fromDate;
    private LocalDate toDate;

    private String sortDir = "desc";
    private String sortBy = "createdDate";
}
