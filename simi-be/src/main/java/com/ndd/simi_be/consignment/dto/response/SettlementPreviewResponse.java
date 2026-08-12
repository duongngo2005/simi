package com.ndd.simi_be.consignment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettlementPreviewResponse {
    private Long consignorId;
    private String consignorName;
    private String bankName;
    private String accountHolder;
    private String accountNumber;

    private BigDecimal totalSoldAmount;
    private BigDecimal totalCommissionAmount;
    private BigDecimal netAmount;
    private boolean canSettle;

    private int soldItemCount;
    private List<ConsignmentItemResponse> soldItems;
    private List<ConsignmentItemResponse> returnItems;
    private List<ConsignmentItemResponse> reserveItems;
}
