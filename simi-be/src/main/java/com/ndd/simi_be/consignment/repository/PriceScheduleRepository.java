package com.ndd.simi_be.consignment.repository;

import com.ndd.simi_be.consignment.entity.PriceSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PriceScheduleRepository extends JpaRepository<PriceSchedule, Long> {
}
