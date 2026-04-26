package com.gila.notification_service.dto;

import com.gila.notification_service.model.Message;

import java.time.Instant;
import java.util.UUID;

public record MessageResponseDto(
        Long   messageId,
        String status            // SENT | DUPLICATE
) {
    public static MessageResponseDto fromCachedMessage(Message m) {
        return new MessageResponseDto(m.getId(), "DUPLICATE");
    }
}