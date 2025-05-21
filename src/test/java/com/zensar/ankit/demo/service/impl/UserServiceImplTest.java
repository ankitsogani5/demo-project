package com.zensar.ankit.demo.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.zensar.ankit.demo.entity.User;
import com.zensar.ankit.demo.repository.UserRepository;

/**
 * Unit tests for {@link UserServiceImpl} class.
 * 
 * These tests verify that the service correctly delegates to the UserRepository
 * for user persistence operations.
 */
@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        // Create test user data programmatically
        testUser = new User();
        testUser.setUserName("TestUser");
        testUser.setPin("123456");
        testUser.setEmailId("test@example.com");
        testUser.setMobileNumber("1234567890");
    }

    @Test
    @DisplayName("Save user - When valid user provided - Should save user and return saved entity")
    void save_WithValidUser_ReturnsSavedUser() {
        // Arrange
        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUserName(testUser.getUserName());
        savedUser.setPin(testUser.getPin());
        savedUser.setEmailId(testUser.getEmailId());
        savedUser.setMobileNumber(testUser.getMobileNumber());
        
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        User result = userService.save(testUser);

        // Assert
        assertNotNull(result, "Saved user should not be null");
        assertEquals(1L, result.getId(), "Saved user should have an ID");
        assertEquals(testUser.getUserName(), result.getUserName(), "Username should match");
        assertEquals(testUser.getPin(), result.getPin(), "PIN should match");
        assertEquals(testUser.getEmailId(), result.getEmailId(), "Email should match");
        assertEquals(testUser.getMobileNumber(), result.getMobileNumber(), "Mobile number should match");
        
        // Verify repository interaction
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    @DisplayName("Save user - When repository throws exception - Should propagate exception")
    void save_WhenRepositoryThrowsException_PropagatesException() {
        // Arrange
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        try {
            userService.save(testUser);
        } catch (RuntimeException ex) {
            assertEquals("Database error", ex.getMessage(), "Exception message should match");
        }
        
        // Verify repository interaction
        verify(userRepository, times(1)).save(testUser);
    }
    
    @Test
    @DisplayName("Save user - When null user provided - Should delegate to repository")
    void save_WithNullUser_DelegatesToRepository() {
        // Arrange
        User nullUser = null;
        when(userRepository.save(null)).thenReturn(null);
        
        // Act
        User result = userService.save(nullUser);
        
        // Assert
        assertEquals(null, result, "Result should be null when null is passed");
        
        // Verify repository interaction
        verify(userRepository, times(1)).save(null);
    }
    
    @Test
    @DisplayName("Save user - When minimal user provided - Should save user correctly")
    void save_WithMinimalUser_SavesUserCorrectly() {
        // Arrange
        User minimalUser = new User();
        minimalUser.setUserName("MinUser");
        minimalUser.setPin("654321");
        minimalUser.setEmailId("minimal@example.com");
        minimalUser.setMobileNumber("9876543210");
        
        User savedMinimalUser = new User();
        savedMinimalUser.setId(2L);
        savedMinimalUser.setUserName(minimalUser.getUserName());
        savedMinimalUser.setPin(minimalUser.getPin());
        savedMinimalUser.setEmailId(minimalUser.getEmailId());
        savedMinimalUser.setMobileNumber(minimalUser.getMobileNumber());
        
        when(userRepository.save(minimalUser)).thenReturn(savedMinimalUser);
        
        // Act
        User result = userService.save(minimalUser);
        
        // Assert
        assertNotNull(result, "Saved minimal user should not be null");
        assertEquals(2L, result.getId(), "Saved minimal user should have an ID");
        assertEquals(minimalUser.getUserName(), result.getUserName(), "Username should match");
        assertEquals(minimalUser.getPin(), result.getPin(), "PIN should match");
        assertEquals(minimalUser.getEmailId(), result.getEmailId(), "Email should match");
        assertEquals(minimalUser.getMobileNumber(), result.getMobileNumber(), "Mobile number should match");
        
        // Verify repository interaction
        verify(userRepository, times(1)).save(minimalUser);
    }
}