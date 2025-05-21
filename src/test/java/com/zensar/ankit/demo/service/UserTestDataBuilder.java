package com.zensar.ankit.demo.service;

import com.zensar.ankit.demo.entity.User;
import java.util.List;
import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * Utility class for creating test User entities with predefined or customizable attributes.
 * This builder simplifies the creation of test data across all service layer tests,
 * ensuring consistency and reducing code duplication.
 * 
 * Designed for Java 22 compatibility with modern language features.
 */
public class UserTestDataBuilder {
    
    // Default valid values
    private static final String DEFAULT_USERNAME = "TestUser";
    private static final String DEFAULT_PIN = "123456";
    private static final String DEFAULT_EMAIL = "test@example.com";
    private static final String DEFAULT_MOBILE = "1234567890";
    
    /**
     * Creates a valid User entity with default values.
     * 
     * @return a new User entity with valid default values
     */
    public static User createValidUser() {
        User user = new User();
        user.setUserName(DEFAULT_USERNAME);
        user.setPin(DEFAULT_PIN);
        user.setEmailId(DEFAULT_EMAIL);
        user.setMobileNumber(DEFAULT_MOBILE);
        return user;
    }
    
    /**
     * Creates a valid User entity with a specified ID.
     * 
     * @param id the ID to set for the user
     * @return a new User entity with valid default values and the specified ID
     */
    public static User createValidUserWithId(Long id) {
        User user = createValidUser();
        user.setId(id);
        return user;
    }
    
    /**
     * Creates a user with a custom username while keeping other fields with valid default values.
     * 
     * @param userName the custom username to set
     * @return a new User entity with the specified username and other valid default values
     */
    public static User createUserWithCustomUsername(String userName) {
        User user = createValidUser();
        user.setUserName(userName);
        return user;
    }
    
    /**
     * Creates a user with a custom pin while keeping other fields with valid default values.
     * 
     * @param pin the custom pin to set
     * @return a new User entity with the specified pin and other valid default values
     */
    public static User createUserWithCustomPin(String pin) {
        User user = createValidUser();
        user.setPin(pin);
        return user;
    }
    
    /**
     * Creates a user with a custom email while keeping other fields with valid default values.
     * 
     * @param emailId the custom email to set
     * @return a new User entity with the specified email and other valid default values
     */
    public static User createUserWithCustomEmail(String emailId) {
        User user = createValidUser();
        user.setEmailId(emailId);
        return user;
    }
    
    /**
     * Creates a user with a custom mobile number while keeping other fields with valid default values.
     * 
     * @param mobileNumber the custom mobile number to set
     * @return a new User entity with the specified mobile number and other valid default values
     */
    public static User createUserWithCustomMobileNumber(String mobileNumber) {
        User user = createValidUser();
        user.setMobileNumber(mobileNumber);
        return user;
    }
    
    /**
     * Creates a user with null username for testing validation.
     * 
     * @return a new User entity with null username and other valid default values
     */
    public static User createUserWithNullUsername() {
        User user = createValidUser();
        user.setUserName(null);
        return user;
    }
    
    /**
     * Creates a user with null pin for testing validation.
     * 
     * @return a new User entity with null pin and other valid default values
     */
    public static User createUserWithNullPin() {
        User user = createValidUser();
        user.setPin(null);
        return user;
    }
    
    /**
     * Creates a user with null email for testing validation.
     * 
     * @return a new User entity with null email and other valid default values
     */
    public static User createUserWithNullEmail() {
        User user = createValidUser();
        user.setEmailId(null);
        return user;
    }
    
    /**
     * Creates a user with null mobile number for testing validation.
     * 
     * @return a new User entity with null mobile number and other valid default values
     */
    public static User createUserWithNullMobileNumber() {
        User user = createValidUser();
        user.setMobileNumber(null);
        return user;
    }
    
    /**
     * Creates a user with invalid pin format for testing validation.
     * 
     * @return a new User entity with invalid pin and other valid default values
     */
    public static User createUserWithInvalidPin() {
        User user = createValidUser();
        user.setPin("12345"); // Not 6 digits
        return user;
    }
    
    /**
     * Creates a user with invalid mobile number format for testing validation.
     * 
     * @return a new User entity with invalid mobile number and other valid default values
     */
    public static User createUserWithInvalidMobileNumber() {
        User user = createValidUser();
        user.setMobileNumber("123456789"); // Not 10 digits
        return user;
    }
    
