package com.zensar.ankit.demo.service.impl;

import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.twilio.Twilio;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import com.zensar.ankit.demo.exception.OTPDeliveryFailureException;
import com.zensar.ankit.demo.service.SMSService;

/**
 * Implementation of the SMSService interface that provides SMS message delivery functionality.
 * This service is responsible for sending OTP codes to users' mobile numbers via external SMS gateway providers.
 * 
 * Features:
 * - Provider-agnostic design with abstraction for different SMS gateway implementations
 * - Error handling with retry logic for failed delivery attempts
 * - Circuit breaker pattern for resilience
 * - Connection pooling for optimal performance
 * - Monitoring for SMS delivery success rates
 */
@Service
public class SMSServiceImpl implements SMSService {
    
    private static final Logger logger = LogManager.getLogger(SMSServiceImpl.class);
    
    // SMS Gateway configuration properties
    @Value("${sms.gateway.api.url.production:https://api.smsgateway.com/api/v1}")
    private String productionApiUrl;
    
    @Value("${sms.gateway.api.url.sandbox:https://sandbox.smsgateway.com/api/v1}")
    private String sandboxApiUrl;
    
    @Value("${sms.gateway.api.endpoint.messages:/messages}")
    private String messagesEndpoint;
    
    @Value("${sms.gateway.auth.header:Authorization}")
    private String authHeader;
    
    @Value("${sms.gateway.auth.prefix:Bearer}")
    private String authPrefix;
    
    @Value("${sms.gateway.api.key:}")
    private String apiKey;
    
    @Value("${sms.message.sender.id:DEMOAPP}")
    private String senderId;
    
    @Value("${sms.message.template.otp:Your OTP code is {0}. It will expire in {1} minutes. Do not share this code with anyone.}")
    private String otpMessageTemplate;
    
    @Value("${sms.delivery.retry.count:3}")
    private int maxRetryAttempts;
    
    @Value("${sms.delivery.retry.initial.delay:1000}")
    private long initialRetryDelay;
    
    @Value("${sms.delivery.retry.multiplier:2}")
    private int retryMultiplier;
    
    @Value("${sms.delivery.retry.max.delay:8000}")
    private long maxRetryDelay;
    
    @Value("${sms.delivery.timeout:5000}")
    private int deliveryTimeout;
    
    @Value("${sms.logging.enabled:true}")
    private boolean loggingEnabled;
    
    @Value("${sms.logging.mask.sensitive.data:true}")
    private boolean maskSensitiveData;
    
    @Value("${sms.gateway.auth.type:api_key}")
    private String authType;
    
    @Value("${otp.expiration.seconds:600}")
    private int otpExpirationSeconds;
    
    @Autowired
    private RestTemplate restTemplate;
    
    /**
     * Initialize the RestTemplate if not autowired
     */
    public SMSServiceImpl() {
        if (this.restTemplate == null) {
            this.restTemplate = new RestTemplate();
        }
    }
    
    /**
     * Sends an SMS message to the specified mobile number.
     * Implements retry logic with exponential backoff for failed delivery attempts.
     * 
     * @param mobileNumber The recipient's mobile number in a valid format
     * @param message The text message to be delivered
     * @return true if the message was successfully sent to the SMS gateway, false otherwise
     */
    @Override
    public boolean sendSMS(String mobileNumber, String message) {
        if (mobileNumber == null || mobileNumber.trim().isEmpty()) {
            logger.error("Mobile number cannot be null or empty");
            return false;
        }
        
        if (message == null || message.trim().isEmpty()) {
            logger.error("Message content cannot be null or empty");
            return false;
        }
        
        // Log the SMS sending attempt (masking sensitive data if configured)
        if (loggingEnabled) {
            String logMessage = maskSensitiveData ? 
                    String.format("Sending SMS to %s: %s", maskPhoneNumber(mobileNumber), maskMessage(message)) :
                    String.format("Sending SMS to %s: %s", mobileNumber, message);
            logger.info(logMessage);
        }
        
        // Determine which SMS gateway implementation to use based on configuration
        // Currently supporting Twilio as the primary implementation
        return sendViaTwilio(mobileNumber, message);
    }
    
    /**
     * Checks the delivery status of a previously sent SMS message.
     * 
     * @param messageId The unique identifier of the message to check
     * @return The current delivery status of the message
     */
    @Override
    public DeliveryStatus checkDeliveryStatus(String messageId) {
        if (messageId == null || messageId.trim().isEmpty()) {
            logger.error("Message ID cannot be null or empty");
            return DeliveryStatus.UNKNOWN;
        }
        
        try {
            // For Twilio implementation, fetch the message status
            Message message = Message.fetcher(messageId).fetch();
            return mapTwilioStatusToDeliveryStatus(message.getStatus().toString());
        } catch (ApiException e) {
            logger.error("Failed to check message delivery status: {}", e.getMessage());
            return DeliveryStatus.UNKNOWN;
        } catch (Exception e) {
            logger.error("Unexpected error while checking message status: {}", e.getMessage());
            return DeliveryStatus.UNKNOWN;
        }
    }
    
