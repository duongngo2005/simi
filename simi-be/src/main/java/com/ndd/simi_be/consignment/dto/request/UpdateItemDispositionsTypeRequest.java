package com.ndd.simi_be.consignment.dto.request;

import com.ndd.simi_be.consignment.enums.ItemDispositionType;
import lombok.Getter;

import java.util.List;

@Getter
public class UpdateItemDispositionsTypeRequest {
    private List<Long> itemDispositionIds;
    private ItemDispositionType type;
}
