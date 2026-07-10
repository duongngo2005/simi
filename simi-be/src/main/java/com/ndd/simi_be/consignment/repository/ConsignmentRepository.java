package com.ndd.simi_be.consignment.repository;

import com.ndd.simi_be.consignment.entity.Consignment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsignmentRepository extends JpaRepository<Consignment, Long> {
}
