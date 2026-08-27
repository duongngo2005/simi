package com.ndd.simi_be.consignment.repository;

import com.ndd.simi_be.consignment.entity.Settlement;
import com.ndd.simi_be.user.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SettlementRepository extends JpaRepository<Settlement, Long> {
    @EntityGraph(attributePaths = {"consignment", "consignment.consignor", "processedBy"})
    List<Settlement> findByConsignment_ConsignorOrderBySettledAtDesc(User user);
    Optional<Settlement> findByConsignmentId(Long consignmentId);
    @Query("""
        SELECT COALESCE(SUM(s.totalCommissionAmount), 0) 
        FROM Settlement s 
        WHERE s.settledAt >= :startOfMonth
    """)
    BigDecimal sumCommissionFromDate(@Param("startOfMonth") LocalDateTime startOfMonth);
}
