package com.zensar.ankit.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
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
import com.zensar.ankit.demo.service.impl.UserServiceImpl;

/**
 * Unit tests for the {@link UserService} interface implementation.
 * <p>
 * This test class verifies the contract and behavior of the service layer,
 * ensuring that user persistence operations work correctly according to
 * business requirements.
 * </p>
 *
 * @author Ankit Demo Team
 * @version 1.0
 * @since Java 22
 */
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User validUser;

    @BeforeEach
    void setUp() {
        // Create a valid user for testing
        validUser = new User();
        validUser.setUserName("TestUser");
        validUser.setPin("123456");
        validUser.setEmailId("test@example.com");
        validUser.setMobileNumber("1234567890");
    }

    @Test
    @DisplayName("save() should persist valid user and return saved entity")
    void save_WithValidUser_ReturnsSavedUser() {
        // Arrange
        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUserName(validUser.getUserName());
        savedUser.setPin(validUser.getPin());
        savedUser.setEmailId(validUser.getEmailId());
        savedUser.setMobileNumber(validUser.getMobileNumber());
        
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        User result = userService.save(validUser);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUserName()).isEqualTo(validUser.getUserName());
        assertThat(result.getPin()).isEqualTo(validUser.getPin());
        assertThat(result.getEmailId()).isEqualTo(validUser.getEmailId());
        assertThat(result.getMobileNumber()).isEqualTo(validUser.getMobileNumber());
        
        verify(userRepository, times(1)).save(validUser);
    }

    @Test
    @DisplayName("save() should throw IllegalArgumentException when user is null")
    void save_WithNullUser_ThrowsIllegalArgumentException() {
        // Act & Assert
        assertThatThrownBy(() -> userService.save(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("User cannot be null");
        
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("save() should pass through repository exceptions")
    void save_WhenRepositoryThrowsException_PropagatesException() {
        // Arrange
        when(userRepository.save(any(User.class)))
            .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThatThrownBy(() -> userService.save(validUser))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Database error");
        
        verify(userRepository, times(1)).save(validUser);
    }

    @Test
    @DisplayName("save() should handle user with pre-assigned ID")
    void save_WithPreAssignedId_DelegatesIdGenerationToRepository() {
        // Arrange
        validUser.setId(999L); // Pre-assign an ID
        
        User savedUser = new User();
        savedUser.setId(1L); // Repository assigns a different ID
        savedUser.setUserName(validUser.getUserName());
        savedUser.setPin(validUser.getPin());
        savedUser.setEmailId(validUser.getEmailId());
        savedUser.setMobileNumber(validUser.getMobileNumber());
        
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        User result = userService.save(validUser);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L); // Repository's ID should be used
        verify(userRepository, times(1)).save(validUser);
    }

    @Test
    @DisplayName("save() should preserve all user properties")
    void save_WithAllUserProperties_PreservesAllProperties() {
        // Arrange
        when(userRepository.save(any(User.class))).thenReturn(validUser);

        // Act
        User result = userService.save(validUser);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUserName()).isEqualTo(validUser.getUserName());
        assertThat(result.getPin()).isEqualTo(validUser.getPin());
        assertThat(result.getEmailId()).isEqualTo(validUser.getEmailId());
        assertThat(result.getMobileNumber()).isEqualTo(validUser.getMobileNumber());
        
        verify(userRepository, times(1)).save(validUser);
    }

    @Test
    @DisplayName("UserServiceImpl constructor should initialize with repository")
    void constructor_WithValidRepository_InitializesService() {
        // Arrange & Act
        UserService newService = new UserServiceImpl(userRepository);
        
        // Assert - verify the service can be used without NullPointerException
        when(userRepository.save(any(User.class))).thenReturn(validUser);
        User result = newService.save(validUser);
        
        assertThat(result).isNotNull();
        verify(userRepository, times(1)).save(validUser);
    }
}