package com.gila.notification_service.dto;


import java.util.List;

public record UserDto(
        Long id,
        String name,
        String email,
        String phoneNumber,
        List<String> subscribed, // Categorías: FINANCE, SPORTS, MOVIES
        List<String> channels    // Canales: SMS, EMAIL, PUSH_NOTIFICATION
) {}