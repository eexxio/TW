package com.cinema.users.dto;

import com.cinema.users.entity.User;
import com.cinema.users.enums.Role;
import com.cinema.users.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for UserMapper.
 * Tests entity-DTO conversion methods.
 *
 * @author Alexandru Tesula
 */
class UserMapperTest {

    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapper();
    }

    @Test
    void testToEntity_WithAllFields() {
        // Arrange
        UserCreateDTO userCreateDTO = new UserCreateDTO(
                "John",
                "Doe",
                "john@example.com",
                "password123",
                "+1234567890",
                "1990-01-01",
                Role.USER
        );

        // Act
        User user = UserMapper.toEntity(userCreateDTO);

        // Assert
        assertNotNull(user);
        assertEquals("John", user.getFirstname());
        assertEquals("Doe", user.getLastname());
        assertEquals("john@example.com", user.getEmail());
        assertEquals("password123", user.getPassword());
        assertEquals("+1234567890", user.getPhoneNumber());
        assertEquals("1990-01-01", user.getDateOfBirth());
        assertEquals(Role.USER, user.getRole());
    }

    @Test
    void testToEntity_WithMinimalFields() {
        // Arrange
        UserCreateDTO userCreateDTO = new UserCreateDTO(
                "Jane",
                "Smith",
                "jane@example.com",
                "pass123",
                null,
                null,
                Role.USER
        );

        // Act
        User user = UserMapper.toEntity(userCreateDTO);

        // Assert
        assertNotNull(user);
        assertEquals("Jane", user.getFirstname());
        assertEquals("Smith", user.getLastname());
        assertEquals("jane@example.com", user.getEmail());
        assertNull(user.getPhoneNumber());
        assertNull(user.getDateOfBirth());
    }

    @Test
    void testToDTO_WithAllFields() {
        // Arrange
        User user = new User("John", "Doe", "john@example.com", "hashedPassword",
                "+1234567890", "1990-01-01", Role.ADMIN);
        user.setUser_id(1L);
        user.setCreatedAt(LocalDateTime.of(2024, 1, 1, 12, 0));
        user.setUpdatedAt(LocalDateTime.of(2024, 1, 2, 12, 0));

        // Act
        UserDTO userDTO = UserMapper.toDTO(user);

        // Assert
        assertNotNull(userDTO);
        assertEquals("john@example.com", userDTO.getEmail());
        assertEquals("John", userDTO.getFirstname());
        assertEquals("Doe", userDTO.getLastname());
        assertEquals("+1234567890", userDTO.getPhoneNumber());
        assertEquals("1990-01-01", userDTO.getDateOfBirth());
        assertEquals(Role.ADMIN, userDTO.getRole());
    }

    @Test
    void testToDTO_WithNullFields() {
        // Arrange
        User user = new User("Jane", "Smith", "jane@example.com", "hashedPassword",
                null, null, Role.USER);

        // Act
        UserDTO userDTO = UserMapper.toDTO(user);

        // Assert
        assertNotNull(userDTO);
        assertEquals("jane@example.com", userDTO.getEmail());
        assertEquals("Jane", userDTO.getFirstname());
        assertNull(userDTO.getPhoneNumber());
        assertNull(userDTO.getDateOfBirth());
    }

    @Test
    void testToEntity_RoundTrip() {
        // Arrange
        UserCreateDTO originalDTO = new UserCreateDTO(
                "Test",
                "User",
                "test@example.com",
                "testPass123",
                "+9876543210",
                "2000-06-15",
                Role.USER
        );

        // Act
        User user = UserMapper.toEntity(originalDTO);
        UserDTO resultDTO = UserMapper.toDTO(user);

        // Assert
        assertEquals(originalDTO.getFirstName(), resultDTO.getFirstname());
        assertEquals(originalDTO.getLastName(), resultDTO.getLastname());
        assertEquals(originalDTO.getEmail(), resultDTO.getEmail());
        assertEquals(originalDTO.getPhoneNumber(), resultDTO.getPhoneNumber());
        assertEquals(originalDTO.getDateOfBirth(), resultDTO.getDateOfBirth());
        assertEquals(originalDTO.getRole(), resultDTO.getRole());
    }

    @Test
    void testToDTO_DifferentRoles() {
        // Test for USER role
        User userWithRole = new User("John", "Doe", "john@example.com", "pass", null, null, Role.USER);
        UserDTO userDTO = UserMapper.toDTO(userWithRole);
        assertEquals(Role.USER, userDTO.getRole());

        // Test for ADMIN role
        User adminWithRole = new User("Admin", "User", "admin@example.com", "pass", null, null, Role.ADMIN);
        UserDTO adminDTO = UserMapper.toDTO(adminWithRole);
        assertEquals(Role.ADMIN, adminDTO.getRole());

        // Test for PREMIUM role
        User premiumUser = new User("Premium", "User", "premium@example.com", "pass", null, null, Role.PREMIUM);
        UserDTO premiumDTO = UserMapper.toDTO(premiumUser);
        assertEquals(Role.PREMIUM, premiumDTO.getRole());
    }

    @Test
    void testToDTO_PreservesUserIdentity() {
        // Arrange
        User user = new User("Original", "User", "original@example.com", "pass", null, null, Role.USER);
        user.setUser_id(42L);

        // Act
        UserDTO userDTO = UserMapper.toDTO(user);

        // Assert
        // Note: UserDTO doesn't have id field, but email is unique identifier
        assertEquals("original@example.com", userDTO.getEmail());
        assertEquals("Original", userDTO.getFirstname());
    }

    @Test
    void testToEntity_WithPremiumRole() {
        // Arrange
        UserCreateDTO premiumDTO = new UserCreateDTO(
                "Premium",
                "Member",
                "premium@example.com",
                "premiumPass123",
                null,
                null,
                Role.PREMIUM
        );

        // Act
        User user = UserMapper.toEntity(premiumDTO);

        // Assert
        assertNotNull(user);
        assertEquals(Role.PREMIUM, user.getRole());
    }

    @Test
    void testToDTO_WithSpecialCharacters() {
        // Arrange
        User user = new User(
                "José",
                "García-López",
                "jose.garcia@example.com",
                "hashedPassword",
                "+34-123-456-789",
                "1985-12-25",
                Role.USER
        );

        // Act
        UserDTO userDTO = UserMapper.toDTO(user);

        // Assert
        assertNotNull(userDTO);
        assertEquals("José", userDTO.getFirstname());
        assertEquals("García-López", userDTO.getLastname());
        assertEquals("jose.garcia@example.com", userDTO.getEmail());
    }
}

