package com.zensar.ankit.demo.service.impl;

import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import com.zensar.ankit.demo.entity.User;
import com.zensar.ankit.demo.repository.UserRepository;
import com.zensar.ankit.demo.service.UserService;

/**
 * Implementation of the UserService interface for managing User entities.
 * <p>
 * This service provides the implementation for operations related to User management,
 * including persistence and business logic operations. It uses constructor injection
 * for better testability and applies transaction management for data consistency.
 * </p>
 *
 * @author Ankit Demo Team
 * @version 1.0
 * @since Java 22
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {
	
	private final UserRepository repository;

	/**
	 * Constructs a new UserServiceImpl with the required repository.
	 * <p>
	 * Uses constructor injection for better testability and to ensure
	 * that the repository is always available.
	 * </p>
	 *
	 * @param repository the user repository to be used for data access operations
	 */
	public UserServiceImpl(UserRepository repository) {
		this.repository = repository;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This implementation delegates to the UserRepository to save the user entity.
	 * The method is transactional, ensuring data consistency.
	 * </p>
	 *
	 * @param user the user entity to be saved, must not be null
	 * @return the saved user entity with populated ID if newly created
	 * @throws IllegalArgumentException if user is null or contains invalid data
	 */
	@Override
	public User save(User user) {
		if (user == null) {
			throw new IllegalArgumentException("User cannot be null");
		}
		return repository.save(user);
	}

}