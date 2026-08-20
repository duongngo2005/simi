package com.ndd.simi_be.order.event;

import com.ndd.simi_be.email.dto.OrderConfirmationEmailData;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;


@Getter
public class OrderCreatedEvent extends ApplicationEvent {
    private final OrderConfirmationEmailData emailData;
    public OrderCreatedEvent(Object source, OrderConfirmationEmailData emailData) {
        super(source);
        this.emailData = emailData;
    }
}
