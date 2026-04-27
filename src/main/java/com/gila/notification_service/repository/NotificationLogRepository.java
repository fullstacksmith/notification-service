package com.gila.notification_service.repository;

import com.gila.notification_service.model.NotificationLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationLogRepository extends JpaRepository<NotificationLog, Long> {

    @Query("SELECT n FROM NotificationLog n WHERE " +
           "(:category IS NULL OR n.category = :category) AND " +
           "(:channel IS NULL OR n.channel = :channel) " +
           "ORDER BY n.sentAt DESC")
    Page<NotificationLog> findByFilters(
            @Param("category") String category,
            @Param("channel") String channel,
            Pageable pageable);
}
