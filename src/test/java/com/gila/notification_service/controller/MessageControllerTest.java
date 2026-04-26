package com.gila.notification_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gila.notification_service.dto.CreateMessageDto;
import com.gila.notification_service.dto.MessageResponseDto;
import com.gila.notification_service.exception.InvalidCategoryException;
import com.gila.notification_service.service.MessageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MessageController.class)
class MessageControllerTest {

    @Autowired MockMvc       mockMvc;
    @Autowired ObjectMapper  objectMapper;
    @MockBean  MessageService messageService;

    @Test
    void post_validRequest_returns201WithSentStatus() throws Exception {
        when(messageService.proccess(any())).thenReturn(new MessageResponseDto(1L, "SENT"));

        mockMvc.perform(post("/api/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateMessageDto(1L, "Goal scored!", null))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.messageId").value(1))
                .andExpect(jsonPath("$.status").value("SENT"));
    }

    @Test
    void post_withIdempotencyKey_returnsDuplicateStatus() throws Exception {
        when(messageService.proccess(any())).thenReturn(new MessageResponseDto(5L, "DUPLICATE"));

        mockMvc.perform(post("/api/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"categoryId\":1,\"body\":\"Goal!\",\"idempotencyKey\":\"key-abc\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("DUPLICATE"))
                .andExpect(jsonPath("$.messageId").value(5));
    }

    @Test
    void post_nullCategoryId_returns400() throws Exception {
        mockMvc.perform(post("/api/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"categoryId\":null,\"body\":\"Some news\"}"))
                .andExpect(status().isBadRequest());

        verify(messageService, never()).proccess(any());
    }

    @Test
    void post_blankBody_returns400() throws Exception {
        mockMvc.perform(post("/api/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"categoryId\":1,\"body\":\"\"}"))
                .andExpect(status().isBadRequest());

        verify(messageService, never()).proccess(any());
    }

    @Test
    void post_emptyRequestBody_returns400() throws Exception {
        mockMvc.perform(post("/api/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void post_invalidCategoryId_returns422() throws Exception {
        when(messageService.proccess(any())).thenThrow(new InvalidCategoryException("99"));

        mockMvc.perform(post("/api/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"categoryId\":99,\"body\":\"Unknown\"}"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error").exists());
    }
}
