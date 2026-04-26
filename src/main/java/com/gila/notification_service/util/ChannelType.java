package com.gila.notification_service.util;

public enum ChannelType {
    EMAIL,
    SMS,
    PUSH_NOTIFICATION;

    public static ChannelType from(String type) {
        try {
            return ChannelType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid channel type: " + type);
        }
    }
}
