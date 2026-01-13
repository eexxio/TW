package com.cinema.bookings.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for BaseEntity lifecycle callbacks
 * Tests the @PrePersist and @PreUpdate functionality
 *
 * @author Ioana-Loredana Stan - Viza 3
 */
class BaseEntityTest {

    /**
     * Concrete implementation of BaseEntity for testing
     */
    private static class TestEntity extends BaseEntity {
        // Empty concrete class for testing
    }

    @Test
    void testOnCreateSetsTimestamps() {
        // Given
        TestEntity entity = new TestEntity();
        LocalDateTime before = LocalDateTime.now().minusSeconds(1);

        // When
        entity.onCreate();

        // Then
        LocalDateTime after = LocalDateTime.now().plusSeconds(1);
        assertThat(entity.getCreatedAt()).isNotNull();
        assertThat(entity.getUpdatedAt()).isNotNull();
        assertThat(entity.getCreatedAt()).isAfterOrEqualTo(before);
        assertThat(entity.getCreatedAt()).isBeforeOrEqualTo(after);
        assertThat(entity.getUpdatedAt()).isAfterOrEqualTo(before);
        assertThat(entity.getUpdatedAt()).isBeforeOrEqualTo(after);
    }

    @Test
    void testOnUpdateSetsUpdatedTimestamp() {
        // Given
        TestEntity entity = new TestEntity();
        entity.onCreate();
        LocalDateTime createdAt = entity.getCreatedAt();

        // Wait a tiny bit to ensure timestamps differ
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // When
        entity.onUpdate();

        // Then
        assertThat(entity.getUpdatedAt()).isNotNull();
        assertThat(entity.getUpdatedAt()).isAfter(createdAt);
        assertThat(entity.getCreatedAt()).isEqualTo(createdAt); // createdAt should not change
    }

    @Test
    void testEntityHasId() {
        // Given
        TestEntity entity = new TestEntity();

        // When
        entity.setId(123L);

        // Then
        assertThat(entity.getId()).isEqualTo(123L);
    }
}
