package com.boatsnbunnies;

import com.boatsnbunnies.config.WebhookConfig;
import com.boatsnbunnies.service.WebhookService;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

/**
 * Main plugin class for BnBWebhookAPI.
 * This class serves as the entry point for the Discord Webhook API.
 * 
 * @since 1.0
 */
public final class BnBWebhookAPI extends JavaPlugin {

    private static BnBWebhookAPI instance;
    private WebhookService webhookService;
    private WebhookConfig webhookConfig;

    @Override
    public void onEnable() {
        instance = this;

        // Initialize configuration
        saveDefaultConfig();
        this.webhookConfig = new WebhookConfig(this);

        // Initialize webhook service
        this.webhookService = new WebhookService(this);

        getLogger().info("BnBWebhookAPI has been enabled!");
    }

    @Override
    public void onDisable() {
        // Clean up resources
        if (webhookService != null) {
            webhookService.shutdown();
        }

        instance = null;
        getLogger().info("BnBWebhookAPI has been disabled!");
    }

    /**
     * Gets the singleton instance of the plugin.
     * 
     * @return The plugin instance
     */
    public static BnBWebhookAPI getInstance() {
        return instance;
    }

    /**
     * Gets the webhook service for sending Discord webhooks.
     * 
     * @return The webhook service
     */
    public WebhookService getWebhookService() {
        return webhookService;
    }

    /**
     * Gets the webhook configuration manager.
     * 
     * @return The webhook configuration
     */
    public WebhookConfig getWebhookConfig() {
        return webhookConfig;
    }

    /**
     * Logs a message to the plugin's logger.
     * 
     * @param level The log level
     * @param message The message to log
     */
    public void log(Level level, String message) {
        getLogger().log(level, message);
    }

    /**
     * Logs a message with an exception to the plugin's logger.
     * 
     * @param level The log level
     * @param message The message to log
     * @param throwable The exception to log
     */
    public void log(Level level, String message, Throwable throwable) {
        getLogger().log(level, message, throwable);
    }
}
