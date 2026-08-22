package com.ndd.simi_be.payment.repository;

import com.ndd.simi_be.payment.entity.Payment;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByOrderId(Long orderId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Payment p WHERE p.gatewayTransactionRef = :ref")
    Optional<Payment> findByGatewayTransactionRefForUpdate(@Param("ref") String ref);

    Optional<Payment> findByGatewayTransactionRef(String ref);
}
