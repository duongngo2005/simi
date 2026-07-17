package com.ndd.simi_be.payment.repository;

import com.ndd.simi_be.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
