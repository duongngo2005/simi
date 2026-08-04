package com.ndd.simi_be.consignment.repository;

import com.ndd.simi_be.consignment.entity.Consignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ConsignmentRepository extends JpaRepository<Consignment, Long>, JpaSpecificationExecutor<Consignment> {
}
