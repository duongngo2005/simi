package com.ndd.simi_be.consignment.dto.request;

import com.ndd.simi_be.consignment.enums.ConsignmentStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ConsignmentFilterRequest {
    private String keyword;
    private LocalDateTime startDateFrom;
    private LocalDateTime startDateTo;
    private LocalDateTime expiryDateFrom;
    private LocalDateTime expiryDateTo;
    private Boolean isExpiringSoon;
    private String consignmentStatus;
    private int page = 0;
    private int size = 10;
    private String sortBy = "createdDate";
    private String sortDir = "desc";

    public ConsignmentStatus parseConsignmentStatus(){
        if (consignmentStatus == null || consignmentStatus.isBlank()) return null;
        try{
            return ConsignmentStatus.valueOf(consignmentStatus.trim().toUpperCase());
        }catch (IllegalArgumentException e){
            return null;
        }
    }
}
