package com.ndd.simi_be.order.repository;

import com.ndd.simi_be.order.entity.Order;
import com.ndd.simi_be.order.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    List<Order> findByOrderStatusAndReservationExpiresAtBefore(OrderStatus status, LocalDateTime now);
}
