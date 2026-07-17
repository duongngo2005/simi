package com.ndd.simi_be.payment.dto;

import com.ndd.simi_be.payment.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class PaymentRequest {
    @NotNull(message = "Không được để trống mã đơn hàng")
    private Long orderId;
    @NotNull(message = "Không được để trống phương thức thanh toán")
    private PaymentMethod paymentMethod;
}
