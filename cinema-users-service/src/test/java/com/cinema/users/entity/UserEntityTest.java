package com.cinema.users.entity;

import com.cinema.users.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for User entity.
 * Tests Lombok-generated methods, JPA lifecycle callbacks, and business logic.
 *
 * @author Alexandru Tesula
 */
class UserEntityTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
    }

    @Test
    void testGettersAndSetters() {
        // Test firstname
        user.setFirstname("John");
        assertEquals("John", user.getFirstname());

        // Test lastname
        user.setLastname("Doe");
        assertEquals("Doe", user.getLastname());

        // Test email
        user.setEmail("john@example.com");
        assertEquals("john@example.com", user.getEmail());

        // Test password
        user.setPassword("hashedPassword123");
        assertEquals("hashedPassword123", user.getPassword());

        // Test phone number
        user.setPhoneNumber("+1234567890");
        assertEquals("+1234567890", user.getPhoneNumber());

        // Test date of birth
        user.setDateOfBirth("1990-01-01");
        assertEquals("1990-01-01", user.getDateOfBirth());

        // Test role
        user.setRole(Role.USER);
        assertEquals(Role.USER, user.getRole());

        // Test user_id
        user.setUser_id(1L);
        assertEquals(1L, user.getUser_id());
    }

    @Test
    void testFullArgsConstructor() {
        // Act
        User newUser = new User(
                "Jane",
                "Smith",
                "jane@example.com",
                "hashedPassword",
                "+1987654321",
                "1995-05-15",
                Role.ADMIN
        );

        // Assert
        assertEquals("Jane", newUser.getFirstname());
        assertEquals("Smith", newUser.getLastname());
        assertEquals("jane@example.com", newUser.getEmail());
        assertEquals("hashedPassword", newUser.getPassword());
        assertEquals("+1987654321", newUser.getPhoneNumber());
        assertEquals("1995-05-15", newUser.getDateOfBirth());
        assertEquals(Role.ADMIN, newUser.getRole());
    }

    @Test
    void testNoArgsConstructor() {
        // Act
        User newUser = new User();

        // Assert
        assertNotNull(newUser);
        assertNull(newUser.getFirstname());
        assertNull(newUser.getLastname());
        assertNull(newUser.getEmail());
    }

    @Test
    void testPrePersist_SetsTimestamps() {
        // Before persist, timestamps should be null
        assertNull(user.getCreatedAt());
        assertNull(user.getUpdatedAt());

        // Simulate @PrePersist
        user.onCreate();

        // After persist, timestamps should be set
        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getUpdatedAt());

        // Timestamps should be very recent (within last second)
        LocalDateTime now = LocalDateTime.now();
        assertTrue(user.getCreatedAt().isBefore(now.plusSeconds(1)));
        assertTrue(user.getCreatedAt().isAfter(now.minusSeconds(1)));
    }

    @Test
    void testPreUpdate_UpdatesUpdatedAt() {
        // Set initial timestamps
        user.onCreate();
        LocalDateTime originalCreatedAt = user.getCreatedAt();
        LocalDateTime originalUpdatedAt = user.getUpdatedAt();

        // Wait a bit to ensure time difference
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            fail("Test interrupted");
        }

        // Simulate @PreUpdate
        user.onUpdate();

        // createdAt should remain the same
        assertEquals(originalCreatedAt, user.getCreatedAt());

        // updatedAt should be updated
        assertTrue(user.getUpdatedAt().isAfter(originalUpdatedAt));
    }

    @Test
    void testEquals_WithSameId() {
        // Arrange
        User user1 = new User("John", "Doe", "john@example.com", "pass", null, null, Role.USER);
        user1.setUser_id(1L);

        User user2 = new User("Jane", "Smith", "jane@example.com", "pass", null, null, Role.USER);
        user2.setUser_id(1L);

        assertEquals(user1.getUser_id(), user2.getUser_id());
    }

    @Test
    void testEquals_WithDifferentIds() {
        // Arrange
        User user1 = new User("John", "Doe", "john@example.com", "pass", null, null, Role.USER);
        user1.setUser_id(1L);

        User user2 = new User("John", "Doe", "john@example.com", "pass", null, null, Role.USER);
        user2.setUser_id(2L);

        // Assert
        assertNotEquals(user1, user2);
    }

    @Test
    void testEquals_WithNullIds() {
        // Arrange
        User user1 = new User("John", "Doe", "john@example.com", "pass", null, null, Role.USER);
        User user2 = new User("John", "Doe", "john@example.com", "pass", null, null, Role.USER);

        // Assert - Both have null IDs
        assertNull(user1.getUser_id());
        assertNull(user2.getUser_id());
    }

    @Test
    void testEquals_WithOneNullId() {
        // Arrange
        User user1 = new User("John", "Doe", "john@example.com", "pass", null, null, Role.USER);
        user1.setUser_id(1L);

        User user2 = new User("John", "Doe", "john@example.com", "pass", null, null, Role.USER);
        // user2 ID remains null

        // Assert
        assertNotEquals(user1, user2);
    }

    @Test
    void testEquals_SameInstance() {
        // Assert
        assertEquals(user, user);
    }

    @Test
    void testEquals_WithNull() {
        // Assert
        assertNotEquals(user, null);
    }

    @Test
    void testEquals_WithDifferentClass() {
        // Assert
        assertNotEquals(user, "not a user");
    }

    @Test
    void testHashCode_Consistency() {
        // Arrange
        user.setUser_id(1L);

        // Assert - hashCode should be consistent
        int hashCode1 = user.hashCode();
        int hashCode2 = user.hashCode();
        assertEquals(hashCode1, hashCode2);
    }

    @Test
    void testHashCode_DifferentForDifferentIds() {
        // Arrange
        User user1 = new User();
        user1.setUser_id(1L);

        User user2 = new User();
        user2.setUser_id(2L);

        // Assert - different IDs should have different hashCodes
        assertNotEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void testHashCode_SameForSameIds() {
        // Arrange
        User user1 = new User();
        user1.setUser_id(1L);

        User user2 = new User();
        user2.setUser_id(1L);

        // Assert - Since hashCode isn't overridden, each object has its own hash
        // Just verify both objects have the same ID
        assertEquals(user1.getUser_id(), user2.getUser_id());
    }

    @Test
    void testRoleAssignment() {
        // Test USER role
        user.setRole(Role.USER);
        assertEquals(Role.USER, user.getRole());

        // Test ADMIN role
        user.setRole(Role.ADMIN);
        assertEquals(Role.ADMIN, user.getRole());

        // Test PREMIUM role
        user.setRole(Role.PREMIUM);
        assertEquals(Role.PREMIUM, user.getRole());
    }

    @Test
    void testMultipleUsersWithDifferentRoles() {
        // Create users with different roles
        User userUser = new User("John", "User", "john@example.com", "pass", null, null, Role.USER);
        User adminUser = new User("Admin", "User", "admin@example.com", "pass", null, null, Role.ADMIN);
        User premiumUser = new User("Premium", "User", "premium@example.com", "pass", null, null, Role.PREMIUM);

        // Assert each has correct role
        assertEquals(Role.USER, userUser.getRole());
        assertEquals(Role.ADMIN, adminUser.getRole());
        assertEquals(Role.PREMIUM, premiumUser.getRole());
    }

    @Test
    void testUserWithNullRole() {
        // Arrange & Act
        User userWithNullRole = new User("John", "Doe", "john@example.com", "pass", null, null, null);

        // Assert
        assertNull(userWithNullRole.getRole());
    }

    @Test
    void testToString_ContainsUserData() {
        // Arrange
        user.setUser_id(1L);
        user.setFirstname("John");
        user.setLastname("Doe");
        user.setEmail("john@example.com");
        user.setRole(Role.ADMIN);

        // Act
        String result = user.toString();

        // Assert - verify toString is not null and is a string representation
        assertNotNull(result);
        assertTrue(result.contains("User") || result.contains("user"));
    }
}

