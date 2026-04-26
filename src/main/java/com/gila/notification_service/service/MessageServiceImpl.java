package com.gila.notification_service.service;

import com.gila.notification_service.dto.CreateMessageDto;
import com.gila.notification_service.dto.MessageResponseDto;
import com.gila.notification_service.model.Message;
import com.gila.notification_service.repository.MessageRepository;
import com.gila.notification_service.util.Category;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private static final Logger log = LoggerFactory.getLogger(MessageServiceImpl.class);

    private final MessageRepository  messageRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public MessageResponseDto proccess(CreateMessageDto request) {
        if (request.idempotencyKey() != null) {
            return messageRepository.findByIdempotencyKey(request.idempotencyKey())
                    .map(MessageResponseDto::fromCachedMessage)
                    .orElseGet(() -> createAndDispatch(request));
        }
        return createAndDispatch(request);
    }

    private MessageResponseDto createAndDispatch(CreateMessageDto dto) {
        String categoryName = Category.fromId(dto.categoryId()).name();

        Message message = new Message(categoryName, dto.body(), dto.idempotencyKey());
        message = messageRepository.save(message);
        log.info("Message saved id={} category={}", message.getId(), categoryName);

        notificationService.dispatch(message);

        return new MessageResponseDto(message.getId(), "SENT");
    }
}
