package com.gila.notification_service.model;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Entity
@Table(name = "notification_log", indexes = {
        @Index(name = "idx_log_sent_at",    columnList = "sent_at DESC"),
        @Index(name = "idx_log_message_id", columnList = "message_id"),
        @Index(name = "idx_log_user_id",    columnList = "user_id")
})
@Getter
public class NotificationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "message_id", nullable = false)
    private Long messageId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "user_name", length = 100)
    private String userName;

    @Column(name = "category", length = 30)
    private String category;

    @Column(name = "channel", nullable = false, length = 30)
    private String channel;

    @Column(name = "status", nullable = false, length = 10)
    private String status;

    @Column(name = "message_body", columnDefinition = "TEXT")
    private String messageBody;

    @Column(name = "error_detail", columnDefinition = "TEXT")
    private String errorDetail;

    @Column(name = "sent_at", nullable = false, updatable = false)
    private LocalDateTime sentAt;

    @PrePersist
    protected void onCreate() {
        this.sentAt = LocalDateTime.now(ZoneOffset.UTC);
    }

    public NotificationLog() {}

    public NotificationLog(Long messageId, Long userId, String userName,
                           String category, String channel,
                           String messageBody, String status, String errorDetail) {
        this.messageId   = messageId;
        this.userId      = userId;
        this.userName    = userName;
        this.category    = category;
        this.channel     = channel;
        this.messageBody = messageBody;
        this.status      = status;
        this.errorDetail = errorDetail;
    }
}
