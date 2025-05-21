package com.zensar.ankit.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Main application class for the Demo microservice.
 * This class serves as the entry point for the Spring Boot application.
 * 
 * @SpringBootApplication - Combines @Configuration, @EnableAutoConfiguration, and @ComponentScan
 * @EnableAspectJAutoProxy - Enables support for handling aspects via AspectJ
 */
@EnableAspectJAutoProxy
@SpringBootApplication
public class DemoApplication {
	
	/**
	 * Main method that bootstraps and launches the Spring application.
	 * 
	 * In Java 22, this method can benefit from virtual threads for handling concurrent operations
	 * within the Spring application context, particularly for I/O-bound operations.
	 * 
	 * @param args command line arguments passed to the application
	 */
	public static void main(String[] args) {
		// Launch the Spring Boot application
		// SpringApplication.run automatically configures the application based on classpath
		// and application properties, creating the appropriate ApplicationContext
		SpringApplication.run(DemoApplication.class, args);
	}

}