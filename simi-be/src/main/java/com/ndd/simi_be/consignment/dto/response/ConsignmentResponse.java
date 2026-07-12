package com.ndd.simi_be.consignment.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class ConsignmentResponse {
    private Long id;
    private Long consignorId;
    private Long receivedBy;
    private LocalDateTime startDate;
    private LocalDateTime expiryDate;
    private int totalItem;
    private int soldItem;
    private String note;
    private String status;
}