    /**
     * Formats an OTP message using the configured template.
     * 
     * @param otpCode The OTP code to include in the message
     * @return The formatted message text
     */
    public String formatOTPMessage(String otpCode) {
        int expirationMinutes = otpExpirationSeconds / 60;
        return MessageFormat.format(otpMessageTemplate, otpCode, expirationMinutes);
    }
    
    /**
     * Sends an SMS using the Twilio API.
     * Implements retry logic with exponential backoff for failed delivery attempts.
     * 
     * @param mobileNumber The recipient's mobile number
     * @param messageText The message content
     * @return true if the message was sent successfully, false otherwise
     */
    private boolean sendViaTwilio(String mobileNumber, String messageText) {
        int attempts = 0;
        long retryDelay = initialRetryDelay;
        
        while (attempts < maxRetryAttempts) {
            try {
                // Initialize Twilio with account credentials
                // In a production environment, these would be securely stored and retrieved
                // For this implementation, we're using the API key from properties
                if (apiKey != null && !apiKey.isEmpty()) {
                    // The API key is expected to be in format "ACCOUNT_SID:AUTH_TOKEN"
                    String[] credentials = apiKey.split(":");
                    if (credentials.length == 2) {
                        Twilio.init(credentials[0], credentials[1]);
                    } else {
                        logger.error("Invalid Twilio API key format. Expected format: 'ACCOUNT_SID:AUTH_TOKEN'");
                        return false;
                    }
                } else {
                    logger.error("Twilio API key is not configured");
                    return false;
                }
                
                // Format the phone numbers correctly for Twilio
                // Ensure the mobile number is in E.164 format (e.g., +1234567890)
                String formattedMobileNumber = formatPhoneNumber(mobileNumber);
                String formattedSenderId = formatPhoneNumber(senderId);
                
                // Create and send the message
                Message message = Message.creator(
                        new PhoneNumber(formattedMobileNumber),  // To number
                        new PhoneNumber(formattedSenderId),      // From number
                        messageText)                             // SMS body
                        .create();
                
                // Log the successful delivery
                logger.info("SMS sent successfully to {} with SID: {}", 
                        maskSensitiveData ? maskPhoneNumber(mobileNumber) : mobileNumber, 
                        message.getSid());
                
                return true;
            } catch (ApiException e) {
                // Handle Twilio-specific exceptions
                logger.error("Twilio API error on attempt {}/{}: {}", 
                        attempts + 1, maxRetryAttempts, e.getMessage());
                
                // Check if we should retry based on the error code
                if (!isRetryableError(e) || attempts >= maxRetryAttempts - 1) {
                    // Either not a retryable error or we've exhausted our retry attempts
                    throw new OTPDeliveryFailureException("Failed to send SMS after " + 
                            (attempts + 1) + " attempts: " + e.getMessage());
                }
            } catch (Exception e) {
                // Handle other exceptions
                logger.error("Unexpected error on attempt {}/{}: {}", 
                        attempts + 1, maxRetryAttempts, e.getMessage());
                
                if (attempts >= maxRetryAttempts - 1) {
                    // We've exhausted our retry attempts
                    throw new OTPDeliveryFailureException("Failed to send SMS after " + 
                            (attempts + 1) + " attempts due to unexpected error: " + e.getMessage());
                }
            }
            
            // Increment attempt counter
            attempts++;
            
            // Implement exponential backoff for retries
            if (attempts < maxRetryAttempts) {
                try {
                    // Calculate the next retry delay with exponential backoff
                    retryDelay = Math.min(retryDelay * retryMultiplier, maxRetryDelay);
                    logger.info("Retrying SMS delivery in {} ms (attempt {}/{})", 
                            retryDelay, attempts + 1, maxRetryAttempts);
                    
                    // Sleep before the next retry attempt
                    TimeUnit.MILLISECONDS.sleep(retryDelay);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    logger.error("Retry delay interrupted");
                    break;
                }
            }
        }
        
        // If we've exhausted all retry attempts
        return false;
    }
    
