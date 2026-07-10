package com.ndd.simi_be.consignment.repository;

import com.ndd.simi_be.consignment.entity.ConsignmentItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsignmentItemRepository extends JpaRepository<ConsignmentItem, Long> {
}
