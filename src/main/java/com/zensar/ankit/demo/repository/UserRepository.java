package com.zensar.ankit.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zensar.ankit.demo.entity.User;

/**
 * Repository interface for {@link User} entities.
 * <p>
 * This interface provides CRUD operations for the User entity by extending the JpaRepository
 * interface from Spring Data JPA. It leverages the standard methods provided by JpaRepository
 * without defining any custom query methods.
 * </p>
 *
 * @author Ankit Demo Team
 * @version 1.0
 * @since Java 22
 */
public interface UserRepository extends JpaRepository<User, Long> {

}