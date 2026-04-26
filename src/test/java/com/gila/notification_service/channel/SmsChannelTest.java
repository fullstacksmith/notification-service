package com.gila.notification_service.channel;

import com.gila.notification_service.dto.UserDto;
import com.gila.notification_service.model.Message;
import com.gila.notification_service.util.ChannelType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class SmsChannelTest {

    private final SmsChannel channel = new SmsChannel();

    @Test
    void getType_returnsSms() {
        assertThat(channel.getType()).isEqualTo(ChannelType.SMS);
    }

    @Test
    void send_doesNotThrow() {
        UserDto user = new UserDto(1L, "Alice", "alice@test.com", "+1234567890",
                List.of("SPORTS"), List.of("SMS"));
        Message message = new Message("SPORTS", "Goal!", null);

        assertThatCode(() -> channel.send(user, message)).doesNotThrowAnyException();
    }
}
