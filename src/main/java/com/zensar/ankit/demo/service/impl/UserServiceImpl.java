package com.zensar.ankit.demo.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zensar.ankit.demo.entity.User;
import com.zensar.ankit.demo.repository.UserRepository;
import com.zensar.ankit.demo.service.UserService;

@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	private UserRepository repository;

	@Override
	public User save(User user) {
		return repository.save(user);
	}

}
