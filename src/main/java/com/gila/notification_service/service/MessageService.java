package com.gila.notification_service.service;

import com.gila.notification_service.dto.CreateMessageDto;
import com.gila.notification_service.dto.MessageResponseDto;


public interface MessageService {

    MessageResponseDto proccess(CreateMessageDto request);
}
