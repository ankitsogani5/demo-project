package com.zensar.ankit.demo.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.zensar.ankit.demo.entity.OTP;

/**
 * Repository interface for managing OTP entities.
 * Provides methods for finding OTPs by mobile number, retrieving the latest valid OTP,
 * updating verification status, and batch deletion of expired OTPs.
 */
@Repository
public interface OTPRepository extends JpaRepository<OTP, Long> {
    
    /**
     * Finds all OTP records for a specific mobile number
     * 
     * @param userMobileNumber the mobile number to search for
     * @return list of OTP records associated with the mobile number
     */
    List<OTP> findByUserMobileNumber(String userMobileNumber);
    
    /**
     * Finds the latest valid (non-expired, pending) OTP for a mobile number
     * 
     * @param userMobileNumber the mobile number to search for
     * @param currentTime the current time to check against expiration
     * @return Optional containing the latest valid OTP if found, empty otherwise
     */
    @Query("SELECT o FROM OTP o WHERE o.userMobileNumber = :userMobileNumber AND o.verificationStatus = 'PENDING' AND o.expirationTimestamp > :currentTime ORDER BY o.creationTimestamp DESC")
    Optional<OTP> findLatestValidOtpForMobile(@Param("userMobileNumber") String userMobileNumber, @Param("currentTime") LocalDateTime currentTime);
    
    /**
     * Updates the verification status of an OTP
     * 
     * @param id the ID of the OTP to update
     * @param status the new verification status
     * @return the number of records updated
     */
    @Modifying
    @Query("UPDATE OTP o SET o.verificationStatus = :status WHERE o.id = :id")
    int updateVerificationStatus(@Param("id") Long id, @Param("status") String status);
    
    /**
     * Batch deletes expired OTP records
     * 
     * @param cutoffTime the time threshold for expiration
     * @return the number of records deleted
     */
    @Modifying
    @Query("DELETE FROM OTP o WHERE o.expirationTimestamp < :cutoffTime")
    int batchDeleteExpiredOtps(@Param("cutoffTime") LocalDateTime cutoffTime);
    
    /**
     * Finds OTP records with a specific verification status
     * 
     * @param status the verification status to search for
     * @return list of OTP records with the specified status
     */
    List<OTP> findByVerificationStatus(String status);
    
    /**
     * Finds OTP records for a mobile number with a specific verification status
     * 
     * @param userMobileNumber the mobile number to search for
     * @param status the verification status to search for
     * @return list of OTP records matching both criteria
     */
    List<OTP> findByUserMobileNumberAndVerificationStatus(String userMobileNumber, String status);
    
    /**
     * Counts the number of OTP generation attempts for a mobile number within a time period
     * Used for rate limiting and abuse prevention
     * 
     * @param userMobileNumber the mobile number to check
     * @param startTime the start of the time window
     * @return count of OTP records created in the time window
     */
    @Query("SELECT COUNT(o) FROM OTP o WHERE o.userMobileNumber = :userMobileNumber AND o.creationTimestamp > :startTime")
    long countRecentOtpsForMobile(@Param("userMobileNumber") String userMobileNumber, @Param("startTime") LocalDateTime startTime);
    
    /**
     * Finds all expired OTP records that haven't been marked as expired yet
     * 
     * @param currentTime the current time to check against expiration
     * @return list of expired OTP records still marked as PENDING
     */
    @Query("SELECT o FROM OTP o WHERE o.expirationTimestamp < :currentTime AND o.verificationStatus = 'PENDING'")
    List<OTP> findExpiredPendingOtps(@Param("currentTime") LocalDateTime currentTime);
    
    /**
     * Deletes all OTP records for a specific mobile number
     * Used when a user is deleted or for privacy compliance
     * 
     * @param userMobileNumber the mobile number whose OTP records should be deleted
     * @return the number of records deleted
     */
    @Modifying
    @Query("DELETE FROM OTP o WHERE o.userMobileNumber = :userMobileNumber")
    int deleteAllByUserMobileNumber(@Param("userMobileNumber") String userMobileNumber);
}