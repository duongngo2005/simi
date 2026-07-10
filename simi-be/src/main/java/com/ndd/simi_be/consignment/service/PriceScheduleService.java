package com.ndd.simi_be.consignment.service;

import com.ndd.simi_be.common.exception.BadRequestException;
import com.ndd.simi_be.consignment.dto.request.PriceScheduleRequest;
import com.ndd.simi_be.consignment.entity.ConsignmentItem;
import com.ndd.simi_be.consignment.entity.PriceSchedule;
import com.ndd.simi_be.consignment.repository.PriceScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PriceScheduleService {
    private final PriceScheduleRepository priceScheduleRepository;

    @Transactional
    public PriceSchedule createPriceSchedule(
            PriceScheduleRequest request,
            ConsignmentItem consignmentItem
    ){
        PriceSchedule priceSchedule = PriceSchedule.builder()
                .price(request.getPrice())
                .effectiveAfterDays(request.getEffectiveAfterDays())
                .sequenceNo(request.getSequenceNo())
                .consignmentItem(consignmentItem)
                .build();
        return priceScheduleRepository.save(priceSchedule);
    }

    public void validatePriceSchedule(List<PriceSchedule> schedules){
        if (schedules == null || schedules.size() < 2){
            return;
        }

        boolean hasDuplicate = schedules.stream()
                .map(PriceSchedule::getSequenceNo)
                .distinct()
                .count() != schedules.size();

        if (hasDuplicate){
            throw new BadRequestException("Các bước giá không được trùng thứ tự");
        }

        List<PriceSchedule> sorted =
                schedules.stream().sorted(Comparator.comparingInt(PriceSchedule::getSequenceNo))
                        .toList();

        for (int i = 0; i < sorted.size() - 1; i++){
            PriceSchedule current = sorted.get(i);
            PriceSchedule next = sorted.get(i + 1);
            if (current.getPrice().compareTo(next.getPrice()) < 0){
                throw new BadRequestException(
                        "Giá ở bước %d (%s) không được lớn hơn giá ở bước %d (%s)".formatted(
                                next.getSequenceNo(), next.getPrice(),
                                current.getSequenceNo(), current.getPrice()
                        )
                );
            }
        }
    }
}
