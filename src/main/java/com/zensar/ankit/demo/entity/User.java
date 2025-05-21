package com.zensar.ankit.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import io.swagger.v3.oas.annotations.media.Schema;

@Entity
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Schema(description = "The database generated product ID")
	private Long id;

	@NotNull(message = "Name cannot be null")
	@Schema(description = "user Name", required=true)
	private String userName;

	@NotNull(message = "pin cannot be null")
	@Schema(description = "pin code (6 digit number)", required=true)
	@Pattern(regexp = "[\\d]{6}", message = "pin should be 6 digit number")
	private String pin;

	@NotNull(message = "email cannot be null")
	@Schema(description = "email id", required=true)
	private String emailId;

	@NotNull(message = "mobile number cannot be null")
	@Schema(description = "mobile number (10 digit number)", required=true)
	@Pattern(regexp = "[\\d]{10}", message = "mobile number should be 10 digit number")
	private String mobileNumber;

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName
	 *            the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return the pin
	 */
	public String getPin() {
		return pin;
	}

	/**
	 * @param pin
	 *            the pin to set
	 */
	public void setPin(String pin) {
		this.pin = pin;
	}

	/**
	 * @return the emailId
	 */
	public String getEmailId() {
		return emailId;
	}

	/**
	 * @param emailId
	 *            the emailId to set
	 */
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	/**
	 * @return the mobileNumber
	 */
	public String getMobileNumber() {
		return mobileNumber;
	}

	/**
	 * @param mobileNumber
	 *            the mobileNumber to set
	 */
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

}