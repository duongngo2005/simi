package com.ndd.simi_be.consignment.scheduler;

import com.ndd.simi_be.consignment.service.ConsignmentExpiryService;
import com.ndd.simi_be.consignment.service.ItemDispositionService;
import com.ndd.simi_be.consignment.service.PriceMarkdownService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsignmentScheduler {
    private final PriceMarkdownService priceMarkdownService;
    private final ConsignmentExpiryService consignmentExpiryService;
    private final ItemDispositionService itemDispositionService;

    @Scheduled(cron = "${app.scheduler.maintenance-cron}")
    public void runConsignmentMaintenance() {
        priceMarkdownService.applyPendingPriceSchedules();
        consignmentExpiryService.processExpiredConsignments();
        itemDispositionService.moveExpiredReturnsToDonation();
    }
}
