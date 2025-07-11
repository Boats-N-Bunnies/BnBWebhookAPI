package com.boatsnbunnies.model;

/**
 * Represents a footer in a Discord webhook embed.
 *
 * @since 1.0
 */
public class WebhookFooter {
    private final String text;
    private final String iconUrl;
    
    /**
     * Creates a new webhook footer.
     *
     * @param text The text of the footer
     * @param iconUrl The URL of the footer icon
     */
    public WebhookFooter(String text, String iconUrl) {
        this.text = text;
        this.iconUrl = iconUrl;
    }
    
    /**
     * Creates a new webhook footer with text only.
     *
     * @param text The text of the footer
     */
    public WebhookFooter(String text) {
        this(text, null);
    }
    
    /**
     * Gets the text of the footer.
     *
     * @return The text
     */
    public String getText() {
        return text;
    }
    
    /**
     * Gets the URL of the footer icon.
     *
     * @return The icon URL
     */
    public String getIconUrl() {
        return iconUrl;
    }
    
    /**
     * Builder class for creating WebhookFooter instances.
     */
    public static class Builder {
        private String text;
        private String iconUrl;
        
        /**
         * Sets the text of the footer.
         *
         * @param text The text
         * @return This builder
         */
        public Builder text(String text) {
            this.text = text;
            return this;
        }
        
        /**
         * Sets the URL of the footer icon.
         *
         * @param iconUrl The icon URL
         * @return This builder
         */
        public Builder iconUrl(String iconUrl) {
            this.iconUrl = iconUrl;
            return this;
        }
        
        /**
         * Builds the WebhookFooter instance.
         *
         * @return A new WebhookFooter instance
         */
        public WebhookFooter build() {
            return new WebhookFooter(text, iconUrl);
        }
    }
}