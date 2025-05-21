package com.zensar.ankit.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.oas.annotations.EnableOpenApi;

/**
 * Swagger configuration for API documentation.
 * Updated for Springfox Swagger 3.0.0 and Spring Boot 3.1.4 compatibility.
 */
@Configuration
@EnableOpenApi
public class SwaggerConfig extends WebMvcConfigurationSupport {
	
	/**
	 * Configures the Swagger Docket bean for API documentation.
	 * 
	 * @return Docket instance configured for OpenAPI 3.0
	 */
	@Bean
	public Docket productApi() {
		return new Docket(DocumentationType.OAS_30)
			.select()
			.apis(RequestHandlerSelectors.basePackage("com.zensar.ankit.demo.controller"))
			.paths(PathSelectors.regex("/user.*"))
			.build()
			.apiInfo(metaData());
	}
	
	/**
	 * Provides metadata for the API documentation.
	 * 
	 * @return ApiInfo instance with comprehensive API information
	 */
	private ApiInfo metaData() {
		return new ApiInfoBuilder()
			.title("Demo Microservice REST API")
			.description("""
				Spring Boot REST API for User Registration
				This API provides endpoints for managing user registration and validation.
				Updated for Java 22 and Spring Boot 3.1.4 compatibility.
			""")
			.version("2.0.0")
			.contact(new Contact("Zensar Demo Team", "https://www.zensar.com", "demo@zensar.com"))
			.license("Apache License Version 2.0")
			.licenseUrl("https://www.apache.org/licenses/LICENSE-2.0")
			.build();
	}
	
	/**
	 * Configures resource handlers for Swagger UI 3.0.0.
	 * 
	 * @param registry ResourceHandlerRegistry to be configured
	 */
	@Override
	protected void addResourceHandlers(ResourceHandlerRegistry registry) {
		// Make sure to call the parent method to maintain other resource handlers
		super.addResourceHandlers(registry);
		
		// Configure resource handlers for Swagger UI 3.0.0
		registry.addResourceHandler("/swagger-ui/**")
			.addResourceLocations("classpath:/META-INF/resources/webjars/springfox-swagger-ui/")
			.resourceChain(false);
		
		registry.addResourceHandler("/swagger-ui.html")
			.addResourceLocations("classpath:/META-INF/resources/");
		
		registry.addResourceHandler("/webjars/**")
			.addResourceLocations("classpath:/META-INF/resources/webjars/");
	}
}