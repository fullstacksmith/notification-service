package com.gila.notification_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateMessageDto(
        @NotNull
        Long   categoryId,
        @NotBlank
        String body,
        @Size(max = 64)
        @Pattern(regexp = "^[a-zA-Z0-9-_]{1,64}$")
        String idempotencyKey
) {}