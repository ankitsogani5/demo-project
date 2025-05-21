package com.zensar.ankit.demo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zensar.ankit.demo.entity.User;
import com.zensar.ankit.demo.service.UserService;

// Note: The controller will be updated to use jakarta.validation.Valid instead of javax.validation.Valid
// for Java 22 compatibility

/**
 * Unit tests for the UserController class.
 * Tests validate that the controller correctly handles HTTP POST requests,
 * properly validates input data using Jakarta Validation, and delegates to the UserService.
 * 
 * This test class is designed for Java 22 compatibility and uses JUnit 5, MockMvc, and Mockito.
 * It follows the test naming convention [UnitOfWork]_[StateUnderTest]_[ExpectedBehavior].
 */
@WebMvcTest(UserController.class)
public class UserControllerTest {
    // This test class uses MockMvc to test the UserController
    // It mocks the UserService dependency to isolate the controller layer

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private User validUser;

    /**
     * Set up method that runs before each test.
     * Initializes test data that will be used across multiple test methods.
     */
    @BeforeEach
    void setUp() {
        // Create a valid user for testing
        validUser = createValidUser();
    }

    /**
     * Test successful user registration with valid input data.
     * Verifies that the controller correctly processes a valid user registration request,
     * delegates to the service, and returns the created user.
     */
    @Test
    @DisplayName("Should successfully register a user when input is valid")
    void createUser_WithValidInput_ReturnsCreatedUser() throws Exception {
        // Given
        when(userService.save(any(User.class))).thenReturn(validUser);

        // When/Then
        mockMvc.perform(post("/user/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(validUser.getId()))
                .andExpect(jsonPath("$.userName").value(validUser.getUserName()))
                .andExpect(jsonPath("$.pin").value(validUser.getPin()))
                .andExpect(jsonPath("$.emailId").value(validUser.getEmailId()))
                .andExpect(jsonPath("$.mobileNumber").value(validUser.getMobileNumber()));
    }

    /**
     * Test validation failure when pin format is invalid.
     * Verifies that the controller correctly validates the pin format using Jakarta Validation
     * and returns a 400 Bad Request response when the pin is not 6 digits.
     */
    @Test
    @DisplayName("Should return 400 Bad Request when pin format is invalid")
    void createUser_WithInvalidPinFormat_ReturnsBadRequest() throws Exception {
        // Given
        User userWithInvalidPin = createValidUser();
        userWithInvalidPin.setPin("12345"); // Invalid: should be 6 digits

        // When/Then
        mockMvc.perform(post("/user/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userWithInvalidPin)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test validation failure when mobile number format is invalid.
     * Verifies that the controller correctly validates the mobile number format using Jakarta Validation
     * and returns a 400 Bad Request response when the mobile number is not 10 digits.
     */
    @Test
    @DisplayName("Should return 400 Bad Request when mobile number format is invalid")
    void createUser_WithInvalidMobileNumberFormat_ReturnsBadRequest() throws Exception {
        // Given
        User userWithInvalidMobile = createValidUser();
        userWithInvalidMobile.setMobileNumber("123456789"); // Invalid: should be 10 digits

        // When/Then
        mockMvc.perform(post("/user/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userWithInvalidMobile)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test validation failure when required fields are missing.
     * Verifies that the controller correctly validates that all required fields are present
     * using Jakarta Validation and returns a 400 Bad Request response when fields are missing.
     */
    @Test
    @DisplayName("Should return 400 Bad Request when required fields are missing")
    void createUser_WithMissingRequiredFields_ReturnsBadRequest() throws Exception {
        // Given
        User userWithMissingFields = new User();
        // No fields set - all required fields are missing

        // When/Then
        mockMvc.perform(post("/user/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userWithMissingFields)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Helper method to create a valid User object for testing.
     * This method serves as a test data builder for creating valid User objects.
     * 
     * @return A User object with all required fields set to valid values
     */
    private User createValidUser() {
        User user = new User();
        user.setId(1L);
        user.setUserName("testUser");
        user.setPin("123456"); // Valid: 6 digits
        user.setEmailId("test@example.com");
        user.setMobileNumber("1234567890"); // Valid: 10 digits
        return user;
    }
}