package com.boatsnbunnies.service;

/**
 * Represents a response from a Discord webhook request.
 *
 * @since 1.0
 */
public class WebhookResponse {
    private final boolean success;
    private final int statusCode;
    private final String message;
    
    /**
     * Creates a new webhook response.
     *
     * @param success Whether the request was successful
     * @param statusCode The HTTP status code
     * @param message The response message
     */
    public WebhookResponse(boolean success, int statusCode, String message) {
        this.success = success;
        this.statusCode = statusCode;
        this.message = message;
    }
    
    /**
     * Creates a successful webhook response.
     *
     * @param statusCode The HTTP status code
     * @param message The response message
     * @return A successful webhook response
     */
    public static WebhookResponse success(int statusCode, String message) {
        return new WebhookResponse(true, statusCode, message);
    }
    
    /**
     * Creates a failed webhook response.
     *
     * @param statusCode The HTTP status code
     * @param message The error message
     * @return A failed webhook response
     */
    public static WebhookResponse failure(int statusCode, String message) {
        return new WebhookResponse(false, statusCode, message);
    }
    
    /**
     * Checks if the request was successful.
     *
     * @return True if the request was successful, false otherwise
     */
    public boolean isSuccess() {
        return success;
    }
    
    /**
     * Gets the HTTP status code.
     *
     * @return The HTTP status code
     */
    public int getStatusCode() {
        return statusCode;
    }
    
    /**
     * Gets the response message.
     *
     * @return The response message
     */
    public String getMessage() {
        return message;
    }
}