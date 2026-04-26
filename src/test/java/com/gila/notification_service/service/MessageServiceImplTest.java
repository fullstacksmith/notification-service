package com.gila.notification_service.service;

import com.gila.notification_service.dto.CreateMessageDto;
import com.gila.notification_service.dto.MessageResponseDto;
import com.gila.notification_service.exception.InvalidCategoryException;
import com.gila.notification_service.model.Message;
import com.gila.notification_service.repository.MessageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceImplTest {

    @Mock MessageRepository      messageRepository;
    @Mock NotificationService    notificationService;
    @InjectMocks MessageServiceImpl service;

    // ── helpers ──────────────────────────────────────────────────────────────

    private Message savedMessage(Long id, String category, String body) {
        Message m = new Message(category, body, null);
        // reflective set id since no setter
        try {
            var field = Message.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(m, id);
        } catch (Exception e) { throw new RuntimeException(e); }
        return m;
    }

    // ── tests ─────────────────────────────────────────────────────────────────

    @Test
    void process_withValidRequest_savesAndDispatchesAndReturnsSent() {
        CreateMessageDto dto = new CreateMessageDto(1L, "Sports news!", null);
        Message saved = savedMessage(10L, "SPORTS", "Sports news!");
        when(messageRepository.save(any())).thenReturn(saved);

        MessageResponseDto result = service.proccess(dto);

        assertThat(result.messageId()).isEqualTo(10L);
        assertThat(result.status()).isEqualTo("SENT");
        verify(messageRepository).save(any());
        verify(notificationService).dispatch(saved);
    }

    @Test
    void process_withDuplicateIdempotencyKey_returnsDuplicateWithoutDispatching() {
        Message existing = savedMessage(5L, "FINANCE", "Old news");
        CreateMessageDto dto = new CreateMessageDto(2L, "New news", "key-abc");
        when(messageRepository.findByIdempotencyKey("key-abc")).thenReturn(Optional.of(existing));

        MessageResponseDto result = service.proccess(dto);

        assertThat(result.status()).isEqualTo("DUPLICATE");
        assertThat(result.messageId()).isEqualTo(5L);
        verify(notificationService, never()).dispatch(any());
        verify(messageRepository, never()).save(any());
    }

    @Test
    void process_withNewIdempotencyKey_savesAndDispatches() {
        CreateMessageDto dto = new CreateMessageDto(3L, "Movie premiere!", "key-new");
        Message saved = savedMessage(7L, "MOVIES", "Movie premiere!");
        when(messageRepository.findByIdempotencyKey("key-new")).thenReturn(Optional.empty());
        when(messageRepository.save(any())).thenReturn(saved);

        MessageResponseDto result = service.proccess(dto);

        assertThat(result.status()).isEqualTo("SENT");
        verify(notificationService).dispatch(saved);
    }

    @Test
    void process_withoutIdempotencyKey_alwaysDispatches() {
        CreateMessageDto dto = new CreateMessageDto(2L, "Finance update", null);
        Message saved = savedMessage(3L, "FINANCE", "Finance update");
        when(messageRepository.save(any())).thenReturn(saved);

        service.proccess(dto);

        verify(messageRepository, never()).findByIdempotencyKey(any());
        verify(notificationService).dispatch(saved);
    }

    @Test
    void process_withInvalidCategoryId_throwsInvalidCategoryException() {
        CreateMessageDto dto = new CreateMessageDto(99L, "Unknown category", null);

        assertThatThrownBy(() -> service.proccess(dto))
                .isInstanceOf(InvalidCategoryException.class)
                .hasMessageContaining("99");

        verify(messageRepository, never()).save(any());
        verify(notificationService, never()).dispatch(any());
    }

    @Test
    void process_mapsCategory1ToSports() {
        CreateMessageDto dto = new CreateMessageDto(1L, "Goal!", null);
        when(messageRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.proccess(dto);

        verify(messageRepository).save(argThat(m -> "SPORTS".equals(m.getCategoryName())));
    }

    @Test
    void process_mapsCategory2ToFinance() {
        CreateMessageDto dto = new CreateMessageDto(2L, "Market up", null);
        when(messageRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.proccess(dto);

        verify(messageRepository).save(argThat(m -> "FINANCE".equals(m.getCategoryName())));
    }

    @Test
    void process_mapsCategory3ToMovies() {
        CreateMessageDto dto = new CreateMessageDto(3L, "New movie", null);
        when(messageRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.proccess(dto);

        verify(messageRepository).save(argThat(m -> "MOVIES".equals(m.getCategoryName())));
    }
}
