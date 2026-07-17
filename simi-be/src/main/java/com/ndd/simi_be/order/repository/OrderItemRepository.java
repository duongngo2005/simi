package com.ndd.simi_be.order.repository;

import com.ndd.simi_be.order.entity.OrderItem;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    @EntityGraph(attributePaths = "product")
    List<OrderItem> findAllByOrderId(Long orderId);
}
