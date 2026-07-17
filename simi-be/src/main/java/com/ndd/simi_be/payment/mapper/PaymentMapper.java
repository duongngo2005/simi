package com.ndd.simi_be.payment.mapper;

import com.ndd.simi_be.payment.dto.PaymentResponse;
import com.ndd.simi_be.payment.entity.Payment;

public class PaymentMapper {
    public static PaymentResponse toPaymentResponse(Payment payment){
        return PaymentResponse.builder()
                .paymentMethod(payment.getPaymentMethod())
                .paymentStatus(payment.getPaymentStatus())
                .amount(payment.getAmount())
                .id(payment.getId())
                .orderId(payment.getOrder().getId())
                .build();
    }
}
