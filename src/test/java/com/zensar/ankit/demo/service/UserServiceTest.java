package com.zensar.ankit.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.ArgumentCaptor;

import com.zensar.ankit.demo.entity.User;
import com.zensar.ankit.demo.repository.UserRepository;
import com.zensar.ankit.demo.service.impl.UserServiceImpl;

/**
 * Unit tests for the {@link UserService} interface.
 * <p>
 * These tests verify the contract and behavior of the service layer,
 * ensuring that user persistence operations work correctly according to
 * business requirements.
 * </p>
 *
 * @author Ankit Demo Team
 * @version 1.0
 * @since Java 22
 */
@DisplayName("UserService Specification")
class UserServiceTest {

    private UserRepository userRepository;
    private UserService userService;

    @BeforeEach
    void setUp() {
        // Mock the repository dependency
        userRepository = mock(UserRepository.class);
        
        // Create the service implementation with the mocked repository
        userService = new UserServiceImpl(userRepository);
    }

    @Nested
    @DisplayName("When saving a user")
    class SaveUserTests {
        
        @Test
        @DisplayName("should delegate to repository and return result")
        void save_DelegationToRepository_ReturnsRepositoryResult() {
            // Arrange
            User user = createValidUser();
            User expectedResult = createSavedUser(user);
            when(userRepository.save(user)).thenReturn(expectedResult);
            
            // Act
            User actualResult = userService.save(user);
            
            // Assert
            assertThat(actualResult).isSameAs(expectedResult);
            verify(userRepository, times(1)).save(user);
        }

        @Test
        @DisplayName("should save valid user and return saved entity")
        void save_ValidUser_ReturnsSavedUser() {
            // Arrange
            User user = createValidUser();
            User savedUser = createSavedUser(user);
            when(userRepository.save(user)).thenReturn(savedUser);

            // Act
            User result = userService.save(user);

            // Assert
            assertThat(result).isNotNull()
                    .isSameAs(savedUser)
                    .extracting(User::getId)
                    .isEqualTo(1L);

            // Verify repository interaction
            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(userCaptor.capture());
            
            User capturedUser = userCaptor.getValue();
            assertThat(capturedUser).isSameAs(user);
        }

        @ParameterizedTest
        @NullSource
        @DisplayName("should throw IllegalArgumentException when user is null")
        void save_NullUser_ThrowsIllegalArgumentException(User nullUser) {
            // Act & Assert
            assertThatThrownBy(() -> userService.save(nullUser))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("User cannot be null");
            
            // Verify repository is never called with null
            verify(userRepository, never()).save(null);
        }
        
        @Test
        @DisplayName("should pass through all user properties to repository")
        void save_UserWithAllProperties_PreservesAllProperties() {
            // Arrange
            User user = createCompleteUser();
            when(userRepository.save(user)).thenReturn(user);

            // Act
            userService.save(user);

            // Assert
            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(userCaptor.capture());
            
            User capturedUser = userCaptor.getValue();
            assertThat(capturedUser)
                    .extracting(
                            User::getUserName,
                            User::getPin,
                            User::getEmailId,
                            User::getMobileNumber
                    )
                    .containsExactly(
                            "testUser",
                            "123456",
                            "test@example.com",
                            "1234567890"
                    );
        }
    }

    /**
     * Creates a valid user entity for testing purposes.
     *
     * @return a new User instance with valid test data
     */
    private User createValidUser() {
        User user = new User();
        user.setUserName("testUser");
        user.setPin("123456");
        user.setEmailId("test@example.com");
        user.setMobileNumber("1234567890");
        return user;
    }

    /**
     * Creates a complete user with all properties set.
     *
     * @return a fully populated User instance
     */
    private User createCompleteUser() {
        User user = new User();
        user.setUserName("testUser");
        user.setPin("123456");
        user.setEmailId("test@example.com");
        user.setMobileNumber("1234567890");
        return user;
    }

    /**
     * Simulates a saved user by setting an ID on the provided user.
     *
     * @param user the user to be "saved"
     * @return the same user instance with an ID set
     */
    private User createSavedUser(User user) {
        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUserName(user.getUserName());
        savedUser.setPin(user.getPin());
        savedUser.setEmailId(user.getEmailId());
        savedUser.setMobileNumber(user.getMobileNumber());
        return savedUser;
    }
}