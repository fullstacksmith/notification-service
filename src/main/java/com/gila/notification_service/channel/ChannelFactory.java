package com.gila.notification_service.channel;

import com.gila.notification_service.util.ChannelType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ChannelFactory {

    private final Map<ChannelType, NotificationChannel> channels;

    public ChannelFactory(List<NotificationChannel> channelList) {
        channels = channelList.stream()
                .collect(Collectors.toMap(NotificationChannel::getType, Function.identity()));
    }

    public NotificationChannel getChannel(String channelName) {
        ChannelType type = ChannelType.from(channelName);
        return Optional.ofNullable(channels.get(type))
                .orElseThrow(() -> new IllegalArgumentException("Canal no soportado: " + channelName));
    }
}
