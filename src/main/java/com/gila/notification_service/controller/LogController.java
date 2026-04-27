package com.gila.notification_service.controller;

import com.gila.notification_service.dto.NotificationLogDto;
import com.gila.notification_service.model.NotificationLog;
import com.gila.notification_service.repository.NotificationLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.data.domain.Sort.Direction.DESC;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
public class LogController {

    private final NotificationLogRepository logRepository;

    @GetMapping
    public ResponseEntity<Page<NotificationLogDto>> getLogs(
            @PageableDefault(size = 20, sort = "sentAt", direction = DESC) Pageable pageable,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String channel) {

        Page<NotificationLogDto> page = logRepository
                .findByFilters(category, channel, pageable)
                .map(this::toDto);

        return ResponseEntity.ok(page);
    }

    private NotificationLogDto toDto(NotificationLog log) {
        return new NotificationLogDto(
                log.getId(),
                log.getUserName(),
                log.getCategory(),
                log.getChannel(),
                log.getMessageBody(),
                log.getStatus(),
                log.getErrorDetail(),
                log.getSentAt().atOffset(java.time.ZoneOffset.UTC).toInstant()
        );
    }
}
