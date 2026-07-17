package com.ndd.simi_be.order.repository;

import com.ndd.simi_be.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

}
