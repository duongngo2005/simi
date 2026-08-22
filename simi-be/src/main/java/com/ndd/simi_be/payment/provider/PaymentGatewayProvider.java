package com.ndd.simi_be.payment.provider;

import com.ndd.simi_be.payment.dto.PaymentVerifyResult;
import com.ndd.simi_be.payment.entity.Payment;
import com.ndd.simi_be.payment.enums.PaymentProvider;

import java.util.Map;

public interface PaymentGatewayProvider {
    PaymentProvider getProviderType();
    String createPaymentUrl(Payment payment, String ipAddress, String orderInfo);
    PaymentVerifyResult verifyCallback(Map<String, String> params);
}
