package com.ndd.simi_be.consignment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ConsignmentFullDetailResponse {
    private ConsignmentResponse consignmentResponse;
    private List<ConsignmentItemResponse> consignmentItemResponses;
}
