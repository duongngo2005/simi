package com.ndd.simi_be.consignment.mapper;

import com.ndd.simi_be.consignment.dto.response.ConsignmentFullDetailResponse;
import com.ndd.simi_be.consignment.dto.response.ConsignmentItemResponse;
import com.ndd.simi_be.consignment.dto.response.ConsignmentResponse;
import com.ndd.simi_be.consignment.entity.Consignment;
import com.ndd.simi_be.consignment.entity.ConsignmentItem;
import com.ndd.simi_be.consignment.enums.ConsignmentItemStatus;

public class ConsignmentMapper {
    public static ConsignmentResponse toConsignmentResponse(Consignment consignment){
        return ConsignmentResponse.builder()
                .id(consignment.getId())
                .receivedBy(consignment.getReceivedBy().getId())
                .consignorId(consignment.getConsignor().getId())
                .startDate(consignment.getStartDate())
                .expiryDate(consignment.getExpiryDate())
                .note(consignment.getNote())
                .status(consignment.getConsignmentStatus().name())
                .totalItem(
                        consignment.getConsignmentItems().size()
                )
                .soldItem(
                        consignment.getConsignmentItems().stream().filter(
                                c -> c.getConsignmentItemStatus() == ConsignmentItemStatus.SOLD
                        ).toList().size()
                )
                .build();
    }

    public static ConsignmentFullDetailResponse toConsignmentFullDetailResponse(Consignment consignment){
        return ConsignmentFullDetailResponse.builder()
                .consignmentResponse(toConsignmentResponse(consignment))
                .consignmentItemResponses(
                        consignment.getConsignmentItems().stream()
                                .map(ConsignmentItemMapper::toConsignmentItemResponse)
                                .toList()
                )
                .build();
    }
}
