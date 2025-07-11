package com.boatsnbunnies.model;

/**
 * Represents a field in a Discord webhook embed.
 *
 * @since 1.0
 */
public class WebhookField {
    private final String name;
    private final String value;
    private final boolean inline;
    
    /**
     * Creates a new webhook field.
     *
     * @param name The name of the field
     * @param value The value of the field
     * @param inline Whether the field should be displayed inline
     */
    public WebhookField(String name, String value, boolean inline) {
        this.name = name;
        this.value = value;
        this.inline = inline;
    }
    
    /**
     * Gets the name of the field.
     *
     * @return The name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Gets the value of the field.
     *
     * @return The value
     */
    public String getValue() {
        return value;
    }
    
    /**
     * Checks if the field should be displayed inline.
     *
     * @return True if the field should be displayed inline, false otherwise
     */
    public boolean isInline() {
        return inline;
    }
    
    /**
     * Builder class for creating WebhookField instances.
     */
    public static class Builder {
        private String name;
        private String value;
        private boolean inline;
        
        /**
         * Sets the name of the field.
         *
         * @param name The name
         * @return This builder
         */
        public Builder name(String name) {
            this.name = name;
            return this;
        }
        
        /**
         * Sets the value of the field.
         *
         * @param value The value
         * @return This builder
         */
        public Builder value(String value) {
            this.value = value;
            return this;
        }
        
        /**
         * Sets whether the field should be displayed inline.
         *
         * @param inline True if the field should be displayed inline, false otherwise
         * @return This builder
         */
        public Builder inline(boolean inline) {
            this.inline = inline;
            return this;
        }
        
        /**
         * Builds the WebhookField instance.
         *
         * @return A new WebhookField instance
         */
        public WebhookField build() {
            return new WebhookField(name, value, inline);
        }
    }
}