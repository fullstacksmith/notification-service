package com.gila.notification_service.repository;

import com.gila.notification_service.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    Optional<Message> findByIdempotencyKey(String idempotencyKey);
}
