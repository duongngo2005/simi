package com.ndd.simi_be.order.dto.response;

import com.ndd.simi_be.payment.enums.PaymentMethod;
import com.ndd.simi_be.payment.enums.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailResponse {
    private Long id;
    private String acceptedByName;

    private String recipientName;
    private String recipientPhone;
    private String ward;
    private String province;
    private String addressDetail;
    private BigDecimal subtotalAmount;

    private BigDecimal shippingFee;
    private BigDecimal discount;
    private BigDecimal finalAmount;
    private String orderStatus;
    private String orderChannel;

    private LocalDateTime createdDate;
    private PaymentStatus paymentStatus;
    private PaymentMethod paymentMethod;

    private List<OrderItemResponse> orderItems;
}
