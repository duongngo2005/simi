package com.ndd.simi_be.dashboard.repository;

import com.ndd.simi_be.dashboard.entity.StaffNotification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StaffNotificationRepository extends JpaRepository<StaffNotification, Long> {

    long countByRecipient_IdAndReadAtIsNull(Long recipientId);

    List<StaffNotification> findByRecipient_IdAndReadAtIsNull(Long recipientId);

    @Query("""
        SELECT n
        FROM StaffNotification n
        JOIN FETCH n.order o
        WHERE n.recipient.id = :recipientId
        ORDER BY n.createdDate DESC
    """)
    List<StaffNotification> findLatestByRecipientId(
            @Param("recipientId") Long recipientId,
            Pageable pageable
    );
}