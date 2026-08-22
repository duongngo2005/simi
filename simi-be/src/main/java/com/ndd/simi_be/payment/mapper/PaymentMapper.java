package com.ndd.simi_be.payment.mapper;

import com.ndd.simi_be.payment.dto.PaymentResponse;
import com.ndd.simi_be.payment.entity.Payment;

public class PaymentMapper {
    public static PaymentResponse toPaymentResponse(Payment payment){
        return PaymentResponse.builder()
                .id(payment.getId())
                .orderId(payment.getOrder().getId())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .paymentProvider(payment.getPaymentProvider())
                .paymentStatus(payment.getPaymentStatus())
                .gatewayTransactionRef(payment.getGatewayTransactionRef())
                .gatewayTransactionId(payment.getGatewayTransactionId())
                .gatewayBankCode(payment.getGatewayBankCode())
                .gatewayResponseCode(payment.getGatewayResponseCode())
                .gatewayCreatedAt(payment.getGatewayCreatedAt())
                .expiresAt(payment.getExpiresAt())
                .paidAt(payment.getPaidAt())
                .refundedAt(payment.getRefundedAt())
                .build();
    }
}
