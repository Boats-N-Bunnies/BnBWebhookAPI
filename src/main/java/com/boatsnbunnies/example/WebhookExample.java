package com.boatsnbunnies.example;

import com.boatsnbunnies.BnBWebhookAPI;
import com.boatsnbunnies.event.WebhookEvent;
import com.boatsnbunnies.model.WebhookEmbed;
import com.boatsnbunnies.model.WebhookField;
import com.boatsnbunnies.model.WebhookFooter;
import com.boatsnbunnies.service.WebhookResponse;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.awt.Color;
import java.time.Instant;
import java.util.Arrays;
import java.util.logging.Level;

/**
 * Example implementation of the BnBWebhookAPI.
 * This class demonstrates how to use the API in your own plugin.
 *
 * @since 1.0
 */
public class WebhookExample extends JavaPlugin implements Listener {
    private BnBWebhookAPI webhookAPI;
    
    @Override
    public void onEnable() {
        // Get the API instance
        webhookAPI = BnBWebhookAPI.getInstance();
        if (webhookAPI == null) {
            getLogger().severe("BnBWebhookAPI not found! Make sure it's installed and loaded before this plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        // Register event listener
        getServer().getPluginManager().registerEvents(this, this);
        
        // Register a webhook (typically done in onEnable or when needed)
        registerWebhook();
        
        // Example of sending a webhook
        sendExampleWebhook();
    }
    
    /**
     * Registers a webhook with the API.
     */
    private void registerWebhook() {
        // Check if webhook already exists
        if (!webhookAPI.getWebhookService().webhookExists("example-webhook")) {
            // Register a new webhook
            // In a real plugin, you would get this URL from your config
            String webhookUrl = "https://discord.com/api/webhooks/your-webhook-url";
            boolean registered = webhookAPI.getWebhookService().registerWebhook("example-webhook", webhookUrl);
            
            if (registered) {
                getLogger().info("Registered example webhook!");
            } else {
                getLogger().warning("Failed to register example webhook!");
            }
        }
    }
    
    /**
     * Sends an example webhook with various features.
     */
    private void sendExampleWebhook() {
        // Create a simple embed
        WebhookEmbed simpleEmbed = new WebhookEmbed.Builder()
                .title("Simple Embed")
                .description("This is a simple embed with just a title and description.")
                .color(0x00FF00) // Green color
                .build();
        
        // Create a more complex embed
        WebhookEmbed complexEmbed = new WebhookEmbed.Builder()
                .title("Complex Embed")
                .description("This embed demonstrates more features of the API.")
                .color(new Color(255, 0, 0)) // Red color using Color object
                .addField("Field 1", "This is a regular field", false)
                .addField("Field 2", "This is an inline field", true)
                .addField("Field 3", "This is another inline field", true)
                .footer("Footer text", "https://example.com/icon.png")
                .timestamp(Instant.now())
                .build();
        
        // Send multiple embeds in one webhook
        webhookAPI.getWebhookService()
                .send("example-webhook", Arrays.asList(simpleEmbed, complexEmbed))
                .thenAccept(response -> {
                    if (response.isSuccess()) {
                        getLogger().info("Webhook sent successfully!");
                    } else {
                        getLogger().warning("Failed to send webhook: " + response.getMessage());
                    }
                });
        
        // Example of sending a single embed and handling the response
        WebhookEmbed singleEmbed = new WebhookEmbed.Builder()
                .title("Single Embed Example")
                .description("This demonstrates sending a single embed and handling the response.")
                .color(0x0000FF) // Blue color
                .build();
        
        webhookAPI.getWebhookService()
                .send("example-webhook", singleEmbed)
                .thenAccept(this::handleWebhookResponse);
    }
    
    /**
     * Handles the response from a webhook request.
     *
     * @param response The webhook response
     */
    private void handleWebhookResponse(WebhookResponse response) {
        if (response.isSuccess()) {
            getLogger().info("Webhook sent successfully with status code: " + response.getStatusCode());
        } else {
            getLogger().warning("Failed to send webhook: " + response.getMessage() + 
                    " (Status code: " + response.getStatusCode() + ")");
        }
    }
    
    /**
     * Event handler for webhook events.
     *
     * @param event The webhook event
     */
    @EventHandler
    public void onWebhookEvent(WebhookEvent event) {
        // Log different types of webhook events
        switch (event.getEventType()) {
            case PRE_SEND:
                getLogger().info("Preparing to send webhook: " + event.getWebhookName());
                break;
            case SENT:
                getLogger().info("Webhook sent successfully: " + event.getWebhookName());
                break;
            case FAILED:
                getLogger().warning("Webhook failed: " + event.getWebhookName() + 
                        " - " + event.getResponse().getMessage());
                break;
            case RATE_LIMITED:
                getLogger().warning("Webhook rate limited: " + event.getWebhookName());
                break;
        }
    }
}