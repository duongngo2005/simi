package com.ndd.simi_be.consignment.repository;

import com.ndd.simi_be.consignment.entity.PriceSchedule;
import com.ndd.simi_be.consignment.enums.ConsignmentItemStatus;
import com.ndd.simi_be.consignment.enums.PriceScheduleStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
public interface PriceScheduleRepository extends JpaRepository<PriceSchedule, Long> {

    @EntityGraph(attributePaths = {
            "consignmentItem",
            "consignmentItem.product"
    })
    List<PriceSchedule> findByPriceScheduleStatusAndConsignmentItem_ConsignmentItemStatus(
            PriceScheduleStatus status, ConsignmentItemStatus consignmentItemStatus
    );
}
