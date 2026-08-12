package com.ndd.simi_be.order.repository;

import com.ndd.simi_be.order.entity.OrderItem;
import com.ndd.simi_be.order.enums.OrderStatus;
import com.ndd.simi_be.product.entity.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    @EntityGraph(attributePaths = "product")
    List<OrderItem> findAllByOrderId(Long orderId);

    Optional<OrderItem> findFirstByProductAndOrder_OrderStatus(Product product, OrderStatus orderStatus);
}
