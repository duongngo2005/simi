package com.ndd.simi_be.order.repository;

import com.ndd.simi_be.order.entity.Order;
import com.ndd.simi_be.order.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    List<Order> findByOrderStatusAndReservationExpiresAtBefore(
            OrderStatus status,
            LocalDateTime now
    );

    long countByCreatedDateGreaterThanEqualAndCreatedDateLessThan(
            LocalDateTime from,
            LocalDateTime to
    );

    @Query("""
        SELECT COALESCE(SUM(o.finalAmount), 0)
        FROM Order o
        WHERE o.orderStatus IN :statuses
    """)
    BigDecimal sumFinalAmountByStatuses(
            @Param("statuses") List<OrderStatus> statuses
    );

    @Query("""
        SELECT COALESCE(SUM(o.finalAmount), 0)
        FROM Order o
        WHERE o.orderStatus IN :statuses
          AND EXISTS (
              SELECT p
              FROM Payment p
              WHERE p.order = o
                AND p.paymentStatus = 'PAID'
          )
    """)
    BigDecimal sumPaidFinalAmountByStatuses(
            @Param("statuses") List<OrderStatus> statuses
    );

    @Query("""
        SELECT COALESCE(SUM(o.finalAmount), 0)
        FROM Order o
        WHERE o.orderStatus IN :statuses
          AND NOT EXISTS (
              SELECT p
              FROM Payment p
              WHERE p.order = o
                AND p.paymentStatus = 'PAID'
          )
    """)
    BigDecimal sumUnpaidFinalAmountByStatuses(
            @Param("statuses") List<OrderStatus> statuses
    );

    @Query("""
        SELECT COALESCE(SUM(o.finalAmount), 0)
        FROM Order o
        WHERE o.orderStatus = 'COMPLETED'
          AND o.completedAt >= :from
          AND o.completedAt < :to
    """)
    BigDecimal sumCompletedRevenueBetween(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );

    @Query("""
        SELECT COUNT(DISTINCT o)
        FROM Order o
        JOIN o.payments p
        WHERE o.orderStatus = 'PENDING'
          AND p.paymentMethod = 'COD'
          AND p.paymentStatus = 'PENDING'
    """)
    long countUnconfirmedCodOrders();

    @Query(value = """
        SELECT
            DATE(o.completed_at) AS completed_day,
            COALESCE(SUM(o.final_amount), 0) AS revenue,
            COUNT(*) AS completed_order_count
        FROM orders o
        WHERE o.order_status = 'COMPLETED'
          AND o.completed_at >= :from
          AND o.completed_at < :to
        GROUP BY DATE(o.completed_at)
        ORDER BY completed_day ASC
    """, nativeQuery = true)
    List<Object[]> findCompletedRevenueByDay(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );
}