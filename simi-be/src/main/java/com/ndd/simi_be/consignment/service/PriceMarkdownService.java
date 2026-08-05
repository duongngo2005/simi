package com.ndd.simi_be.consignment.service;

import com.ndd.simi_be.consignment.entity.PriceSchedule;
import com.ndd.simi_be.consignment.enums.ConsignmentItemStatus;
import com.ndd.simi_be.consignment.enums.PriceScheduleStatus;
import com.ndd.simi_be.consignment.repository.PriceScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PriceMarkdownService {

    private final PriceScheduleRepository priceScheduleRepository;

    @Transactional
    public void applyPendingPriceSchedules(){
        List<PriceSchedule> priceSchedules
                =  priceScheduleRepository.findByPriceScheduleStatusAndConsignmentItem_ConsignmentItemStatus(
                        PriceScheduleStatus.PENDING, ConsignmentItemStatus.ACTIVE
        );

        for (PriceSchedule schedule : priceSchedules){
            LocalDateTime effectiveAt = schedule.getConsignmentItem()
                    .getActivatedAt()
                    .plusDays(schedule.getEffectiveAfterDays());
            if (effectiveAt.isBefore(LocalDateTime.now())){
                schedule.setPriceScheduleStatus(PriceScheduleStatus.APPLIED);
                schedule.setAppliedAt(LocalDateTime.now());
                schedule.getConsignmentItem().getProduct().setCurrentPrice(schedule.getPrice());
            }
        }
    }
}
