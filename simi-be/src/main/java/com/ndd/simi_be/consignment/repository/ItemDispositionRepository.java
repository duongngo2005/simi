package com.ndd.simi_be.consignment.repository;

import com.ndd.simi_be.consignment.entity.ItemDisposition;
import com.ndd.simi_be.consignment.enums.ItemDispositionStatus;
import com.ndd.simi_be.consignment.enums.ItemDispositionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ItemDispositionRepository
        extends JpaRepository<ItemDisposition, Long>, JpaSpecificationExecutor<ItemDisposition> {

    List<ItemDisposition> findByConsignmentItem_Consignment_Id(Long consignmentId);

    @Query("""
        SELECT COUNT(d)
        FROM ItemDisposition d
        WHERE d.itemDispositionType = :type
          AND d.itemDispositionStatus = :status
    """)
    long countByTypeAndStatus(
            @Param("type") ItemDispositionType type,
            @Param("status") ItemDispositionStatus status
    );

    List<ItemDisposition>
    findByItemDispositionTypeAndItemDispositionStatusAndPickupDeadlineBefore(
            ItemDispositionType type,
            ItemDispositionStatus status,
            LocalDateTime deadline
    );
}