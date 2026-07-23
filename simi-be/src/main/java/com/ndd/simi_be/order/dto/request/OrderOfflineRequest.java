package com.ndd.simi_be.order.dto.request;

import com.ndd.simi_be.payment.enums.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Builder
@Data
public class OrderOfflineRequest {
    @NotNull(message = "Chi tiết đơn hàng không được thiếu")
    private List<OrderItemRequest> orderItemRequests;

    @NotBlank(message = "Bạn chưa nhập số điện thoại")
    private String recipientPhone;
    @NotBlank(message = "Bạn chưa nhập tên")
    private String recipientName;
    @Builder.Default
    private BigDecimal discount = BigDecimal.ZERO;

    @NotNull(message = "Bạn chưa chọn phương thức thanh toán")
    private PaymentMethod paymentMethod;
}
