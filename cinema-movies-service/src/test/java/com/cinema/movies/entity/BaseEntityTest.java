package com.cinema.movies.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for BaseEntity.
 * Tests JPA lifecycle callbacks, equals, and hashCode methods.
 *
 * @author Tudor
 */
class BaseEntityTest {

    // Concrete implementation of BaseEntity for testing
    private static class TestEntity extends BaseEntity {
    }

    private TestEntity entity1;
    private TestEntity entity2;

    @BeforeEach
    void setUp() {
        entity1 = new TestEntity();
        entity2 = new TestEntity();
    }

    @Test
    void testGettersAndSetters() {
        // Test ID
        entity1.setId(1L);
        assertEquals(1L, entity1.getId());

        // Test createdAt
        LocalDateTime now = LocalDateTime.now();
        entity1.setCreatedAt(now);
        assertEquals(now, entity1.getCreatedAt());

        // Test updatedAt
        entity1.setUpdatedAt(now);
        assertEquals(now, entity1.getUpdatedAt());
    }

    @Test
    void testPrePersist_SetsTimestamps() {
        // Before persist, timestamps should be null
        assertNull(entity1.getCreatedAt());
        assertNull(entity1.getUpdatedAt());

        // Simulate @PrePersist
        entity1.onCreate();

        // After persist, timestamps should be set
        assertNotNull(entity1.getCreatedAt());
        assertNotNull(entity1.getUpdatedAt());

        // Timestamps should be very recent (within last second)
        LocalDateTime now = LocalDateTime.now();
        assertTrue(entity1.getCreatedAt().isBefore(now.plusSeconds(1)));
        assertTrue(entity1.getCreatedAt().isAfter(now.minusSeconds(1)));
    }

    @Test
    void testPreUpdate_UpdatesUpdatedAt() {
        // Set initial createdAt
        entity1.onCreate();
        LocalDateTime originalUpdatedAt = entity1.getUpdatedAt();

        // Wait a bit to ensure time difference
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            fail("Test interrupted");
        }

        // Simulate @PreUpdate
        entity1.onUpdate();

        // createdAt should remain the same
        assertNotNull(entity1.getCreatedAt());

        // updatedAt should be updated
        assertNotNull(entity1.getUpdatedAt());
        assertTrue(entity1.getUpdatedAt().isAfter(originalUpdatedAt));
    }

    @Test
    void testEquals_WithSameId() {
        // Arrange
        entity1.setId(1L);
        entity2.setId(1L);

        // Assert
        assertEquals(entity1, entity2);
        assertEquals(entity1.hashCode(), entity2.hashCode());
    }

    @Test
    void testEquals_WithDifferentIds() {
        // Arrange
        entity1.setId(1L);
        entity2.setId(2L);

        // Assert
        assertNotEquals(entity1, entity2);
    }

    @Test
    void testEquals_WithNullIds() {
        // Both null IDs
        assertEquals(entity1, entity2);
        assertEquals(entity1.hashCode(), entity2.hashCode());
    }

    @Test
    void testEquals_WithOneNullId() {
        // Arrange
        entity1.setId(1L);
        // entity2 ID remains null

        // Assert
        assertNotEquals(entity1, entity2);
    }

    @Test
    void testEquals_SameInstance() {
        // Assert
        assertEquals(entity1, entity1);
    }

    @Test
    void testEquals_WithNull() {
        // Assert
        assertNotEquals(entity1, null);
    }

    @Test
    void testEquals_WithDifferentClass() {
        // Assert
        assertNotEquals(entity1, "not an entity");
    }

    @Test
    void testHashCode_Consistency() {
        // Arrange
        entity1.setId(1L);

        // Assert - hashCode should be consistent
        int hashCode1 = entity1.hashCode();
        int hashCode2 = entity1.hashCode();
        assertEquals(hashCode1, hashCode2);
    }

    @Test
    void testHashCode_DifferentForDifferentIds() {
        // Arrange
        entity1.setId(1L);
        entity2.setId(2L);

        // Assert - different IDs should have different hashCodes
        assertNotEquals(entity1.hashCode(), entity2.hashCode());
    }

    @Test
    void testHashCode_SameForSameIds() {
        // Arrange
        entity1.setId(1L);
        entity2.setId(1L);

        // Assert - same IDs should have same hashCodes
        assertEquals(entity1.hashCode(), entity2.hashCode());
    }

    @Test
    void testHashCode_WithNullId() {
        // Assert - hashCode should work with null ID
        assertNotNull(entity1.hashCode());
        assertEquals(entity1.hashCode(), entity2.hashCode());
    }
}
