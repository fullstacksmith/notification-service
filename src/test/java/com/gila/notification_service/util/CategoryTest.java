package com.gila.notification_service.util;

import com.gila.notification_service.exception.InvalidCategoryException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CategoryTest {

    @Test
    void fromId_1_returnsSports() {
        assertThat(Category.fromId(1L)).isEqualTo(Category.SPORTS);
    }

    @Test
    void fromId_2_returnsFinance() {
        assertThat(Category.fromId(2L)).isEqualTo(Category.FINANCE);
    }

    @Test
    void fromId_3_returnsMovies() {
        assertThat(Category.fromId(3L)).isEqualTo(Category.MOVIES);
    }

    @Test
    void fromId_unknownId_throwsInvalidCategoryException() {
        assertThatThrownBy(() -> Category.fromId(99L))
                .isInstanceOf(InvalidCategoryException.class)
                .hasMessageContaining("99");
    }

    @Test
    void fromId_zero_throwsInvalidCategoryException() {
        assertThatThrownBy(() -> Category.fromId(0L))
                .isInstanceOf(InvalidCategoryException.class);
    }

    @Test
    void category_getId_returnsCorrectId() {
        assertThat(Category.SPORTS.getId()).isEqualTo(1L);
        assertThat(Category.FINANCE.getId()).isEqualTo(2L);
        assertThat(Category.MOVIES.getId()).isEqualTo(3L);
    }
}