    /**
     * Creates a fully customized user with all attributes specified.
     * 
     * @param userName the username to set
     * @param pin the pin to set
     * @param emailId the email to set
     * @param mobileNumber the mobile number to set
     * @return a new User entity with all specified attributes
     */
    public static User createCustomUser(String userName, String pin, String emailId, String mobileNumber) {
        User user = new User();
        user.setUserName(userName);
        user.setPin(pin);
        user.setEmailId(emailId);
        user.setMobileNumber(mobileNumber);
        return user;
    }
    
    /**
     * Creates a user based on the provided record pattern.
     * Demonstrates Java 22's record pattern matching capabilities.
     * 
     * @param userInfo the record containing user information
     * @return a new User entity with attributes from the record
     */
    public static User createFromUserInfo(UserInfo userInfo) {
        return switch (userInfo) {
            case UserInfo(var name, var pin, var email, var mobile) when name != null && pin != null && 
                email != null && mobile != null -> {
                User user = new User();
                user.setUserName(name);
                user.setPin(pin);
                user.setEmailId(email);
                user.setMobileNumber(mobile);
                yield user;
            }
            case null -> throw new IllegalArgumentException("UserInfo cannot be null");
            default -> throw new IllegalArgumentException("Invalid UserInfo: missing required fields");
        };
    }
    
    /**
     * Record for storing user information to demonstrate record pattern matching.
     * This is a simple data carrier class using Java's record feature.
     */
    public record UserInfo(String name, String pin, String email, String mobile) {}
    
    /**
     * Creates a fully customized user with all attributes specified, including ID.
     * 
     * @param id the ID to set
     * @param userName the username to set
     * @param pin the pin to set
     * @param emailId the email to set
     * @param mobileNumber the mobile number to set
     * @return a new User entity with all specified attributes
     */
    public static User createCustomUser(Long id, String userName, String pin, String emailId, String mobileNumber) {
        User user = createCustomUser(userName, pin, emailId, mobileNumber);
        user.setId(id);
        return user;
    }
    
    /**
     * Creates a list of valid users for batch testing scenarios.
     * 
     * @param count the number of users to create
     * @return a list containing the specified number of valid users
     */
    public static List<User> createValidUsers(int count) {
        List<User> users = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            User user = createValidUser();
            user.setUserName(DEFAULT_USERNAME + i);
            user.setEmailId("test" + i + "@example.com");
            users.add(user);
        }
        return users;
    }
    
    /**
     * Creates an invalid user based on the specified validation type or object.
     * Uses Java 22's pattern matching for switch to determine the type of invalid user to create.
     * 
     * @param validationType the type of validation to test, can be a ValidationType enum or a String description
     * @return a user with the specified validation issue
     */
    public static User createInvalidUser(Object validationType) {
        return switch (validationType) {
            case ValidationType.NULL_USERNAME -> createUserWithNullUsername();
            case ValidationType.NULL_PIN -> createUserWithNullPin();
            case ValidationType.NULL_EMAIL -> createUserWithNullEmail();
            case ValidationType.NULL_MOBILE -> createUserWithNullMobileNumber();
            case ValidationType.INVALID_PIN_FORMAT -> createUserWithInvalidPin();
            case ValidationType.INVALID_MOBILE_FORMAT -> createUserWithInvalidMobileNumber();
            case String s when s.equalsIgnoreCase("null username") -> createUserWithNullUsername();
            case String s when s.equalsIgnoreCase("null pin") -> createUserWithNullPin();
            case String s when s.equalsIgnoreCase("null email") -> createUserWithNullEmail();
            case String s when s.equalsIgnoreCase("null mobile") -> createUserWithNullMobileNumber();
            case String s when s.equalsIgnoreCase("invalid pin") -> createUserWithInvalidPin();
            case String s when s.equalsIgnoreCase("invalid mobile") -> createUserWithInvalidMobileNumber();
            case null -> throw new IllegalArgumentException("Validation type cannot be null");
            default -> throw new IllegalArgumentException("Unknown validation type: " + validationType);
        };
    }
    
    /**
     * Enum representing different types of validation issues for testing.
     */
    public enum ValidationType {
        NULL_USERNAME,
        NULL_PIN,
        NULL_EMAIL,
        NULL_MOBILE,
        INVALID_PIN_FORMAT,
        INVALID_MOBILE_FORMAT
    }
    
    /**
     * Creates a user with custom modifications applied via a consumer function.
     * This method leverages Java's functional programming capabilities for flexible test data creation.
     * 
     * @param customizer a consumer function that applies custom modifications to a user
     * @return a user with the custom modifications applied
     */
    public static User createUserWithCustomizer(Consumer<User> customizer) {
        User user = createValidUser();
        customizer.accept(user);
        return user;
    }
}