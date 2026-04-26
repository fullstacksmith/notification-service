package com.gila.notification_service.service;

import com.gila.notification_service.channel.ChannelFactory;
import com.gila.notification_service.channel.NotificationChannel;
import com.gila.notification_service.dto.UserDto;
import com.gila.notification_service.model.Message;
import com.gila.notification_service.model.NotificationLog;
import com.gila.notification_service.repository.NotificationLogRepository;
import com.gila.notification_service.util.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final UserService           userService;
    private final ChannelFactory        channelFactory;
    private final NotificationLogRepository logRepository;

    public void dispatch(Message message) {
        List<UserDto> subscribers = userService.findSubscribers(message.getCategoryName());

        if (subscribers.isEmpty()) {
            log.info("No subscribers for category={}", message.getCategoryName());
            return;
        }

        subscribers.stream()
                .flatMap(user -> user.channels().stream()
                        .map(channel -> Map.entry(user, channel)))
                .forEach(entry -> sendAndLog(entry.getKey(), message, entry.getValue()));
    }

    private void sendAndLog(UserDto user, Message message, String channelName) {
        try {
            NotificationChannel channel = channelFactory.getChannel(channelName);
            channel.send(user, message);
            persist(message, user, channelName, "SENT", null);
        } catch (Exception e) {
            log.warn("[{}] failed for userId={} — {}", channelName, user.id(), e.getMessage());
            persist(message, user, channelName, "FAILED", e.getMessage());
        }
    }

    private void persist(Message message, UserDto user, String channelName, String status, String error) {
        logRepository.save(new NotificationLog(
                message.getId(),
                user.id(),
                user.name(),
                message.getCategoryName(),
                channelName,
                message.getBody(),
                status,
                error
        ));
    }
}
