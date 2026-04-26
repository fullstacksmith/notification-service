package com.gila.notification_service.util;

import com.gila.notification_service.exception.InvalidCategoryException;

public enum Category {
    SPORTS(1L),
    FINANCE(2L),
    MOVIES(3L);

    private final Long id;

    Category(Long id) { this.id = id; }

    public Long getId() { return id; }

    public static Category fromId(Long id) {
        for (Category c : values()) {
            if (c.id.equals(id)) return c;
        }
        throw new InvalidCategoryException(String.valueOf(id));
    }
}
