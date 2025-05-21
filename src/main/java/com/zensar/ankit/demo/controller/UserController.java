package com.zensar.ankit.demo.controller;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zensar.ankit.demo.entity.User;
import com.zensar.ankit.demo.service.UserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Controller for handling User registration operations
 */
@RestController
@RequestMapping(value = "user")
@Api(value = "user", tags = {"User Management"})
public class UserController {

	private final UserService userService;

	/**
	 * Constructor injection for better testability
	 * 
	 * @param userService the user service implementation
	 */
	public UserController(UserService userService) {
		this.userService = userService;
	}

	/**
	 * Creates a new user in the system
	 * 
	 * @param user the user data to be registered
	 * @return the created user with generated ID
	 */
	@ApiOperation(value = "Register a new user", response = User.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "User successfully registered"),
			@ApiResponse(code = 400, message = "Bad Request, request provided is not valid"),
			@ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
	})
	@PostMapping(value = "/")
	public User create(@Valid @RequestBody User user) {
		return userService.save(user);
	}
}