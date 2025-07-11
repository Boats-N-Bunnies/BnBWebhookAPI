package com.boatsnbunnies.service;

import com.boatsnbunnies.BnBWebhookAPI;
import com.boatsnbunnies.event.WebhookEvent;
import com.boatsnbunnies.model.WebhookEmbed;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import okhttp3.*;
import org.bukkit.Bukkit;

import java.awt.Color;
import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Level;

/**
 * Service for sending Discord webhooks.
 *
 * @since 1.0
 */
public class WebhookService {
    private final BnBWebhookAPI plugin;
    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final ScheduledExecutorService executorService;
    private final Map<String, RateLimiter> rateLimiters = new ConcurrentHashMap<>();

    /**
     * Creates a new webhook service.
     *
     * @param plugin The plugin instance
     */
    public WebhookService(BnBWebhookAPI plugin) {
        this.plugin = plugin;
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        this.objectMapper = new ObjectMapper();
        this.executorService = Executors.newScheduledThreadPool(2);
    }

    /**
     * Sends a webhook with a single embed.
     *
     * @param webhookName The name of the webhook
     * @param embed The embed to send
     * @return A CompletableFuture that will be completed with the response
     */
    public CompletableFuture<WebhookResponse> send(String webhookName, WebhookEmbed embed) {
        return send(webhookName, Collections.singletonList(embed));
    }

    /**
     * Sends a webhook with multiple embeds.
     *
     * @param webhookName The name of the webhook
     * @param embeds The embeds to send
     * @return A CompletableFuture that will be completed with the response
     */
    public CompletableFuture<WebhookResponse> send(String webhookName, List<WebhookEmbed> embeds) {
        CompletableFuture<WebhookResponse> future = new CompletableFuture<>();

        // Get webhook URL
        String webhookUrl = plugin.getWebhookConfig().getWebhookUrl(webhookName);
        if (webhookUrl == null) {
            WebhookResponse response = WebhookResponse.failure(404, "Webhook not found: " + webhookName);
            future.complete(response);
            return future;
        }

        // Check rate limit
        RateLimiter rateLimiter = getRateLimiter(webhookName);
        if (!rateLimiter.tryAcquire()) {
            WebhookResponse response = WebhookResponse.failure(429, "Rate limited");

            // Fire rate limited event
            Bukkit.getScheduler().runTask(plugin, () -> {
                WebhookEvent event = new WebhookEvent(webhookName, embeds, response, WebhookEvent.WebhookEventType.RATE_LIMITED);
                Bukkit.getPluginManager().callEvent(event);
            });

            future.complete(response);
            return future;
        }

        // Create JSON payload
        String json;
        try {
            json = createJsonPayload(embeds);
        } catch (JsonProcessingException e) {
            WebhookResponse response = WebhookResponse.failure(400, "Failed to create JSON payload: " + e.getMessage());
            future.complete(response);
            return future;
        }

        // Create request
        RequestBody body = RequestBody.create(MediaType.get("application/json"), json);
        Request request = new Request.Builder()
                .url(webhookUrl)
                .post(body)
                .build();

        // Fire pre-send event
        WebhookResponse preResponse = WebhookResponse.success(0, "Preparing to send webhook");
        Bukkit.getScheduler().runTask(plugin, () -> {
            WebhookEvent event = new WebhookEvent(webhookName, embeds, preResponse, WebhookEvent.WebhookEventType.PRE_SEND);
            Bukkit.getPluginManager().callEvent(event);
        });

        // Send request asynchronously
        executorService.submit(() -> {
            try {
                Response response = httpClient.newCall(request).execute();
                int statusCode = response.code();
                String message = response.message();
                response.close();

                boolean success = statusCode >= 200 && statusCode < 300;
                WebhookResponse webhookResponse = success
                        ? WebhookResponse.success(statusCode, message)
                        : WebhookResponse.failure(statusCode, message);

                // Fire event
                WebhookEvent.WebhookEventType eventType = success
                        ? WebhookEvent.WebhookEventType.SENT
                        : WebhookEvent.WebhookEventType.FAILED;

                Bukkit.getScheduler().runTask(plugin, () -> {
                    WebhookEvent event = new WebhookEvent(webhookName, embeds, webhookResponse, eventType);
                    Bukkit.getPluginManager().callEvent(event);
                });

                future.complete(webhookResponse);
            } catch (IOException e) {
                WebhookResponse webhookResponse = WebhookResponse.failure(500, "Failed to send webhook: " + e.getMessage());

                // Fire failed event
                Bukkit.getScheduler().runTask(plugin, () -> {
                    WebhookEvent event = new WebhookEvent(webhookName, embeds, webhookResponse, WebhookEvent.WebhookEventType.FAILED);
                    Bukkit.getPluginManager().callEvent(event);
                });

                future.complete(webhookResponse);
                plugin.log(Level.WARNING, "Failed to send webhook: " + e.getMessage(), e);
            }
        });

        return future;
    }

