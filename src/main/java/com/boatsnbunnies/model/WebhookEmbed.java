package com.boatsnbunnies.model;

import java.awt.Color;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a Discord webhook embed.
 * This class uses the Builder pattern for creating embeds.
 *
 * @since 1.0
 */
public class WebhookEmbed {
    private final String title;
    private final String description;
    private final Integer color;
    private final List<WebhookField> fields;
    private final WebhookFooter footer;
    private final Instant timestamp;
    
    private WebhookEmbed(Builder builder) {
        this.title = builder.title;
        this.description = builder.description;
        this.color = builder.color;
        this.fields = Collections.unmodifiableList(new ArrayList<>(builder.fields));
        this.footer = builder.footer;
        this.timestamp = builder.timestamp;
    }
    
    /**
     * Gets the title of the embed.
     *
     * @return The title
     */
    public String getTitle() {
        return title;
    }
    
    /**
     * Gets the description of the embed.
     *
     * @return The description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Gets the color of the embed.
     *
     * @return The color as an integer
     */
    public Integer getColor() {
        return color;
    }
    
    /**
     * Gets the fields of the embed.
     *
     * @return An unmodifiable list of fields
     */
    public List<WebhookField> getFields() {
        return fields;
    }
    
    /**
     * Gets the footer of the embed.
     *
     * @return The footer
     */
    public WebhookFooter getFooter() {
        return footer;
    }
    
    /**
     * Gets the timestamp of the embed.
     *
     * @return The timestamp
     */
    public Instant getTimestamp() {
        return timestamp;
    }
    
    /**
     * Builder class for creating WebhookEmbed instances.
     */
    public static class Builder {
        private String title;
        private String description;
        private Integer color;
        private List<WebhookField> fields = new ArrayList<>();
        private WebhookFooter footer;
        private Instant timestamp;
        
        /**
         * Sets the title of the embed.
         *
         * @param title The title
         * @return This builder
         */
        public Builder title(String title) {
            this.title = title;
            return this;
        }
        
        /**
         * Sets the description of the embed.
         *
         * @param description The description
         * @return This builder
         */
        public Builder description(String description) {
            this.description = description;
            return this;
        }
        
        /**
         * Sets the color of the embed using an integer value.
         *
         * @param color The color as an integer
         * @return This builder
         */
        public Builder color(int color) {
            this.color = color;
            return this;
        }
        
        /**
         * Sets the color of the embed using a Color object.
         *
         * @param color The color
         * @return This builder
         */
        public Builder color(Color color) {
            this.color = color.getRGB() & 0xFFFFFF;
            return this;
        }
        
        /**
         * Adds a field to the embed.
         *
         * @param field The field to add
         * @return This builder
         */
        public Builder addField(WebhookField field) {
            this.fields.add(field);
            return this;
        }
        
        /**
         * Adds a field to the embed.
         *
         * @param name The name of the field
         * @param value The value of the field
         * @param inline Whether the field should be inline
         * @return This builder
         */
        public Builder addField(String name, String value, boolean inline) {
            this.fields.add(new WebhookField(name, value, inline));
            return this;
        }
        
        /**
         * Sets the footer of the embed.
         *
         * @param footer The footer
         * @return This builder
         */
        public Builder footer(WebhookFooter footer) {
            this.footer = footer;
            return this;
        }
        
        /**
         * Sets the footer of the embed.
         *
         * @param text The footer text
         * @param iconUrl The footer icon URL
         * @return This builder
         */
        public Builder footer(String text, String iconUrl) {
            this.footer = new WebhookFooter(text, iconUrl);
            return this;
        }
        
        /**
         * Sets the timestamp of the embed.
         *
         * @param timestamp The timestamp
         * @return This builder
         */
        public Builder timestamp(Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }
        
        /**
         * Sets the timestamp of the embed to the current time.
         *
         * @return This builder
         */
        public Builder timestamp() {
            this.timestamp = Instant.now();
            return this;
        }
        
        /**
         * Builds the WebhookEmbed instance.
         *
         * @return A new WebhookEmbed instance
         */
        public WebhookEmbed build() {
            return new WebhookEmbed(this);
        }
    }
}