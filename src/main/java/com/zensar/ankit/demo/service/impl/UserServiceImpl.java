package com.zensar.ankit.demo.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zensar.ankit.demo.entity.User;
import com.zensar.ankit.demo.repository.UserRepository;
import com.zensar.ankit.demo.service.UserService;

/**
 * Implementation of the {@link UserService} interface.
 * <p>
 * This service provides the implementation for user management operations,
 * including persistence and business logic. It uses the {@link UserRepository}
 * for data access operations and applies appropriate transaction management.
 * </p>
 *
 * @author Ankit Demo Team
 * @version 1.0
 * @since Java 22
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {
	
	/**
	 * The repository used for User entity persistence operations.
	 */
	private final UserRepository repository;

	/**
	 * Constructs a new UserServiceImpl with the required repository dependency.
	 * <p>
	 * Uses constructor injection for better testability and to ensure that
	 * the required dependency is available at initialization time.
	 * </p>
	 *
	 * @param repository the user repository to be used for data access operations, must not be null
	 */
	public UserServiceImpl(UserRepository repository) {
		this.repository = repository;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This implementation validates that the user is not null before attempting to save it.
	 * If the user is null, an IllegalArgumentException is thrown.
	 * </p>
	 *
	 * @throws IllegalArgumentException if the user is null
	 */
	@Override
	@Transactional
	public User save(User user) {
		if (user == null) {
			throw new IllegalArgumentException("User cannot be null");
		}
		return repository.save(user);
	}

}