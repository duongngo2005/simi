package com.ndd.simi_be.consignment.service;

import com.ndd.simi_be.consignment.entity.PriceSchedule;
import com.ndd.simi_be.consignment.enums.ConsignmentItemStatus;
import com.ndd.simi_be.consignment.enums.PriceScheduleStatus;
import com.ndd.simi_be.consignment.repository.PriceScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

        LocalDateTime now = LocalDateTime.now();
        Map<Long, List<PriceSchedule>> schedulesByItem = priceSchedules.stream()
                .collect(Collectors.groupingBy(schedule -> schedule.getConsignmentItem().getId()));

        for (List<PriceSchedule> itemSchedules : schedulesByItem.values()) {
            List<PriceSchedule> eligibleSchedules = itemSchedules.stream()
                    .filter(schedule -> schedule.getConsignmentItem().getActivatedAt() != null)
                    .filter(schedule -> !schedule.getConsignmentItem().getActivatedAt()
                            .plusDays(schedule.getEffectiveAfterDays()).isAfter(now))
                    .toList();

            if (eligibleSchedules.isEmpty()) {
                continue;
            }

            PriceSchedule latestEligibleSchedule = eligibleSchedules.stream()
                    .max(Comparator.comparingInt(PriceSchedule::getEffectiveAfterDays))
                    .orElseThrow();

            for (PriceSchedule schedule : eligibleSchedules) {
                schedule.setPriceScheduleStatus(PriceScheduleStatus.APPLIED);
                schedule.setAppliedAt(now);
            }
            latestEligibleSchedule.getConsignmentItem().getProduct()
                    .setCurrentPrice(latestEligibleSchedule.getPrice());
        }
    }
}
