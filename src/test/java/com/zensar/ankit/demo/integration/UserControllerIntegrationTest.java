package com.zensar.ankit.demo.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.zensar.ankit.demo.entity.User;

/**
 * Integration tests for the User Registration API.
 * These tests verify the entire request-response cycle through controller, service, and repository layers.
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class UserControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    /**
     * Test successful user registration with valid input data.
     * Verifies that a user is created and returned with a valid ID.
     */
    @Test
    public void registerUser_WithValidData_ReturnsCreatedUser() {
        // Arrange
        User user = new User();
        user.setUserName("TestUser");
        user.setPin("123456");
        user.setEmailId("test@example.com");
        user.setMobileNumber("1234567890");

        // Act
        ResponseEntity<User> response = restTemplate.postForEntity("/user/", user, User.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();
        assertThat(response.getBody().getUserName()).isEqualTo("TestUser");
        assertThat(response.getBody().getPin()).isEqualTo("123456");
        assertThat(response.getBody().getEmailId()).isEqualTo("test@example.com");
        assertThat(response.getBody().getMobileNumber()).isEqualTo("1234567890");
    }

    /**
     * Test user registration with invalid pin format.
     * Verifies that the API returns a 400 Bad Request with appropriate validation error messages.
     */
    @Test
    public void registerUser_WithInvalidPin_ReturnsBadRequestWithValidationErrors() {
        // Arrange
        User user = new User();
        user.setUserName("TestUser");
        user.setPin("12345"); // Invalid: should be 6 digits
        user.setEmailId("test@example.com");
        user.setMobileNumber("1234567890");

        // Act
        ResponseEntity<Map> response = restTemplate.postForEntity("/user/", user, Map.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("timestamp")).isNotNull();
        assertThat(response.getBody().get("status")).isEqualTo(400);
        
        List<String> errors = (List<String>) response.getBody().get("errors");
        assertThat(errors).isNotNull();
        assertThat(errors).contains("pin should be 6 digit number");
    }

    /**
     * Test user registration with invalid mobile number format.
     * Verifies that the API returns a 400 Bad Request with appropriate validation error messages.
     */
    @Test
    public void registerUser_WithInvalidMobileNumber_ReturnsBadRequestWithValidationErrors() {
        // Arrange
        User user = new User();
        user.setUserName("TestUser");
        user.setPin("123456");
        user.setEmailId("test@example.com");
        user.setMobileNumber("123456789"); // Invalid: should be 10 digits

        // Act
        ResponseEntity<Map> response = restTemplate.postForEntity("/user/", user, Map.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("timestamp")).isNotNull();
        assertThat(response.getBody().get("status")).isEqualTo(400);
        
        List<String> errors = (List<String>) response.getBody().get("errors");
        assertThat(errors).isNotNull();
        assertThat(errors).contains("mobile number should be 10 digit number");
    }

    /**
     * Test user registration with missing required fields.
     * Verifies that the API returns a 400 Bad Request with appropriate validation error messages.
     */
    @Test
    public void registerUser_WithMissingRequiredFields_ReturnsBadRequestWithValidationErrors() {
        // Arrange
        User user = new User();
        // Missing all required fields

        // Act
        ResponseEntity<Map> response = restTemplate.postForEntity("/user/", user, Map.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("timestamp")).isNotNull();
        assertThat(response.getBody().get("status")).isEqualTo(400);
        
        List<String> errors = (List<String>) response.getBody().get("errors");
        assertThat(errors).isNotNull();
        assertThat(errors).contains("Name cannot be null");
        assertThat(errors).contains("pin cannot be null");
        assertThat(errors).contains("email cannot be null");
        assertThat(errors).contains("mobile number cannot be null");
    }
}