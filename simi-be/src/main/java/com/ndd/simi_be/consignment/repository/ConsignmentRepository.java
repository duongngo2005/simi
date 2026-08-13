package com.ndd.simi_be.consignment.repository;

import com.ndd.simi_be.consignment.entity.Consignment;
import com.ndd.simi_be.consignment.enums.ConsignmentStatus;
import com.ndd.simi_be.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;

public interface ConsignmentRepository extends JpaRepository<Consignment, Long>, JpaSpecificationExecutor<Consignment> {
    List<Consignment> findByConsignor(User consignor);
    List<Consignment> findByConsignmentStatusAndExpiryDateBefore(ConsignmentStatus status, LocalDateTime dateTime);
}
