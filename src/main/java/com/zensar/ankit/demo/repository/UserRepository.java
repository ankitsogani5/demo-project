package com.zensar.ankit.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zensar.ankit.demo.entity.User;

/**
 * Repository interface for User entity operations
 * Compatible with Java 22 and Spring Boot 3.1.4
 */
public interface UserRepository extends JpaRepository<User, Long> {

}