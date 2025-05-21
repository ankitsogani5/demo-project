package com.zensar.ankit.demo.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.zensar.ankit.demo.entity.User;

/**
 * Unit tests for the UserRepository interface.
 * Uses Spring Boot's @DataJpaTest annotation to set up an in-memory H2 database
 * for testing repository methods in isolation.
 */
@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        // Create a test user for each test
        testUser = new User();
        testUser.setUserName("testUser");
        testUser.setPin("123456");
        testUser.setEmailId("test@example.com");
        testUser.setMobileNumber("1234567890");
    }

    @Test
    void saveUser_ValidUser_ReturnsUserWithId() {
        // Save the user
        User savedUser = userRepository.save(testUser);
        
        // Assert that the user has been assigned an ID
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getUserName()).isEqualTo(testUser.getUserName());
        assertThat(savedUser.getPin()).isEqualTo(testUser.getPin());
        assertThat(savedUser.getEmailId()).isEqualTo(testUser.getEmailId());
        assertThat(savedUser.getMobileNumber()).isEqualTo(testUser.getMobileNumber());
    }

    @Test
    void findById_ExistingUser_ReturnsUser() {
        // Save the user to the database
        User persistedUser = entityManager.persistAndFlush(testUser);
        
        // Find the user by ID
        Optional<User> foundUser = userRepository.findById(persistedUser.getId());
        
        // Assert that the user is found and has the correct properties
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUserName()).isEqualTo(testUser.getUserName());
        assertThat(foundUser.get().getPin()).isEqualTo(testUser.getPin());
        assertThat(foundUser.get().getEmailId()).isEqualTo(testUser.getEmailId());
        assertThat(foundUser.get().getMobileNumber()).isEqualTo(testUser.getMobileNumber());
    }

    @Test
    void findById_NonExistingUser_ReturnsEmptyOptional() {
        // Try to find a user with a non-existing ID
        Optional<User> foundUser = userRepository.findById(999L);
        
        // Assert that no user is found
        assertThat(foundUser).isEmpty();
    }

    @Test
    void findAll_WithMultipleUsers_ReturnsAllUsers() {
        // Create and save multiple users
        User user1 = new User();
        user1.setUserName("user1");
        user1.setPin("111111");
        user1.setEmailId("user1@example.com");
        user1.setMobileNumber("1111111111");
        entityManager.persist(user1);
        
        User user2 = new User();
        user2.setUserName("user2");
        user2.setPin("222222");
        user2.setEmailId("user2@example.com");
        user2.setMobileNumber("2222222222");
        entityManager.persist(user2);
        
        entityManager.flush();
        
        // Find all users
        List<User> users = userRepository.findAll();
        
        // Assert that all users are returned
        assertThat(users).hasSize(2);
        assertThat(users).extracting(User::getUserName).containsExactlyInAnyOrder("user1", "user2");
    }

    @Test
    void findAll_WithNoUsers_ReturnsEmptyList() {
        // Find all users in an empty database
        List<User> users = userRepository.findAll();
        
        // Assert that an empty list is returned
        assertThat(users).isEmpty();
    }

    @Test
    void deleteById_ExistingUser_RemovesUser() {
        // Save a user to the database
        User persistedUser = entityManager.persistAndFlush(testUser);
        
        // Delete the user by ID
        userRepository.deleteById(persistedUser.getId());
        
        // Try to find the deleted user
        Optional<User> deletedUser = userRepository.findById(persistedUser.getId());
        
        // Assert that the user is no longer found
        assertThat(deletedUser).isEmpty();
    }

    @Test
    void delete_ExistingUser_RemovesUser() {
        // Save a user to the database
        User persistedUser = entityManager.persistAndFlush(testUser);
        
        // Delete the user
        userRepository.delete(persistedUser);
        
        // Try to find the deleted user
        Optional<User> deletedUser = userRepository.findById(persistedUser.getId());
        
        // Assert that the user is no longer found
        assertThat(deletedUser).isEmpty();
    }

    @Test
    void count_WithMultipleUsers_ReturnsCorrectCount() {
        // Create and save multiple users
        User user1 = new User();
        user1.setUserName("user1");
        user1.setPin("111111");
        user1.setEmailId("user1@example.com");
        user1.setMobileNumber("1111111111");
        entityManager.persist(user1);
        
        User user2 = new User();
        user2.setUserName("user2");
        user2.setPin("222222");
        user2.setEmailId("user2@example.com");
        user2.setMobileNumber("2222222222");
        entityManager.persist(user2);
        
        entityManager.flush();
        
        // Count all users
        long count = userRepository.count();
        
        // Assert that the count is correct
        assertThat(count).isEqualTo(2);
    }

    @Test
    void existsById_ExistingUser_ReturnsTrue() {
        // Save a user to the database
        User persistedUser = entityManager.persistAndFlush(testUser);
        
        // Check if the user exists by ID
        boolean exists = userRepository.existsById(persistedUser.getId());
        
        // Assert that the user exists
        assertThat(exists).isTrue();
    }

    @Test
    void existsById_NonExistingUser_ReturnsFalse() {
        // Check if a user with a non-existing ID exists
        boolean exists = userRepository.existsById(999L);
        
        // Assert that the user does not exist
        assertThat(exists).isFalse();
    }

    @Test
    void saveAll_MultipleUsers_SavesAllUsers() {
        // Create multiple users
        User user1 = new User();
        user1.setUserName("user1");
        user1.setPin("111111");
        user1.setEmailId("user1@example.com");
        user1.setMobileNumber("1111111111");
        
        User user2 = new User();
        user2.setUserName("user2");
        user2.setPin("222222");
        user2.setEmailId("user2@example.com");
        user2.setMobileNumber("2222222222");
        
        // Save all users
        List<User> savedUsers = userRepository.saveAll(List.of(user1, user2));
        
        // Assert that all users are saved with IDs
        assertThat(savedUsers).hasSize(2);
        assertThat(savedUsers).allMatch(user -> user.getId() != null);
        assertThat(savedUsers).extracting(User::getUserName).containsExactlyInAnyOrder("user1", "user2");
    }

    @Test
    void deleteAll_WithMultipleUsers_RemovesAllUsers() {
        // Create and save multiple users
        User user1 = new User();
        user1.setUserName("user1");
        user1.setPin("111111");
        user1.setEmailId("user1@example.com");
        user1.setMobileNumber("1111111111");
        entityManager.persist(user1);
        
        User user2 = new User();
        user2.setUserName("user2");
        user2.setPin("222222");
        user2.setEmailId("user2@example.com");
        user2.setMobileNumber("2222222222");
        entityManager.persist(user2);
        
        entityManager.flush();
        
        // Delete all users
        userRepository.deleteAll();
        
        // Count all users
        long count = userRepository.count();
        
        // Assert that no users remain
        assertThat(count).isZero();
    }
}