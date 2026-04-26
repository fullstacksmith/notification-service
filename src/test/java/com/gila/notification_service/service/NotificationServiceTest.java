package com.gila.notification_service.service;

import com.gila.notification_service.channel.ChannelFactory;
import com.gila.notification_service.channel.NotificationChannel;
import com.gila.notification_service.dto.UserDto;
import com.gila.notification_service.model.Message;
import com.gila.notification_service.model.NotificationLog;
import com.gila.notification_service.repository.NotificationLogRepository;
import com.gila.notification_service.util.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock UserService               userService;
    @Mock ChannelFactory            channelFactory;
    @Mock NotificationLogRepository logRepository;
    @Mock NotificationChannel       mockChannel;
    @InjectMocks NotificationService service;

    private Message message;

    @BeforeEach
    void setUp() {
        message = new Message("FINANCE", "Market up 3%", null);
    }

    private UserDto user(Long id, List<String> channels) {
        return new UserDto(id, "User " + id, "user" + id + "@test.com",
                "+1000000" + id, List.of("FINANCE"), channels);
    }

    // ── dispatch ──────────────────────────────────────────────────────────────

    @Test
    void dispatch_withOneSubscriberOneChannel_sendsAndLogsSent() {
        UserDto bob = user(1L, List.of("SMS"));
        when(userService.findSubscribers("FINANCE")).thenReturn(List.of(bob));
        when(channelFactory.getChannel("SMS")).thenReturn(mockChannel);

        service.dispatch(message);

        verify(mockChannel).send(bob, message);
        ArgumentCaptor<NotificationLog> captor = ArgumentCaptor.forClass(NotificationLog.class);
        verify(logRepository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo("SENT");
        assertThat(captor.getValue().getChannel()).isEqualTo("SMS");
        assertThat(captor.getValue().getUserId()).isEqualTo(1L);
    }

    @Test
    void dispatch_withNoSubscribers_doesNotSendOrLog() {
        when(userService.findSubscribers("FINANCE")).thenReturn(List.of());

        service.dispatch(message);

        verify(channelFactory, never()).getChannel(any());
        verify(logRepository, never()).save(any());
    }

    @Test
    void dispatch_whenChannelFails_logsFailedAndContinuesWithNextChannel() {
        UserDto alice = user(2L, List.of("SMS", "EMAIL"));
        NotificationChannel emailChannel = mock(NotificationChannel.class);

        when(userService.findSubscribers("FINANCE")).thenReturn(List.of(alice));
        when(channelFactory.getChannel("SMS")).thenThrow(new RuntimeException("SMS provider down"));
        when(channelFactory.getChannel("EMAIL")).thenReturn(emailChannel);

        service.dispatch(message);

        verify(emailChannel).send(alice, message);

        ArgumentCaptor<NotificationLog> captor = ArgumentCaptor.forClass(NotificationLog.class);
        verify(logRepository, times(2)).save(captor.capture());

        List<NotificationLog> logs = captor.getAllValues();
        assertThat(logs).extracting(NotificationLog::getStatus)
                .containsExactlyInAnyOrder("FAILED", "SENT");
        assertThat(logs).filteredOn(l -> "FAILED".equals(l.getStatus()))
                .extracting(NotificationLog::getErrorDetail)
                .containsExactly("SMS provider down");
    }

    @Test
    void dispatch_whenAllChannelsFail_logsAllFailed() {
        UserDto user = user(3L, List.of("SMS", "EMAIL"));
        when(userService.findSubscribers("FINANCE")).thenReturn(List.of(user));
        when(channelFactory.getChannel(any())).thenThrow(new RuntimeException("All down"));

        service.dispatch(message);

        ArgumentCaptor<NotificationLog> captor = ArgumentCaptor.forClass(NotificationLog.class);
        verify(logRepository, times(2)).save(captor.capture());
        assertThat(captor.getAllValues()).extracting(NotificationLog::getStatus)
                .containsOnly("FAILED");
    }

    @Test
    void dispatch_withMultipleSubscribers_sendsToEach() {
        UserDto u1 = user(1L, List.of("SMS"));
        UserDto u2 = user(2L, List.of("EMAIL"));
        when(userService.findSubscribers("FINANCE")).thenReturn(List.of(u1, u2));
        when(channelFactory.getChannel(any())).thenReturn(mockChannel);

        service.dispatch(message);

        verify(mockChannel, times(2)).send(any(), eq(message));
        verify(logRepository, times(2)).save(any());
    }

    @Test
    void dispatch_userWithThreeChannels_sendsToAllThree() {
        UserDto user = user(1L, List.of("SMS", "EMAIL", "PUSH_NOTIFICATION"));
        when(userService.findSubscribers("FINANCE")).thenReturn(List.of(user));
        when(channelFactory.getChannel(any())).thenReturn(mockChannel);

        service.dispatch(message);

        verify(mockChannel, times(3)).send(eq(user), eq(message));
        verify(logRepository, times(3)).save(any());
    }

    @Test
    void dispatch_persistsCorrectLogFields() {
        UserDto bob = user(1L, List.of("SMS"));
        when(userService.findSubscribers("FINANCE")).thenReturn(List.of(bob));
        when(channelFactory.getChannel("SMS")).thenReturn(mockChannel);

        service.dispatch(message);

        ArgumentCaptor<NotificationLog> captor = ArgumentCaptor.forClass(NotificationLog.class);
        verify(logRepository).save(captor.capture());
        NotificationLog saved = captor.getValue();

        assertThat(saved.getUserId()).isEqualTo(1L);
        assertThat(saved.getUserName()).isEqualTo("User 1");
        assertThat(saved.getCategory()).isEqualTo("FINANCE");
        assertThat(saved.getChannel()).isEqualTo("SMS");
        assertThat(saved.getStatus()).isEqualTo("SENT");
        assertThat(saved.getErrorDetail()).isNull();
    }
}