    /**
     * Creates a JSON payload for the webhook.
     *
     * @param embeds The embeds to include in the payload
     * @return The JSON payload
     * @throws JsonProcessingException If the JSON could not be created
     */
    private String createJsonPayload(List<WebhookEmbed> embeds) throws JsonProcessingException {
        ObjectNode rootNode = objectMapper.createObjectNode();
        ArrayNode embedsNode = rootNode.putArray("embeds");

        for (WebhookEmbed embed : embeds) {
            ObjectNode embedNode = embedsNode.addObject();

            // Add title and description
            if (embed.getTitle() != null) {
                embedNode.put("title", embed.getTitle());
            }
            if (embed.getDescription() != null) {
                embedNode.put("description", embed.getDescription());
            }

            // Add color
            if (embed.getColor() != null) {
                embedNode.put("color", embed.getColor());
            }

            // Add fields
            if (!embed.getFields().isEmpty()) {
                ArrayNode fieldsNode = embedNode.putArray("fields");
                for (var field : embed.getFields()) {
                    ObjectNode fieldNode = fieldsNode.addObject();
                    fieldNode.put("name", field.getName());
                    fieldNode.put("value", field.getValue());
                    fieldNode.put("inline", field.isInline());
                }
            }

            // Add footer
            if (embed.getFooter() != null) {
                ObjectNode footerNode = embedNode.putObject("footer");
                footerNode.put("text", embed.getFooter().getText());
                if (embed.getFooter().getIconUrl() != null) {
                    footerNode.put("icon_url", embed.getFooter().getIconUrl());
                }
            }

            // Add timestamp
            if (embed.getTimestamp() != null) {
                embedNode.put("timestamp", DateTimeFormatter.ISO_INSTANT.format(embed.getTimestamp()));
            }
        }

        return objectMapper.writeValueAsString(rootNode);
    }

    /**
     * Gets the rate limiter for a webhook.
     *
     * @param webhookName The name of the webhook
     * @return The rate limiter
     */
    private RateLimiter getRateLimiter(String webhookName) {
        return rateLimiters.computeIfAbsent(webhookName, name -> {
            int requests = plugin.getWebhookConfig().getRateLimit(name);
            int period = plugin.getWebhookConfig().getRateLimitPeriod(name);
            return new RateLimiter(requests, period, TimeUnit.SECONDS);
        });
    }

    /**
     * Shuts down the webhook service.
     */
    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Checks if a webhook exists.
     *
     * @param webhookName The name of the webhook
     * @return True if the webhook exists, false otherwise
     */
    public boolean webhookExists(String webhookName) {
        return plugin.getWebhookConfig().getWebhookUrl(webhookName) != null;
    }

    /**
     * Registers a webhook.
     *
     * @param webhookName The name of the webhook
     * @param webhookUrl The URL of the webhook
     * @return True if the webhook was registered, false if it already exists
     */
    public boolean registerWebhook(String webhookName, String webhookUrl) {
        return plugin.getWebhookConfig().registerWebhook(webhookName, webhookUrl);
    }

    /**
     * Unregisters a webhook.
     *
     * @param webhookName The name of the webhook
     * @return True if the webhook was unregistered, false if it doesn't exist
     */
    public boolean unregisterWebhook(String webhookName) {
        rateLimiters.remove(webhookName);
        return plugin.getWebhookConfig().unregisterWebhook(webhookName);
    }

    /**
     * Gets all registered webhooks.
     *
     * @return A map of webhook names to URLs
     */
    public Map<String, String> getWebhooks() {
        return plugin.getWebhookConfig().getWebhooks();
    }

    /**
     * A rate limiter that limits the number of requests per time period.
     */
    private static class RateLimiter {
        private final int maxRequests;
        private final long periodNanos;
        private final Queue<Long> requestTimestamps = new ConcurrentLinkedQueue<>();

        /**
         * Creates a new rate limiter.
         *
         * @param maxRequests The maximum number of requests per period
         * @param period The time period
         * @param unit The time unit of the period
         */
        public RateLimiter(int maxRequests, long period, TimeUnit unit) {
            this.maxRequests = maxRequests;
            this.periodNanos = unit.toNanos(period);
        }

        /**
         * Tries to acquire a permit from the rate limiter.
         *
         * @return True if a permit was acquired, false if the rate limit was exceeded
         */
        public boolean tryAcquire() {
            long now = System.nanoTime();
            long cutoff = now - periodNanos;

            // Remove expired timestamps
            while (!requestTimestamps.isEmpty() && requestTimestamps.peek() < cutoff) {
                requestTimestamps.poll();
            }

            // Check if we can make another request
            if (requestTimestamps.size() < maxRequests) {
                requestTimestamps.add(now);
                return true;
            }

            return false;
        }
    }
}
