package com.zensar.ankit.demo.service.impl;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.zensar.ankit.demo.exception.OTPDeliveryFailureException;
import com.zensar.ankit.demo.service.SMSService;

/**
 * Implementation of the SMSService interface for sending SMS messages.
 * This service provides a provider-agnostic abstraction for SMS delivery
 * with support for error handling, retry logic, and circuit breaker patterns.
 */
@Service
public class SMSServiceImpl implements SMSService {

    private static final Logger logger = LoggerFactory.getLogger(SMSServiceImpl.class);
    
    private static final int DEFAULT_CONNECT_TIMEOUT = 5000;
    private static final int DEFAULT_REQUEST_TIMEOUT = 5000;
    private static final int DEFAULT_SOCKET_TIMEOUT = 10000;
    private static final int DEFAULT_MAX_TOTAL_CONNECTIONS = 50;
    private static final int DEFAULT_MAX_PER_ROUTE_CONNECTIONS = 20;
    private static final int DEFAULT_KEEP_ALIVE_TIME_MILLIS = 20000; // 20 seconds
    private static final int DEFAULT_RETRY_COUNT = 3;
    private static final int DEFAULT_RETRY_DELAY_MS = 1000; // 1 second
    private static final double DEFAULT_RETRY_MULTIPLIER = 2.0;
    private static final int IDLE_CONNECTION_CLOSE_TIME_MS = 30000; // 30 seconds
    
    // Circuit breaker states
    private enum CircuitState {
        CLOSED,     // Normal operation, requests pass through
        OPEN,       // Circuit is open, requests fail fast
        HALF_OPEN   // Testing if service is back, limited requests pass through
    }
    
    // Circuit breaker configuration
    private static final int FAILURE_THRESHOLD = 5;  // Number of failures before opening circuit
    private static final int RESET_TIMEOUT_MS = 30000; // Time circuit stays open before trying again
    private static final int SUCCESS_THRESHOLD = 2;  // Successes needed in half-open state to close circuit
    
    // Circuit breaker state
    private CircuitState circuitState = CircuitState.CLOSED;
    private int failureCount = 0;
    private int successCount = 0;
    private long circuitOpenTime = 0;
    
    // Connection manager for cleanup
    private final PoolingHttpClientConnectionManager connectionManager;
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    @Value("${sms.gateway.api.url.production:https://api.smsgateway.com/api/v1}")
    private String smsGatewayApiUrl;
    
    @Value("${sms.gateway.api.endpoint.messages:/messages}")
    private String smsGatewayMessagesEndpoint;
    
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
    private int retryCount;
    
    @Value("${sms.delivery.retry.initial.delay:1000}")
    private int initialRetryDelayMs;
    
    @Value("${sms.delivery.retry.multiplier:2}")
    private double retryMultiplier;
    
