package com.gila.notification_service.controller;

import com.gila.notification_service.model.NotificationLog;
import com.gila.notification_service.repository.NotificationLogRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Configuration
class LogControllerTestConfig {
    @Bean
    @SuppressWarnings("unused")
    public NotificationLogRepository logRepository() {
        return mock(NotificationLogRepository.class);
    }
}

@WebMvcTest(LogController.class)
@Import(LogControllerTestConfig.class)
class LogControllerTest {

    @Autowired MockMvc                   mockMvc;
    @Autowired NotificationLogRepository logRepository;

    private NotificationLog buildLog(String category, String channel, String status) {
        NotificationLog log = new NotificationLog(
                1L, 1L, "Alice", category, channel, "Goal!", status, null);
        try {
            var field = NotificationLog.class.getDeclaredField("sentAt");
            field.setAccessible(true);
            field.set(log, LocalDateTime.now(ZoneOffset.UTC));
        } catch (Exception e) { throw new RuntimeException(e); }
        return log;
    }

    @Test
    void getLogs_noFilters_returns200WithPagedContent() throws Exception {
        NotificationLog entry = buildLog("SPORTS", "SMS", "SENT");
        when(logRepository.findByFilters(isNull(), isNull(), any()))
                .thenReturn(new PageImpl<>(List.of(entry)));

        mockMvc.perform(get("/api/logs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].status").value("SENT"))
                .andExpect(jsonPath("$.content[0].channelName").value("SMS"))
                .andExpect(jsonPath("$.content[0].userName").value("Alice"));
    }

    @Test
    void getLogs_emptyPage_returns200WithEmptyContent() throws Exception {
        when(logRepository.findByFilters(isNull(), isNull(), any()))
                .thenReturn(Page.empty());

        mockMvc.perform(get("/api/logs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    void getLogs_withPaginationParams_returns200() throws Exception {
        when(logRepository.findByFilters(isNull(), isNull(), any()))
                .thenReturn(Page.empty());

        mockMvc.perform(get("/api/logs").param("page", "0").param("size", "5"))
                .andExpect(status().isOk());
    }

    @Test
    void getLogs_filterByCategory_passesCorrectParam() throws Exception {
        NotificationLog entry = buildLog("FINANCE", "EMAIL", "SENT");
        when(logRepository.findByFilters(eq("FINANCE"), isNull(), any()))
                .thenReturn(new PageImpl<>(List.of(entry)));

        mockMvc.perform(get("/api/logs").param("category", "FINANCE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].categoryName").value("FINANCE"));
    }

    @Test
    void getLogs_filterByChannel_passesCorrectParam() throws Exception {
        NotificationLog entry = buildLog("SPORTS", "EMAIL", "SENT");
        when(logRepository.findByFilters(isNull(), eq("EMAIL"), any()))
                .thenReturn(new PageImpl<>(List.of(entry)));

        mockMvc.perform(get("/api/logs").param("channel", "EMAIL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].channelName").value("EMAIL"));
    }

    @Test
    void getLogs_filterByCategoryAndChannel_passesCorrectParams() throws Exception {
        NotificationLog entry = buildLog("FINANCE", "EMAIL", "SENT");
        when(logRepository.findByFilters(eq("FINANCE"), eq("EMAIL"), any()))
                .thenReturn(new PageImpl<>(List.of(entry)));

        mockMvc.perform(get("/api/logs")
                        .param("category", "FINANCE")
                        .param("channel", "EMAIL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].categoryName").value("FINANCE"))
                .andExpect(jsonPath("$.content[0].channelName").value("EMAIL"));
    }

    @Test
    void getLogs_multipleEntries_returnsAll() throws Exception {
        NotificationLog sent   = buildLog("SPORTS",  "SMS",   "SENT");
        NotificationLog failed = buildLog("FINANCE",  "EMAIL", "FAILED");
        when(logRepository.findByFilters(isNull(), isNull(), any()))
                .thenReturn(new PageImpl<>(List.of(sent, failed)));

        mockMvc.perform(get("/api/logs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2));
    }
}
