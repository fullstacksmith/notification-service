package com.gila.notification_service.channel;

import com.gila.notification_service.dto.UserDto;
import com.gila.notification_service.model.Message;
import com.gila.notification_service.util.ChannelType;

public interface NotificationChannel {
    void send(UserDto user, Message message);
    ChannelType getType();
}
