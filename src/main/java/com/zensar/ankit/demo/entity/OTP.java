package com.zensar.ankit.demo.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Entity representing a One-Time Password (OTP) used for mobile number verification.
 * This entity stores the generated OTP code, associated mobile number, timestamps,
 * verification status, and attempt count to enable secure mobile verification through
 * time-limited OTP codes.
 */
@Entity
@Table(name = "otp", indexes = {
    @Index(name = "idx_otp_mobile", columnList = "userMobileNumber"),
    @Index(name = "idx_otp_expiration", columnList = "expirationTimestamp"),
    @Index(name = "idx_otp_mobile_status", columnList = "userMobileNumber,verificationStatus")
})
@ApiModel(description = "Entity representing a One-Time Password for mobile verification")
public class OTP {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @ApiModelProperty(notes = "The database generated OTP record ID")
    private Long id;
    
    @NotNull(message = "Mobile number cannot be null")
    @Column(nullable = false)
    @ApiModelProperty(notes = "Mobile number to which the OTP was sent", required = true)
    private String userMobileNumber;
    
    @NotNull(message = "OTP code cannot be null")
    @Column(nullable = false)
    @ApiModelProperty(notes = "The generated one-time password (stored in encrypted form)", required = true)
    private String otpCode;
    
    @NotNull(message = "Creation timestamp cannot be null")
    @Column(nullable = false)
    @ApiModelProperty(notes = "When the OTP was generated", required = true)
    private LocalDateTime creationTimestamp;
    
    @NotNull(message = "Expiration timestamp cannot be null")
    @Column(nullable = false)
    @ApiModelProperty(notes = "When the OTP expires (typically 10 minutes after creation)", required = true)
    private LocalDateTime expirationTimestamp;
    
    @NotNull(message = "Verification status cannot be null")
    @Column(nullable = false)
    @ApiModelProperty(notes = "Current status (PENDING, VERIFIED, EXPIRED, FAILED)", required = true)
    private String verificationStatus;
    
    @NotNull(message = "Verification attempts cannot be null")
    @Column(nullable = false)
    @ApiModelProperty(notes = "Number of verification attempts made with this OTP", required = true)
    private int verificationAttempts;
    
    @Version
    @ApiModelProperty(notes = "Version for optimistic locking")
    private Integer version;
    
    /**
     * Default constructor required by JPA
     */
    public OTP() {
        // Default values for new OTP records
        this.creationTimestamp = LocalDateTime.now();
        this.expirationTimestamp = LocalDateTime.now().plusMinutes(10); // 10-minute expiration
        this.verificationStatus = "PENDING";
        this.verificationAttempts = 0;
    }
    
    /**
     * Constructor with required fields for OTP generation
     * 
     * @param userMobileNumber The mobile number to which the OTP is sent
     * @param otpCode The generated OTP code
     */
    public OTP(String userMobileNumber, String otpCode) {
        this();
        this.userMobileNumber = userMobileNumber;
        this.otpCode = otpCode;
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the userMobileNumber
     */
    public String getUserMobileNumber() {
        return userMobileNumber;
    }

    /**
     * @param userMobileNumber the userMobileNumber to set
     */
    public void setUserMobileNumber(String userMobileNumber) {
        this.userMobileNumber = userMobileNumber;
    }

    /**
     * @return the otpCode
     */
    public String getOtpCode() {
        return otpCode;
    }

    /**
     * @param otpCode the otpCode to set
     */
    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }

    /**
     * @return the creationTimestamp
     */
    public LocalDateTime getCreationTimestamp() {
        return creationTimestamp;
    }

    /**
     * @param creationTimestamp the creationTimestamp to set
     */
    public void setCreationTimestamp(LocalDateTime creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    /**
     * @return the expirationTimestamp
     */
    public LocalDateTime getExpirationTimestamp() {
        return expirationTimestamp;
    }

    /**
     * @param expirationTimestamp the expirationTimestamp to set
     */
    public void setExpirationTimestamp(LocalDateTime expirationTimestamp) {
        this.expirationTimestamp = expirationTimestamp;
    }

    /**
     * @return the verificationStatus
     */
    public String getVerificationStatus() {
        return verificationStatus;
    }

    /**
     * @param verificationStatus the verificationStatus to set
     */
    public void setVerificationStatus(String verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    /**
     * @return the verificationAttempts
     */
    public int getVerificationAttempts() {
        return verificationAttempts;
    }

    /**
     * @param verificationAttempts the verificationAttempts to set
     */
    public void setVerificationAttempts(int verificationAttempts) {
        this.verificationAttempts = verificationAttempts;
    }
    
    /**
     * @return the version
     */
    public Integer getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(Integer version) {
        this.version = version;
    }
    
    /**
     * Increments the verification attempt counter
     * @return the new attempt count
     */
    public int incrementAttempts() {
        this.verificationAttempts++;
        return this.verificationAttempts;
    }
    
    /**
     * Checks if the OTP has expired based on current time
     * @return true if the OTP has expired, false otherwise
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expirationTimestamp);
    }
    
    /**
     * Checks if the maximum number of verification attempts has been reached
     * @return true if max attempts reached, false otherwise
     */
    public boolean isMaxAttemptsReached() {
        return this.verificationAttempts >= 3; // Maximum 3 attempts allowed
    }
    
    /**
     * Marks this OTP as verified
     */
    public void markAsVerified() {
        this.verificationStatus = "VERIFIED";
    }
    
    /**
     * Marks this OTP as expired
     */
    public void markAsExpired() {
        this.verificationStatus = "EXPIRED";
    }
    
    /**
     * Marks this OTP as failed due to too many attempts
     */
    public void markAsFailed() {
        this.verificationStatus = "FAILED";
    }
}