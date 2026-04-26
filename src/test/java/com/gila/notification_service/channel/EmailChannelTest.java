package com.gila.notification_service.channel;

import com.gila.notification_service.dto.UserDto;
import com.gila.notification_service.model.Message;
import com.gila.notification_service.util.ChannelType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class EmailChannelTest {

    private final EmailChannel channel = new EmailChannel();

    @Test
    void getType_returnsEmail() {
        assertThat(channel.getType()).isEqualTo(ChannelType.EMAIL);
    }

    @Test
    void send_doesNotThrow() {
        UserDto user = new UserDto(2L, "Bob", "bob@test.com", "+0987654321",
                List.of("FINANCE"), List.of("EMAIL"));
        Message message = new Message("FINANCE", "Market update", null);

        assertThatCode(() -> channel.send(user, message)).doesNotThrowAnyException();
    }
}
