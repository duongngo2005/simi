package com.ndd.simi_be.order.event;

import com.ndd.simi_be.dashboard.service.StaffNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class StaffNotificationEventListener {
    private final StaffNotificationService staffNotificationService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOrderCreated(StaffOrderCreatedEvent event) {
        staffNotificationService.createOrderNotifications(event.orderId());
    }
}