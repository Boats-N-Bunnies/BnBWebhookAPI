package com.boatsnbunnies.config;

import com.boatsnbunnies.BnBWebhookAPI;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/**
 * Configuration manager for Discord webhooks.
 *
 * @since 1.0
 */
public class WebhookConfig {
    private final BnBWebhookAPI plugin;
    private final Map<String, String> webhooks = new ConcurrentHashMap<>();
    private File configFile;
    private FileConfiguration config;
    
    // Default rate limit settings
    private int defaultRateLimit = 5; // requests per
    private int defaultRateLimitPeriod = 2; // seconds
    
    /**
     * Creates a new webhook configuration manager.
     *
     * @param plugin The plugin instance
     */
    public WebhookConfig(BnBWebhookAPI plugin) {
        this.plugin = plugin;
        loadConfig();
    }
    
    /**
     * Loads the configuration from disk.
     */
    public void loadConfig() {
        // Create default config if it doesn't exist
        plugin.saveDefaultConfig();
        
        // Load main config
        plugin.reloadConfig();
        FileConfiguration mainConfig = plugin.getConfig();
        
        // Load rate limit settings
        defaultRateLimit = mainConfig.getInt("rate-limit.requests", 5);
        defaultRateLimitPeriod = mainConfig.getInt("rate-limit.period", 2);
        
        // Load webhooks file
        configFile = new File(plugin.getDataFolder(), "webhooks.yml");
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
                // Create example webhook
                config = YamlConfiguration.loadConfiguration(configFile);
                config.set("example-webhook.url", "https://discord.com/api/webhooks/your-webhook-url");
                config.set("example-webhook.rate-limit.requests", 5);
                config.set("example-webhook.rate-limit.period", 2);
                config.save(configFile);
            } catch (IOException e) {
                plugin.log(Level.SEVERE, "Could not create webhooks.yml", e);
            }
        }
        
        // Load webhooks
        config = YamlConfiguration.loadConfiguration(configFile);
        loadWebhooks();
    }
    
    /**
     * Loads webhooks from the configuration.
     */
    private void loadWebhooks() {
        webhooks.clear();
        
        for (String key : config.getKeys(false)) {
            if (config.isConfigurationSection(key)) {
                String url = config.getString(key + ".url");
                if (url != null && !url.isEmpty()) {
                    webhooks.put(key, url);
                }
            }
        }
        
        plugin.log(Level.INFO, "Loaded " + webhooks.size() + " webhooks");
    }
    
    /**
     * Saves the configuration to disk.
     */
    public void saveConfig() {
        if (config == null || configFile == null) {
            return;
        }
        
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.log(Level.SEVERE, "Could not save webhooks.yml", e);
        }
    }
    
    /**
     * Registers a webhook.
     *
     * @param name The name of the webhook
     * @param url The URL of the webhook
     * @return True if the webhook was registered, false if it already exists
     */
    public boolean registerWebhook(String name, String url) {
        if (webhooks.containsKey(name)) {
            return false;
        }
        
        webhooks.put(name, url);
        config.set(name + ".url", url);
        saveConfig();
        return true;
    }
    
    /**
     * Unregisters a webhook.
     *
     * @param name The name of the webhook
     * @return True if the webhook was unregistered, false if it doesn't exist
     */
    public boolean unregisterWebhook(String name) {
        if (!webhooks.containsKey(name)) {
            return false;
        }
        
        webhooks.remove(name);
        config.set(name, null);
        saveConfig();
        return true;
    }
    
    /**
     * Gets the URL of a webhook.
     *
     * @param name The name of the webhook
     * @return The URL of the webhook, or null if it doesn't exist
     */
    public String getWebhookUrl(String name) {
        return webhooks.get(name);
    }
    
    /**
     * Gets all registered webhooks.
     *
     * @return A map of webhook names to URLs
     */
    public Map<String, String> getWebhooks() {
        return new HashMap<>(webhooks);
    }
    
    /**
     * Gets the rate limit for a webhook.
     *
     * @param name The name of the webhook
     * @return The rate limit in requests per period
     */
    public int getRateLimit(String name) {
        if (config.isConfigurationSection(name)) {
            ConfigurationSection section = config.getConfigurationSection(name);
            if (section.isConfigurationSection("rate-limit")) {
                return section.getInt("rate-limit.requests", defaultRateLimit);
            }
        }
        return defaultRateLimit;
    }
    
    /**
     * Gets the rate limit period for a webhook.
     *
     * @param name The name of the webhook
     * @return The rate limit period in seconds
     */
    public int getRateLimitPeriod(String name) {
        if (config.isConfigurationSection(name)) {
            ConfigurationSection section = config.getConfigurationSection(name);
            if (section.isConfigurationSection("rate-limit")) {
                return section.getInt("rate-limit.period", defaultRateLimitPeriod);
            }
        }
        return defaultRateLimitPeriod;
    }
    
    /**
     * Gets the default rate limit.
     *
     * @return The default rate limit in requests per period
     */
    public int getDefaultRateLimit() {
        return defaultRateLimit;
    }
    
    /**
     * Gets the default rate limit period.
     *
     * @return The default rate limit period in seconds
     */
    public int getDefaultRateLimitPeriod() {
        return defaultRateLimitPeriod;
    }
}