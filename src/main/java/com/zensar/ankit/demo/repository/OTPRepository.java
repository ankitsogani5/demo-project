package com.zensar.ankit.demo.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.zensar.ankit.demo.entity.OTP;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing OTP entities.
 * Extends JpaRepository to inherit standard CRUD operations.
 * Provides custom query methods for OTP management and verification.
 */
@Repository
public interface OTPRepository extends JpaRepository<OTP, Long> {
    
    /**
     * Finds all OTP records for a specific mobile number.
     * 
     * @param mobileNumber The mobile number to search for
     * @return List of OTP entities associated with the mobile number
     */
    List<OTP> findByUserMobileNumber(String mobileNumber);
    
    /**
     * Finds the latest valid (not expired and pending verification) OTP for a mobile number.
     * 
     * @param mobileNumber The mobile number to search for
     * @return Optional containing the latest valid OTP if found, empty otherwise
     */
    @Query("SELECT o FROM OTP o WHERE o.userMobileNumber = :mobileNumber AND o.verificationStatus = 'PENDING' AND o.expirationTimestamp > CURRENT_TIMESTAMP ORDER BY o.creationTimestamp DESC")
    Optional<OTP> findLatestValidOtpForMobile(@Param("mobileNumber") String mobileNumber);
    
    /**
     * Updates the verification status of an OTP record.
     * 
     * @param otpId The ID of the OTP record to update
     * @param status The new verification status
     * @param attempts The updated number of verification attempts
     * @return Number of records updated (should be 1 if successful)
     */
    @Modifying
    @Transactional
    @Query("UPDATE OTP o SET o.verificationStatus = :status, o.verificationAttempts = :attempts WHERE o.id = :otpId")
    int updateVerificationStatus(@Param("otpId") Long otpId, @Param("status") String status, @Param("attempts") int attempts);
    
    /**
     * Batch deletes all expired OTP records.
     * 
     * @param cutoffTime The timestamp before which OTPs are considered expired
     * @return Number of records deleted
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM OTP o WHERE o.expirationTimestamp < :cutoffTime")
    int batchDeleteExpiredOtps(@Param("cutoffTime") LocalDateTime cutoffTime);
    
    /**
     * Counts the number of OTP generation attempts for a mobile number within a time window.
     * Used for rate limiting to prevent abuse.
     * 
     * @param mobileNumber The mobile number to check
     * @param startTime The start of the time window
     * @return Count of OTP records created within the time window
     */
    @Query("SELECT COUNT(o) FROM OTP o WHERE o.userMobileNumber = :mobileNumber AND o.creationTimestamp > :startTime")
    int countRecentOtpsForMobile(@Param("mobileNumber") String mobileNumber, @Param("startTime") LocalDateTime startTime);
    
    /**
     * Finds all OTPs with a specific verification status.
     * Useful for auditing and monitoring purposes.
     * 
     * @param status The verification status to search for
     * @return List of OTP entities with the specified status
     */
    List<OTP> findByVerificationStatus(String status);
}