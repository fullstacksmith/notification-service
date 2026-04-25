package com.gila.notification_service.dto;

public record CreateMessageDto(
    @NotNull  Long   categoryId,
    @NotBlank String body
) {}
