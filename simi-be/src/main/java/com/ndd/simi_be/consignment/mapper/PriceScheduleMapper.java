package com.ndd.simi_be.consignment.mapper;

import com.ndd.simi_be.consignment.dto.response.PriceScheduleResponse;
import com.ndd.simi_be.consignment.entity.PriceSchedule;

public class PriceScheduleMapper {
    public static PriceScheduleResponse toPriceScheduleResponse(PriceSchedule schedule){
        return PriceScheduleResponse.builder()
                .price(schedule.getPrice())
                .effectiveAfterDays(schedule.getEffectiveAfterDays())
                .id(schedule.getId())
                .status(schedule.getPriceScheduleStatus().name())
                .appliedAt(schedule.getAppliedAt())
                .build();
    }
}
