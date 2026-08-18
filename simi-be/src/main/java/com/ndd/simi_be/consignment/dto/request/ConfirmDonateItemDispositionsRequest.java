package com.ndd.simi_be.consignment.dto.request;

import lombok.Getter;

import java.util.List;

@Getter
public class ConfirmDonateItemDispositionsRequest {
    private List<Long> itemDispositionIds;
}
