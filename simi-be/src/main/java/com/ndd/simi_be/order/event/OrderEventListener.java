package com.ndd.simi_be.order.event;

import com.ndd.simi_be.email.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventListener {
    private final EmailService emailService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderCreated(OrderCreatedEvent event){
        log.info("Transaction commit - gửi email xác nhận đơn hàng #{}",
                event.getEmailData().getOrderId());
        emailService.sendOrderConfirmationEmail(event.getEmailData());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderPaid(OrderPaidEvent event){
        log.info("Transaction commit — gửi email xác nhận thanh toán cho đơn hàng #{}",
                event.getEmailData().getOrderId());
        emailService.sendOrderConfirmationEmail(event.getEmailData());
    }
}
