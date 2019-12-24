package com.zensar.ankit.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zensar.ankit.demo.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

}
