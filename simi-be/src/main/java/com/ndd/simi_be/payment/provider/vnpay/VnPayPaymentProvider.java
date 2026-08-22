package com.ndd.simi_be.payment.provider.vnpay;

import com.ndd.simi_be.config.VnPayConfig;
import com.ndd.simi_be.payment.dto.PaymentVerifyResult;
import com.ndd.simi_be.payment.entity.Payment;
import com.ndd.simi_be.payment.enums.PaymentProvider;
import com.ndd.simi_be.payment.enums.PaymentVerificationStatus;
import com.ndd.simi_be.payment.provider.PaymentGatewayProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.HexFormat;
import java.util.Map;
import java.util.TreeMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class VnPayPaymentProvider implements PaymentGatewayProvider {

    private final VnPayConfig vnPayConfig;
    private static final DateTimeFormatter VNP_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    @Override
    public PaymentProvider getProviderType() {
        return PaymentProvider.VNPAY;
    }

    @Override
    public String createPaymentUrl(Payment payment, String ipAddress, String orderInfo) {
        if (payment.getGatewayTransactionRef() == null || payment.getGatewayTransactionRef().isBlank()){
            throw new IllegalStateException("Payment bắt buộc phải có gatewayTransactionRef được lưu trước khi tạo URL.");
        }

        Map<String, String> params = new TreeMap<>();
        params.put("vnp_Version", "2.1.0");
        params.put("vnp_Command", "pay");
        params.put("vnp_TmnCode", vnPayConfig.getTmnCode());
        params.put("vnp_Amount", String.valueOf(payment.getAmount().longValue() * 100L));
        params.put("vnp_CurrCode", "VND");
        params.put("vnp_TxnRef", payment.getGatewayTransactionRef());
        params.put("vnp_OrderInfo", orderInfo);
        params.put("vnp_OrderType", "other");
        params.put("vnp_Locale", "vn");
        params.put("vnp_ReturnUrl", vnPayConfig.getReturnUrl());
        params.put("vnp_IpAddr", ipAddress);
        params.put("vnp_CreateDate", payment.getGatewayCreatedAt().format(VNP_DATE_FORMAT));
        params.put("vnp_ExpireDate", payment.getExpiresAt().format(VNP_DATE_FORMAT));

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        for (Map.Entry<String, String> entry : params.entrySet()){
            if (entry.getValue() != null && !entry.getValue().isEmpty()){
                if (!hashData.isEmpty()){
                    hashData.append('&');
                    query.append('&');
                }

                String encodedValue = URLEncoder.encode(entry.getValue(), StandardCharsets.US_ASCII);
                hashData.append(entry.getKey()).append('=').append(encodedValue);
                query.append(URLEncoder.encode(entry.getKey(), StandardCharsets.US_ASCII))
                        .append('=')
                        .append(encodedValue);
            }
        }

        String secureHash = hmacSHA512(vnPayConfig.getHashSecret(), hashData.toString());
        query.append("&vnp_SecureHash=").append(secureHash);

        return vnPayConfig.getPayUrl() + "?" + query;
    }

    @Override
    public PaymentVerifyResult verifyCallback(Map<String, String> params) {
        String receivedHash = params.get("vnp_SecureHash");
        if (receivedHash == null || receivedHash.isBlank()){
            return PaymentVerifyResult.builder().isValidChecksum(false).build();
        }

        Map<String, String> filteredParams = new TreeMap<>(params);
        filteredParams.remove("vnp_SecureHash");
        filteredParams.remove("vnp_SecureHashType");

        StringBuilder hashData = new StringBuilder();
        for (Map.Entry<String, String> entry : filteredParams.entrySet()){
            if (entry.getValue() != null || !entry.getValue().isEmpty()){
                if (!hashData.isEmpty()){
                    hashData.append('&');
                }

                hashData.append(entry.getKey())
                        .append('=')
                        .append(URLEncoder.encode(entry.getValue(), StandardCharsets.US_ASCII));
            }
        }

        String calculatedHash = hmacSHA512(vnPayConfig.getHashSecret(), hashData.toString());
        boolean isValid = calculatedHash.equalsIgnoreCase(receivedHash);

        if (!isValid){
            return PaymentVerifyResult.builder().isValidChecksum(false).build();
        }

        String responseCode = params.get("vnp_ResponseCode");
        String transactionStatus = params.get("vnp_TransactionStatus");
        long amount = Long.parseLong(params.getOrDefault("vnp_Amount", "0")) / 100L;

        PaymentVerificationStatus status;
        if ("00".equals(responseCode) && "00".equals(transactionStatus)){
            status = PaymentVerificationStatus.SUCCESS;
        } else if ("01".equals(transactionStatus)){
            status = PaymentVerificationStatus.PENDING;
        } else if ("04".equals(transactionStatus) || "07".equals(transactionStatus) || "07".equals(responseCode)) {
            status = PaymentVerificationStatus.UNKNOWN;
        }else {
            status = PaymentVerificationStatus.FAILED;
        }

        return PaymentVerifyResult.builder()
                .isValidChecksum(true)
                .verificationStatus(status)
                .gatewayTxnRef(params.get("vnp_TxnRef"))
                .gatewayTransactionNo(params.get("vnp_TransactionNo"))
                .gatewayBankCode(params.get("vnp_BankCode"))
                .gatewayResponseCode(responseCode)
                .gatewayTransactionStatus(transactionStatus)
                .amount(amount)
                .rawMessage(params.get("vnp_OrderInfo"))
                .build();
    }

    private String hmacSHA512(String key, String data){
        try {
            Mac hmac512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            hmac512.init(secretKey);
            byte[] bytes = hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(bytes);
        }catch (Exception e){
            throw new RuntimeException("Lỗi mã hóa HMAC-SHA512", e);
        }
    }
}
