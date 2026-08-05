package com.ndd.simi_be.consignment.scheduler;

import com.ndd.simi_be.consignment.service.PriceMarkdownService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsignmentScheduler {

    private final PriceMarkdownService priceMarkdownService;

    @Scheduled(cron = "${app.scheduler.price-cron}")
    public void runDailyConsignmentMaintenance(){
        priceMarkdownService.applyPendingPriceSchedules();
    }
}
