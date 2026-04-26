package com.gila.notification_service.channel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ChannelFactoryTest {

    private ChannelFactory factory;

    @BeforeEach
    void setUp() {
        factory = new ChannelFactory(List.of(new SmsChannel(), new EmailChannel(), new PushChannel()));
    }

    @Test
    void getChannel_sms_returnsSmsChannel() {
        assertThat(factory.getChannel("SMS")).isInstanceOf(SmsChannel.class);
    }

    @Test
    void getChannel_email_returnsEmailChannel() {
        assertThat(factory.getChannel("EMAIL")).isInstanceOf(EmailChannel.class);
    }

    @Test
    void getChannel_pushNotification_returnsPushChannel() {
        assertThat(factory.getChannel("PUSH_NOTIFICATION")).isInstanceOf(PushChannel.class);
    }

    @Test
    void getChannel_lowercaseName_works() {
        assertThat(factory.getChannel("sms")).isInstanceOf(SmsChannel.class);
    }

    @Test
    void getChannel_mixedCase_works() {
        assertThat(factory.getChannel("Email")).isInstanceOf(EmailChannel.class);
    }

    @Test
    void getChannel_unknownChannel_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> factory.getChannel("FAX"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getChannel_emptyString_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> factory.getChannel(""))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
