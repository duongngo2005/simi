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
    private LocalDateTime pickupDeadline;
    private LocalDateTime consentedAt;
    private LocalDateTime processedAt;
    private Long processedBy;
    private String note;
}
