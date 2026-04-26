package com.gila.notification_service.channel;

import com.gila.notification_service.dto.UserDto;
import com.gila.notification_service.model.Message;
import com.gila.notification_service.util.ChannelType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EmailChannel implements NotificationChannel {

    private static final Logger log = LoggerFactory.getLogger(EmailChannel.class);

    @Override
    public void send(UserDto user, Message message) {
        log.info("[EMAIL] → {} | {}", user.email(), message.getBody());
    }

    @Override
    public ChannelType getType() {
        return ChannelType.EMAIL;
    }
}
