package com.gila.notification_service.repository;

import com.gila.notification_service.model.NotificationLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationLogRepository extends JpaRepository<NotificationLog, Long> {
    Page<NotificationLog> findAllByOrderBySentAtDesc(Pageable pageable);
}
