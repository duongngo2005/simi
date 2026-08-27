package com.ndd.simi_be.consignment.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ProcessItemDispositionsRequest {
    @NotEmpty
    private List<@NotNull Long> itemDispositionIds;
}