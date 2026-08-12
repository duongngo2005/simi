package com.ndd.simi_be.consignment.dto.response;

import com.ndd.simi_be.payment.enums.PaymentMethod;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class SettlementResponse {
    private Long id;
    private Long consignmentId;
    private Long consignorId;
    private Long processedById;
    private BigDecimal totalSoldAmount;
    private BigDecimal totalCommissionAmount;
    private BigDecimal netAmount;
    private PaymentMethod paymentMethod;
    private String bankName;
    private String accountNumber;
    private String accountHolder;
    private String proofImageUrl;
    private LocalDateTime settledAt;

    @Builder.Default
    private List<ConsignmentItemResponse> settlementItemResponses = new ArrayList<>();
}
