package com.gila.notification_service.exception;

public class InvalidCategoryException extends RuntimeException {
    public InvalidCategoryException(String category) {
        super("Category not found: " + category);
    }
}
