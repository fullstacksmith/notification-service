package com.gila.notification_service.controller;

import com.gila.notification_service.dto.CreateMessageDto;
import com.gila.notification_service.dto.MessageResponseDto;
import com.gila.notification_service.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping
    public ResponseEntity<MessageResponseDto> sendMessage(@Valid @RequestBody CreateMessageDto createMessageDto) {
        MessageResponseDto result = messageService.proccess(createMessageDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
}
