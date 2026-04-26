package com.gila.notification_service.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

public class GlobalExceptionHandler implements HandlerExceptionResolver {

    @Override
    public ModelAndView resolveException(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            Exception ex) {
        try {
            if (ex instanceof org.springframework.web.bind.MethodArgumentNotValidException) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Validation failed: " + ex.getMessage());
            } else {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An unexpected error occurred: " + ex.getMessage());
            }
        } catch (java.io.IOException e) {
            // Log the error
        }
        return new org.springframework.web.servlet.ModelAndView();
    }

    public static class ValidationException extends RuntimeException {
        public ValidationException(String message) {
            super(message);
        }
    }
}
