package com.ndd.simi_be.consignment.mapper;

import com.ndd.simi_be.consignment.dto.response.ItemDispositionResponse;
import com.ndd.simi_be.consignment.entity.ItemDisposition;

public class ItemDispositionMapper {
    public static ItemDispositionResponse toItemDispositionResponse(ItemDisposition itemDisposition){
        return ItemDispositionResponse.builder()
                .note(itemDisposition.getNote())
                .id(itemDisposition.getId())
                .type(itemDisposition.getItemDispositionType().name())
                .status(itemDisposition.getItemDispositionStatus().name())
                .pickupDeadline(itemDisposition.getPickupDeadline())
                .consentedAt(itemDisposition.getConsentedAt())
                .processedAt(itemDisposition.getProcessedAt())
                .processedBy(itemDisposition.getProcessedBy().getId())
                .build();
    }
}