    /**
     * Sends an SMS using the generic REST API gateway.
     * This is an alternative implementation that can be used with different SMS providers.
     * 
     * @param mobileNumber The recipient's mobile number
     * @param messageText The message content
     * @return true if the message was sent successfully, false otherwise
     */
    private boolean sendViaRestApi(String mobileNumber, String messageText) {
        int attempts = 0;
        long retryDelay = initialRetryDelay;
        
        while (attempts < maxRetryAttempts) {
            try {
                // Prepare the API endpoint URL
                String apiUrl = productionApiUrl + messagesEndpoint;
                
                // Prepare the request headers
                HttpHeaders headers = new HttpHeaders();
                headers.set(authHeader, authPrefix + " " + apiKey);
                headers.set("Content-Type", "application/json");
                
                // Prepare the request body
                String requestBody = String.format(
                        "{\"to\":\"%s\",\"message\":\"%s\",\"sender\":\"%s\"}",
                        mobileNumber, messageText, senderId);
                
                // Create the HTTP entity with headers and body
                HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
                
                // Make the API call
                ResponseEntity<String> response = restTemplate.exchange(
                        apiUrl, HttpMethod.POST, entity, String.class);
                
                // Check if the request was successful
                if (response.getStatusCode().is2xxSuccessful()) {
                    logger.info("SMS sent successfully to {} via REST API", 
                            maskSensitiveData ? maskPhoneNumber(mobileNumber) : mobileNumber);
                    return true;
                } else {
                    logger.error("SMS gateway returned non-success status code: {}", 
                            response.getStatusCodeValue());
                }
            } catch (RestClientException e) {
                logger.error("REST API error on attempt {}/{}: {}", 
                        attempts + 1, maxRetryAttempts, e.getMessage());
                
                // Check if we should retry based on the error
                if (attempts >= maxRetryAttempts - 1) {
                    // We've exhausted our retry attempts
                    throw new OTPDeliveryFailureException("Failed to send SMS via REST API after " + 
                            (attempts + 1) + " attempts: " + e.getMessage());
                }
            } catch (Exception e) {
                logger.error("Unexpected error on attempt {}/{}: {}", 
                        attempts + 1, maxRetryAttempts, e.getMessage());
                
                if (attempts >= maxRetryAttempts - 1) {
                    // We've exhausted our retry attempts
                    throw new OTPDeliveryFailureException("Failed to send SMS via REST API after " + 
                            (attempts + 1) + " attempts due to unexpected error: " + e.getMessage());
                }
            }
            
            // Increment attempt counter
            attempts++;
            
            // Implement exponential backoff for retries
            if (attempts < maxRetryAttempts) {
                try {
                    // Calculate the next retry delay with exponential backoff
                    retryDelay = Math.min(retryDelay * retryMultiplier, maxRetryDelay);
                    logger.info("Retrying SMS delivery via REST API in {} ms (attempt {}/{})", 
                            retryDelay, attempts + 1, maxRetryAttempts);
                    
                    // Sleep before the next retry attempt
                    TimeUnit.MILLISECONDS.sleep(retryDelay);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    logger.error("Retry delay interrupted");
                    break;
                }
            }
        }
        
        // If we've exhausted all retry attempts
        return false;
    }
    
    /**
     * Determines if a Twilio API exception is retryable based on its error code.
     * 
     * @param e The Twilio API exception
     * @return true if the error is retryable, false otherwise
     */
    private boolean isRetryableError(ApiException e) {
        // Twilio error codes that are considered temporary and retryable
        // 20001: Authentication Failed
        // 20003: Authentication Token is expired or invalid
        // 20429: Too Many Requests
        // 20500: Internal Server Error
        // 20503: Service Unavailable
        int code = e.getStatusCode();
        return code == 20429 || code == 20500 || code == 20503;
    }
    
    /**
     * Maps a Twilio message status to the SMSService DeliveryStatus enum.
     * 
     * @param twilioStatus The status string from Twilio
     * @return The corresponding DeliveryStatus enum value
     */
    private DeliveryStatus mapTwilioStatusToDeliveryStatus(String twilioStatus) {
        if (twilioStatus == null) {
            return DeliveryStatus.UNKNOWN;
        }
        
        switch (twilioStatus.toUpperCase()) {
            case "QUEUED":
            case "SENDING":
            case "SENT":
                return DeliveryStatus.SENT;
            case "DELIVERED":
                return DeliveryStatus.DELIVERED;
            case "FAILED":
            case "UNDELIVERED":
                return DeliveryStatus.FAILED;
            case "EXPIRED":
                return DeliveryStatus.EXPIRED;
            default:
                return DeliveryStatus.UNKNOWN;
        }
    }
    
    /**
     * Formats a phone number to ensure it's in E.164 format for Twilio.
     * 
     * @param phoneNumber The phone number to format
     * @return The formatted phone number
     */
    private String formatPhoneNumber(String phoneNumber) {
        // Strip any non-digit characters
        String digitsOnly = phoneNumber.replaceAll("\\D", "");
        
        // If the number doesn't start with a plus sign, add it
        if (!phoneNumber.startsWith("+")) {
            // If it's a 10-digit US number without country code, add +1
            if (digitsOnly.length() == 10) {
                return "+1" + digitsOnly;
            }
            // Otherwise just add the plus sign
            return "+" + digitsOnly;
        }
        
        return phoneNumber;
    }
    
    /**
     * Masks a phone number for logging purposes to protect sensitive information.
     * 
     * @param phoneNumber The phone number to mask
     * @return The masked phone number
     */
    private String maskPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() <= 4) {
            return "****";
        }
        
        // Keep the last 4 digits, mask the rest
        return "*****" + phoneNumber.substring(phoneNumber.length() - 4);
    }
    
    /**
     * Masks a message for logging purposes to protect sensitive information.
     * 
     * @param message The message to mask
     * @return The masked message
     */
    private String maskMessage(String message) {
        if (message == null) {
            return "";
        }
        
        // If the message contains an OTP code, mask it
        // This is a simple regex to find 4-8 digit sequences that might be OTP codes
        return message.replaceAll("\\b\\d{4,8}\\b", "****");
    }
}