package com.zensar.ankit.demo.integration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.zensar.ankit.demo.entity.User;
import com.zensar.ankit.demo.repository.UserRepository;

/**
 * Integration test for the UserRepository using TestContainers to provide a real PostgreSQL database environment.
 * This test verifies that the repository correctly interacts with a real database for CRUD operations.
 */
@SpringBootTest
@Testcontainers
public class UserRepositoryIntegrationTest {

    /**
     * PostgreSQL container for testing.
     * Using version 16-alpine which is compatible with Java 22.
     */
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("integration-tests-db")
            .withUsername("test")
            .withPassword("test");

    /**
     * Dynamically registers the database connection properties from the PostgreSQL container.
     */
    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        // Clear the repository before each test
        userRepository.deleteAll();
    }

    /**
     * Test that verifies saving a user to the database and retrieving it by ID.
     * This test ensures that the repository can correctly persist and retrieve user data.
     */
    @Test
    void saveUser_WithValidData_PersistsToDatabase() {
        // Arrange
        User user = createValidUser();

        // Act
        User savedUser = userRepository.save(user);
        User retrievedUser = userRepository.findById(savedUser.getId()).orElse(null);

        // Assert
        assertThat(retrievedUser).isNotNull();
        assertThat(retrievedUser.getId()).isNotNull();
        assertThat(retrievedUser.getUserName()).isEqualTo(user.getUserName());
        assertThat(retrievedUser.getPin()).isEqualTo(user.getPin());
        assertThat(retrievedUser.getEmailId()).isEqualTo(user.getEmailId());
        assertThat(retrievedUser.getMobileNumber()).isEqualTo(user.getMobileNumber());
    }

    /**
     * Test that verifies the repository can find all users in the database.
     * This test ensures that the findAll method works correctly with multiple users.
     */
    @Test
    void findAllUsers_WithMultipleUsers_ReturnsAllUsers() {
        // Arrange
        User user1 = createValidUser();
        User user2 = createValidUser();
        user2.setUserName("TestUser2");
        user2.setEmailId("test2@example.com");

        userRepository.save(user1);
        userRepository.save(user2);

        // Act
        var users = userRepository.findAll();

        // Assert
        assertThat(users).hasSize(2);
        assertThat(users).extracting(User::getUserName).containsExactlyInAnyOrder("TestUser", "TestUser2");
    }

    /**
     * Test that verifies the repository can delete a user from the database.
     * This test ensures that the delete method works correctly.
     */
    @Test
    void deleteUser_WithExistingUser_RemovesUserFromDatabase() {
        // Arrange
        User user = createValidUser();
        User savedUser = userRepository.save(user);

        // Act
        userRepository.delete(savedUser);
        var retrievedUser = userRepository.findById(savedUser.getId());

        // Assert
        assertThat(retrievedUser).isEmpty();
    }

    /**
     * Helper method to create a valid user for testing.
     * 
     * @return a valid User entity
     */
    private User createValidUser() {
        User user = new User();
        user.setUserName("TestUser");
        user.setPin("123456");
        user.setEmailId("test@example.com");
        user.setMobileNumber("1234567890");
        return user;
    }
}