    /**
     * Constructs a new SMSServiceImpl with the necessary dependencies.
     * Initializes the RestTemplate with connection pooling and timeout configurations.
     */
    @Autowired
    public SMSServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.connectionManager = createConnectionManager();
        this.restTemplate = createRestTemplate(this.connectionManager);
    }
    
    /**
     * Creates and configures a connection manager for connection pooling.
     * 
     * @return A configured PoolingHttpClientConnectionManager instance
     */
    private PoolingHttpClientConnectionManager createConnectionManager() {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(DEFAULT_MAX_TOTAL_CONNECTIONS);
        connectionManager.setDefaultMaxPerRoute(DEFAULT_MAX_PER_ROUTE_CONNECTIONS);
        connectionManager.setValidateAfterInactivity(1000); // Validate connections after 1 second of inactivity
        return connectionManager;
    }
    
    /**
     * Creates and configures a RestTemplate with connection pooling and timeout settings.
     * 
     * @param connectionManager The connection manager to use
     * @return A configured RestTemplate instance
     */
    private RestTemplate createRestTemplate(PoolingHttpClientConnectionManager connectionManager) {
        // Configure request timeouts
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(DEFAULT_CONNECT_TIMEOUT)
                .setConnectionRequestTimeout(DEFAULT_REQUEST_TIMEOUT)
                .setSocketTimeout(DEFAULT_SOCKET_TIMEOUT)
                .build();
        
        // Configure keep-alive strategy
        ConnectionKeepAliveStrategy keepAliveStrategy = (HttpResponse response, org.apache.http.protocol.HttpContext context) -> {
            HeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
            while (it.hasNext()) {
                HeaderElement he = it.nextElement();
                String param = he.getName();
                String value = he.getValue();
                if (value != null && param.equalsIgnoreCase("timeout")) {
                    return Long.parseLong(value) * 1000;
                }
            }
            return DEFAULT_KEEP_ALIVE_TIME_MILLIS;
        };
        
        // Build HttpClient with connection pooling
        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig)
                .setKeepAliveStrategy(keepAliveStrategy)
                .build();
        
        // Create request factory with the HttpClient
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        requestFactory.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT);
        requestFactory.setConnectionRequestTimeout(DEFAULT_REQUEST_TIMEOUT);
        requestFactory.setReadTimeout(DEFAULT_SOCKET_TIMEOUT);
        
        return new RestTemplate(requestFactory);
    }
    
    /**
     * Scheduled task to close idle and expired connections in the connection pool.
     * Runs every 30 seconds to clean up unused connections.
     */
    @Scheduled(fixedRate = 30000) // Run every 30 seconds
    public void closeIdleConnections() {
        if (connectionManager != null) {
            connectionManager.closeExpiredConnections();
            connectionManager.closeIdleConnections(IDLE_CONNECTION_CLOSE_TIME_MS, TimeUnit.MILLISECONDS);
            logger.debug("Closed expired and idle connections");
        }
    }

    /**
     * Sends an SMS message to the specified mobile number.
     * Implements retry logic for failed delivery attempts.
     * 
     * @param mobileNumber The recipient's mobile number in a valid format
     * @param message The text message to be delivered
     * @return true if the message was successfully sent to the SMS gateway, false otherwise
     */
    @Override
    public boolean sendSMS(String mobileNumber, String message) {
        logger.info("Sending SMS to mobile number: {}", mobileNumber);
        
        // Check circuit breaker state before proceeding
        if (!allowRequest()) {
            logger.warn("Circuit breaker is OPEN. Fast failing SMS delivery to {}", mobileNumber);
            throw OTPDeliveryFailureException.forMobileNumber(mobileNumber, 
                    "Service temporarily unavailable. Please try again later.");
        }
        
        int attempts = 0;
        int currentDelay = initialRetryDelayMs;
        Exception lastException = null;
        
        // Retry logic with exponential backoff
        while (attempts < retryCount) {
            try {
                // Attempt to send the SMS
                boolean result = doSendSMS(mobileNumber, message);
                if (result) {
                    logger.info("Successfully sent SMS to mobile number: {} (attempt: {})", mobileNumber, attempts + 1);
                    recordSuccess(); // Record success for circuit breaker
                    return true;
                }
            } catch (Exception e) {
                lastException = e;
                logger.warn("Failed to send SMS to mobile number: {} (attempt: {}). Error: {}", 
                        mobileNumber, attempts + 1, e.getMessage());
                recordFailure(); // Record failure for circuit breaker
            }
            
            attempts++;
            
            // If we've reached the maximum number of attempts, break out of the loop
            if (attempts >= retryCount) {
                break;
            }
            
            // Wait before the next retry with exponential backoff
            try {
                logger.info("Retrying SMS delivery to {} in {} ms (attempt {}/{})", 
                        mobileNumber, currentDelay, attempts + 1, retryCount);
                Thread.sleep(currentDelay);
                currentDelay = (int) (currentDelay * retryMultiplier); // Exponential backoff
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                logger.error("Retry interrupted", ie);
                break;
            }
        }
        
        // All retries failed
        if (lastException != null) {
            throw OTPDeliveryFailureException.forMobileNumber(mobileNumber, lastException);
        } else {
            throw OTPDeliveryFailureException.forMobileNumber(mobileNumber, 
                    "Failed to deliver SMS after " + retryCount + " attempts");
        }
    }

    /**
     * Checks the delivery status of a previously sent SMS message.
     * 
     * @param messageId The unique identifier of the message to check
     * @return The current delivery status of the message
     */
    @Override
    public DeliveryStatus checkDeliveryStatus(String messageId) {
        logger.info("Checking delivery status for message ID: {}", messageId);
        
        // Check circuit breaker state before proceeding
        if (!allowRequest()) {
            logger.warn("Circuit breaker is OPEN. Fast failing status check for message ID: {}", messageId);
            return DeliveryStatus.UNKNOWN;
        }
        
        try {
            // Prepare headers
            HttpHeaders headers = createAuthHeaders();
            
            // Make the API call to check status
            ResponseEntity<String> response = restTemplate.exchange(
                    smsGatewayApiUrl + "/status/" + messageId,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    String.class);
            
            // Record success for circuit breaker
            recordSuccess();
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                JsonNode responseJson = objectMapper.readTree(response.getBody());
                String status = responseJson.path("status").asText("UNKNOWN");
                
                // Map the gateway status to our DeliveryStatus enum
                switch (status.toUpperCase()) {
                    case "DELIVERED":
                        return DeliveryStatus.DELIVERED;
                    case "ACCEPTED":
                    case "SENT":
                        return DeliveryStatus.SENT;
                    case "FAILED":
                        return DeliveryStatus.FAILED;
                    case "EXPIRED":
                        return DeliveryStatus.EXPIRED;
                    default:
                        return DeliveryStatus.UNKNOWN;
                }
            }
            
            return DeliveryStatus.UNKNOWN;
            
        } catch (Exception e) {
            // Record failure for circuit breaker
            recordFailure();
            logger.error("Error checking delivery status for message ID: {}", messageId, e);
            return DeliveryStatus.UNKNOWN;
        }
    }
    
    /**
     * Formats an OTP message using the configured template.
     * 
     * @param otpCode The OTP code to include in the message
     * @param expiryMinutes The number of minutes until the OTP expires
     * @return The formatted message text
     */
    public String formatOTPMessage(String otpCode, int expiryMinutes) {
        return MessageFormat.format(otpMessageTemplate, otpCode, expiryMinutes);
    }
    
    /**
     * Internal method to send an SMS message to the SMS gateway.
     * 
     * @param mobileNumber The recipient's mobile number
     * @param message The message content
     * @return true if the message was successfully sent, false otherwise
     * @throws IOException if there is an error communicating with the SMS gateway
     */
    private boolean doSendSMS(String mobileNumber, String message) throws IOException {
        // Prepare headers
        HttpHeaders headers = createAuthHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        // Prepare request body
        ObjectNode requestBody = objectMapper.createObjectNode();
        requestBody.put("to", mobileNumber);
        requestBody.put("message", message);
        requestBody.put("sender", senderId);
        
        // Make the API call
        ResponseEntity<String> response = restTemplate.exchange(
                smsGatewayApiUrl + smsGatewayMessagesEndpoint,
                HttpMethod.POST,
                new HttpEntity<>(requestBody.toString(), headers),
                String.class);
        
        // Check if the request was successful
        if (response.getStatusCode() == HttpStatus.OK || response.getStatusCode() == HttpStatus.CREATED) {
            JsonNode responseJson = objectMapper.readTree(response.getBody());
            String messageId = responseJson.path("messageId").asText();
            logger.info("SMS sent successfully. Message ID: {}", messageId);
            return true;
        } else {
            logger.warn("Failed to send SMS. Status code: {}, Response: {}", 
                    response.getStatusCode(), response.getBody());
            return false;
        }
    }
    
    /**
     * Creates HTTP headers with authentication for the SMS gateway.
     * 
     * @return HttpHeaders with authentication
     */
    private HttpHeaders createAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(authHeader, authPrefix + " " + apiKey);
        return headers;
    }
    
    /**
     * Circuit breaker implementation: Checks if a request should be allowed based on the current circuit state.
     * 
     * @return true if the request should be allowed, false otherwise
     */
    private synchronized boolean allowRequest() {
        switch (circuitState) {
            case CLOSED:
                return true;
                
            case OPEN:
                // Check if the reset timeout has elapsed
                long elapsedTime = System.currentTimeMillis() - circuitOpenTime;
                if (elapsedTime >= RESET_TIMEOUT_MS) {
                    logger.info("Circuit breaker transitioning from OPEN to HALF_OPEN after {} ms", elapsedTime);
                    circuitState = CircuitState.HALF_OPEN;
                    successCount = 0;
                    return true;
                }
                return false;
                
            case HALF_OPEN:
                // In half-open state, allow limited requests to test if the service is back
                return true;
                
            default:
                return true;
        }
    }
    
    /**
     * Records a successful request for the circuit breaker.
     * In HALF_OPEN state, if enough successes occur, the circuit will close.
     */
    private synchronized void recordSuccess() {
        if (circuitState == CircuitState.HALF_OPEN) {
            successCount++;
            if (successCount >= SUCCESS_THRESHOLD) {
                logger.info("Circuit breaker closing after {} consecutive successes", successCount);
                circuitState = CircuitState.CLOSED;
                failureCount = 0;
                successCount = 0;
            }
        } else if (circuitState == CircuitState.CLOSED) {
            // Reset failure count on success in closed state
            failureCount = 0;
        }
    }
    
    /**
     * Records a failed request for the circuit breaker.
     * In CLOSED state, if enough failures occur, the circuit will open.
     * In HALF_OPEN state, a single failure will reopen the circuit.
     */
    private synchronized void recordFailure() {
        switch (circuitState) {
            case CLOSED:
                failureCount++;
                if (failureCount >= FAILURE_THRESHOLD) {
                    logger.warn("Circuit breaker opening after {} consecutive failures", failureCount);
                    circuitState = CircuitState.OPEN;
                    circuitOpenTime = System.currentTimeMillis();
                }
                break;
                
            case HALF_OPEN:
                logger.warn("Circuit breaker returning to OPEN state after failure in HALF_OPEN state");
                circuitState = CircuitState.OPEN;
                circuitOpenTime = System.currentTimeMillis();
                successCount = 0;
                break;
                
            default:
                // Do nothing for OPEN state
                break;
        }
    }
    
    /**
     * Execute a function with circuit breaker protection.
     * This is a generic method that can be used for any operation that needs circuit breaker protection.
     * 
     * @param <T> The return type of the function
     * @param function The function to execute
     * @param fallback The fallback function to execute if the circuit is open or the main function fails
     * @return The result of the function or fallback
     */
    private <T> T executeWithCircuitBreaker(Supplier<T> function, Supplier<T> fallback) {
        if (!allowRequest()) {
            logger.warn("Circuit breaker is OPEN. Using fallback.");
            return fallback.get();
        }
        
        try {
            T result = function.get();
            recordSuccess();
            return result;
        } catch (Exception e) {
            logger.error("Error executing function with circuit breaker", e);
            recordFailure();
            return fallback.get();
        }
    }
}