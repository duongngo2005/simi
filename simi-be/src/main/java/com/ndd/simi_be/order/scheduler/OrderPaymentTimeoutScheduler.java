package com.ndd.simi_be.order.scheduler;

import com.ndd.simi_be.order.entity.Order;
import com.ndd.simi_be.order.enums.OrderStatus;
import com.ndd.simi_be.order.repository.OrderRepository;
import com.ndd.simi_be.order.service.OrderReservationReleaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderPaymentTimeoutScheduler {
    private final OrderRepository orderRepository;
    private final OrderReservationReleaseService reservationReleaseService;

    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void scanAndReleaseExpiredReservations(){
        LocalDateTime now = LocalDateTime.now();
        List<Order> expiredOrders = orderRepository.findByOrderStatusAndReservationExpiresAtBefore(
                OrderStatus.PENDING_PAYMENT, now
        );

        for (Order order : expiredOrders){
            if (order.getOrderStatus() != OrderStatus.PENDING_PAYMENT){
                continue;
            }

            log.info("Đơn hàng #{} quá hạn 30 phút → EXPIRED", order.getId());
            order.setOrderStatus(OrderStatus.EXPIRED);

            reservationReleaseService.release(order, now);
        }
    }
}
