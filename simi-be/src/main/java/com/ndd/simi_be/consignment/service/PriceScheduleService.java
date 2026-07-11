package com.ndd.simi_be.consignment.service;

import com.ndd.simi_be.common.exception.BadRequestException;
import com.ndd.simi_be.consignment.dto.request.PriceScheduleRequest;
import com.ndd.simi_be.consignment.entity.ConsignmentItem;
import com.ndd.simi_be.consignment.entity.PriceSchedule;
import com.ndd.simi_be.consignment.repository.PriceScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PriceScheduleService {
    private final PriceScheduleRepository priceScheduleRepository;

    public PriceSchedule createPriceSchedule(
            PriceScheduleRequest request,
            ConsignmentItem consignmentItem
    ){
        return PriceSchedule.builder()
                .price(request.getPrice())
                .effectiveAfterDays(request.getEffectiveAfterDays())
                .consignmentItem(consignmentItem)
                .build();
    }

    @Transactional
    public List<PriceSchedule> createListPriceSchedule(
            List<PriceScheduleRequest> schedules,
            ConsignmentItem consignmentItem
    ){
        if (schedules == null || schedules.isEmpty()){
            throw new BadRequestException("Lịch giá không được để trống");
        }

        List<PriceScheduleRequest> sortedRequest = schedules.stream()
                .sorted(Comparator.comparingInt(PriceScheduleRequest::getEffectiveAfterDays))
                .toList();

        if (sortedRequest.getFirst().getEffectiveAfterDays() != 0){
            throw new BadRequestException("Lịch giá đầu tiên phải là sau 0 ngày");
        }

        int size = sortedRequest.size();

        for (int i = 0; i < size - 1; i++){
            PriceScheduleRequest current = sortedRequest.get(i);
            PriceScheduleRequest next = sortedRequest.get(i + 1);
            if (current.getPrice().compareTo(next.getPrice()) < 0){
                throw new BadRequestException("Lần giảm giá sau giá không được cao hơn lần trước");
            }
            if (current.getEffectiveAfterDays() == next.getEffectiveAfterDays()){
                throw new BadRequestException("Không được cấu hình trùng ngày giảm giá");
            }
        }

        List<PriceSchedule> priceSchedules = new ArrayList<>();

        for (PriceScheduleRequest request : sortedRequest){
            PriceSchedule schedule = createPriceSchedule(request, consignmentItem);
            priceSchedules.add(schedule);
        }

        return priceScheduleRepository.saveAll(priceSchedules);
    }
}
