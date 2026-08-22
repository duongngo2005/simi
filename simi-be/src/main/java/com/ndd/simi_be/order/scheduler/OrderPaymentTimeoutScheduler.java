package com.ndd.simi_be.order.scheduler;

import com.ndd.simi_be.consignment.entity.Consignment;
import com.ndd.simi_be.consignment.entity.ConsignmentItem;
import com.ndd.simi_be.consignment.enums.ConsignmentItemStatus;
import com.ndd.simi_be.consignment.enums.ConsignmentStatus;
import com.ndd.simi_be.consignment.repository.ConsignmentItemRepository;
import com.ndd.simi_be.order.entity.Order;
import com.ndd.simi_be.order.entity.OrderItem;
import com.ndd.simi_be.order.enums.OrderStatus;
import com.ndd.simi_be.order.repository.OrderRepository;
import com.ndd.simi_be.payment.entity.Payment;
import com.ndd.simi_be.payment.enums.PaymentStatus;
import com.ndd.simi_be.product.entity.Product;
import com.ndd.simi_be.product.enums.ProductStatus;
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
    private final ConsignmentItemRepository consignmentItemRepository;

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

            for (OrderItem orderItem : order.getOrderItems()){
                Product product = orderItem.getProduct();
                ConsignmentItem consignmentItem = consignmentItemRepository
                        .findByProduct(product).orElse(null);

                if (consignmentItem != null){
                    Consignment consignment = consignmentItem.getConsignment();
                    if (consignment.getConsignmentStatus() == ConsignmentStatus.PENDING_SETTLEMENT){
                        product.setProductStatus(ProductStatus.EXPIRED);
                        consignmentItem.setConsignmentItemStatus(ConsignmentItemStatus.EXPIRED);
                    }else {
                        product.setProductStatus(ProductStatus.AVAILABLE);
                        consignmentItem.setConsignmentItemStatus(ConsignmentItemStatus.ACTIVE);
                    }
                }else {
                    product.setProductStatus(ProductStatus.AVAILABLE);
                }
            }

            for (Payment payment : order.getPayments()){
                if (payment.getPaymentStatus() == PaymentStatus.PENDING){
                    payment.setPaymentStatus(PaymentStatus.CANCELLED);
                }
            }
        }
    }
}
