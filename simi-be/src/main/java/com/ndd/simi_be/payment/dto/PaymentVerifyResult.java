package com.ndd.simi_be.payment.dto;

import com.ndd.simi_be.payment.enums.PaymentVerificationStatus;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentVerifyResult {
    private boolean isValidChecksum;
    private PaymentVerificationStatus verificationStatus;
    private String gatewayTxnRef;
    private String gatewayTransactionNo;
    private String gatewayBankCode;
    private String gatewayResponseCode;
    private String gatewayTransactionStatus;
    private long amount;
    private String rawMessage;
}
