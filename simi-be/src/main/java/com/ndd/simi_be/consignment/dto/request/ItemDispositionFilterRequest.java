package com.ndd.simi_be.consignment.dto.request;

import com.ndd.simi_be.consignment.enums.ItemDispositionStatus;
import com.ndd.simi_be.consignment.enums.ItemDispositionType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemDispositionFilterRequest {
    private String keyword;
    private Long consignmentId;
    private String status;
    private String type;
    private int page = 0;
    private int size = 10;
    private String sortBy = "createdDate";
    private String sortDir = "desc";

    public static ItemDispositionStatus parseItemDispositionStatus(String statusStr){
        if (statusStr == null || statusStr.isBlank()){
            return null;
        }
        try{
            return ItemDispositionStatus.valueOf(statusStr.trim().toUpperCase());
        }catch (IllegalArgumentException e){
            return null;
        }
    }

    public static ItemDispositionType parseItemDispositionType(String typeStr){
        if (typeStr == null || typeStr.isBlank()) return  null;
        try {
            return ItemDispositionType.valueOf(typeStr.trim().toUpperCase());
        }catch (IllegalArgumentException e){
            return null;
        }
    }
}
