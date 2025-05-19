package com.zensar.ankit.demo.service;

/**
 * Service interface for SMS message delivery to mobile devices.
 * Provides an abstraction layer for external SMS gateway integration,
 * handling message formatting, delivery, and error handling.
 * 
 * This interface is designed to be provider-agnostic, allowing for
 * different SMS gateway implementations.
 */
public interface SMSService {
    
    /**
     * Sends an SMS message to the specified mobile number.
     * 
     * @param mobileNumber The recipient's mobile number in a valid format
     * @param message The text message to be delivered
     * @return true if the message was successfully sent to the SMS gateway, false otherwise
     */
    boolean sendSMS(String mobileNumber, String message);
    
    /**
     * Checks the delivery status of a previously sent SMS message.
     * 
     * @param messageId The unique identifier of the message to check
     * @return The current delivery status of the message
     */
    DeliveryStatus checkDeliveryStatus(String messageId);
    
    /**
     * Enum representing possible delivery statuses for SMS messages.
     */
    enum DeliveryStatus {
        /**
         * Message has been accepted by the SMS gateway but not yet delivered
         */
        SENT,
        
        /**
         * Message has been successfully delivered to the recipient's device
         */
        DELIVERED,
        
        /**
         * Message delivery failed due to an error
         */
        FAILED,
        
        /**
         * Message expired before it could be delivered
         */
        EXPIRED,
        
        /**
         * Message delivery status is unknown or could not be determined
         */
        UNKNOWN
    }
}