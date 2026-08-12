package com.ndd.simi_be.consignment.mapper;

import com.ndd.simi_be.consignment.dto.response.SettlementResponse;
import com.ndd.simi_be.consignment.entity.Settlement;

public class SettlementMapper {
    public static SettlementResponse toSettlementResponse(Settlement settlement){
        return SettlementResponse.builder()
                .id(settlement.getId())
                .consignmentId(settlement.getConsignment().getId())
                .consignorId(settlement.getConsignment().getConsignor().getId())
                .processedById(settlement.getProcessedBy().getId())
                .totalSoldAmount(settlement.getTotalSoldAmount())
                .totalCommissionAmount(settlement.getTotalCommissionAmount())
                .netAmount(settlement.getNetAmount())
                .paymentMethod(settlement.getPaymentMethod())
                .bankName(settlement.getBankName())
                .accountHolder(settlement.getAccountHolder())
                .accountNumber(settlement.getAccountNumber())
                .proofImageUrl(settlement.getProofImageUrl())
                .settledAt(settlement.getSettledAt())
                .settlementItemResponses(
                        (settlement.getSettlementItems() == null || settlement.getSettlementItems().isEmpty())
                        ? null
                        : settlement.getSettlementItems().stream()
                                .map(ConsignmentItemMapper::toConsignmentItemResponse)
                                .toList()
                )
                .build();
    }
}
