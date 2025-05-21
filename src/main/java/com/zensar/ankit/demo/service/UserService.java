package com.zensar.ankit.demo.service;

import com.zensar.ankit.demo.entity.User;

/**
 * Service interface for managing User entities.
 * <p>
 * This interface defines the contract for operations related to User management,
 * including persistence and business logic operations.
 * </p>
 */
public interface UserService {
    
    /**
     * Saves a user entity to the database.
     * <p>
     * This method persists a new user or updates an existing one.
     * If the user has an ID, it will update the existing record;
     * otherwise, it will create a new record.
     * </p>
     *
     * @param user the user entity to be saved, must not be null
     * @return the saved user entity with populated ID if newly created
     * @throws IllegalArgumentException if user is null or contains invalid data
     */
    User save(User user);
}