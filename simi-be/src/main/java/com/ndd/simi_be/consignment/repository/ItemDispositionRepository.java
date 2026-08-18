package com.ndd.simi_be.consignment.repository;

import com.ndd.simi_be.consignment.entity.ItemDisposition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ItemDispositionRepository extends JpaRepository<ItemDisposition, Long>, JpaSpecificationExecutor<ItemDisposition> {
    List<ItemDisposition> findByConsignmentItem_Consignment_Id(Long consignmentId);
}
