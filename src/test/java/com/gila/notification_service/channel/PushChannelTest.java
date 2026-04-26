package com.gila.notification_service.channel;

import com.gila.notification_service.dto.UserDto;
import com.gila.notification_service.model.Message;
import com.gila.notification_service.util.ChannelType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class PushChannelTest {

    private final PushChannel channel = new PushChannel();

    @Test
    void getType_returnsPushNotification() {
        assertThat(channel.getType()).isEqualTo(ChannelType.PUSH_NOTIFICATION);
    }

    @Test
    void send_doesNotThrow() {
        UserDto user = new UserDto(3L, "Carol", "carol@test.com", "+1122334455",
                List.of("MOVIES"), List.of("PUSH_NOTIFICATION"));
        Message message = new Message("MOVIES", "New release tonight!", null);

        assertThatCode(() -> channel.send(user, message)).doesNotThrowAnyException();
    }
}
