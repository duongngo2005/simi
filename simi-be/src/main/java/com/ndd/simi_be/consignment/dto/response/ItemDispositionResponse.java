package com.ndd.simi_be.consignment.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ItemDispositionResponse {
    private Long id;
    private String type;
    private String status;
    private String productName;
    private Long consignmentId;
    private String consignorName;
    private String consignorPhone;
    private LocalDateTime pickupDeadline;
}
