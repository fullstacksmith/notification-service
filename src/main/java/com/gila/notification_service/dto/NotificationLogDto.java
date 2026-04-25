package com.gila.notification_service.dto;

public record NotificationLogDto(
    Long        id,
    String      userName,
    String      categoryName,
    String      channelName,
    String      messageBody,
    String      status,
    String      errorDetail,
    Instant     sentAt
) {}