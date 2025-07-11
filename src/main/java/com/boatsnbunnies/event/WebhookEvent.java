package com.boatsnbunnies.event;

import com.boatsnbunnies.model.WebhookEmbed;
import com.boatsnbunnies.service.WebhookResponse;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.Collections;
import java.util.List;

/**
 * Represents an event related to a Discord webhook.
 *
 * @since 1.0
 */
public class WebhookEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    
    private final String webhookName;
    private final List<WebhookEmbed> embeds;
    private final WebhookResponse response;
    private final WebhookEventType eventType;
    
    /**
     * Creates a new webhook event.
     *
     * @param webhookName The name of the webhook
     * @param embeds The embeds sent with the webhook
     * @param response The response from the webhook request
     * @param eventType The type of event
     */
    public WebhookEvent(String webhookName, List<WebhookEmbed> embeds, WebhookResponse response, WebhookEventType eventType) {
        super(true); // Async event
        this.webhookName = webhookName;
        this.embeds = Collections.unmodifiableList(embeds);
        this.response = response;
        this.eventType = eventType;
    }
    
    /**
     * Gets the name of the webhook.
     *
     * @return The webhook name
     */
    public String getWebhookName() {
        return webhookName;
    }
    
    /**
     * Gets the embeds sent with the webhook.
     *
     * @return An unmodifiable list of embeds
     */
    public List<WebhookEmbed> getEmbeds() {
        return embeds;
    }
    
    /**
     * Gets the response from the webhook request.
     *
     * @return The webhook response
     */
    public WebhookResponse getResponse() {
        return response;
    }
    
    /**
     * Gets the type of event.
     *
     * @return The event type
     */
    public WebhookEventType getEventType() {
        return eventType;
    }
    
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
    
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
    
    /**
     * Enum representing the types of webhook events.
     */
    public enum WebhookEventType {
        /**
         * Event fired before a webhook is sent.
         */
        PRE_SEND,
        
        /**
         * Event fired after a webhook is successfully sent.
         */
        SENT,
        
        /**
         * Event fired when a webhook fails to send.
         */
        FAILED,
        
        /**
         * Event fired when a webhook is rate limited.
         */
        RATE_LIMITED
    }
}