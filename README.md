# BnBWebhookAPI

A modular Discord Webhook API plugin for Spigot/Paper servers. This API allows other plugins to easily send Discord webhook messages with rich embeds.

## Features

- **Modular Design**: Separate plugin that other plugins can depend on
- **Rich Embeds**: Support for Discord webhook embeds with titles, descriptions, colors, fields, footers, and timestamps
- **Asynchronous**: Non-blocking webhook sending with CompletableFuture
- **Rate Limiting**: Built-in rate limiting to prevent Discord API abuse
- **Event System**: Events for webhook lifecycle (pre-send, sent, failed, rate-limited)
- **Configuration Management**: Easy management of webhook URLs
- **Thread-Safe**: Safe to use from multiple plugins simultaneously

## Installation

1. Download the latest release from the [Releases](https://github.com/boatsnbunnies/BnBWebhookAPI/releases) page
2. Place the JAR file in your server's `plugins` folder
3. Restart your server
4. Configure your webhooks in `plugins/BnBWebhookAPI/webhooks.yml`

## Configuration

### config.yml

```yaml
# BnBWebhookAPI Configuration

# Rate limiting settings
# These settings apply to all webhooks unless overridden in webhooks.yml
rate-limit:
  # Maximum number of requests per period
  requests: 5
  # Period in seconds
  period: 2

# Debug mode (enables additional logging)
debug: false
```

### webhooks.yml

```yaml
example-webhook:
  url: https://discord.com/api/webhooks/your-webhook-url
  rate-limit:
    requests: 5
    period: 2
```

## Usage

### Adding as a Dependency

#### Maven

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.Boats-N-Bunnies</groupId>
        <artifactId>BnBWebhookAPI</artifactId>
        <version>v1.1</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

#### Gradle

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    compileOnly 'com.github.boatsnbunnies:BnBWebhookAPI:1.0'
}
```

### plugin.yml

```yaml
depend: [BnBWebhookAPI]
# or
softdepend: [BnBWebhookAPI]
```

### Basic Usage

```java
// Get the API instance
BnBWebhookAPI api = BnBWebhookAPI.getInstance();

// Create a simple embed
WebhookEmbed embed = new WebhookEmbed.Builder()
        .title("Hello World")
        .description("This is a test message")
        .color(0xFF0000) // Red color
        .build();

// Send the webhook
api.getWebhookService()
        .send("webhook-name", null embed) // - null can be replaced with a string that is sent alongside the embed
        .thenAccept(response -> {
            if (response.isSuccess()) {
                // Handle success
                getLogger().info("Webhook sent successfully!");
            } else {
                // Handle failure
                getLogger().warning("Failed to send webhook: " + response.getMessage());
            }
        });
```

### Advanced Usage

See the [WebhookExample.java](src/main/java/com/boatsnbunnies/example/WebhookExample.java) file for a complete example of how to use the API.

## API Documentation

### Main Classes

- **BnBWebhookAPI**: Main plugin class and entry point for the API
- **WebhookService**: Service for sending webhooks and managing rate limits
- **WebhookEmbed**: Model class for Discord embeds
- **WebhookField**: Model class for embed fields
- **WebhookFooter**: Model class for embed footers
- **WebhookResponse**: Response from a webhook request
- **WebhookEvent**: Event fired during webhook lifecycle

### Creating Embeds

```java
// Simple embed
WebhookEmbed simpleEmbed = new WebhookEmbed.Builder()
        .title("Simple Embed")
        .description("This is a simple embed with just a title and description.")
        .color(0x00FF00) // Green color
        .build();

// Complex embed
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
```

### Sending Webhooks

```java
// Send a single embed - null can be replaced with a string that is sent alongside the embed
api.getWebhookService()
        .send("webhook-name", null, embed)
        .thenAccept(response -> {
            // Handle response
        });

// Send multiple embeds - null can be replaced with a string that is sent alongside the embed
api.getWebhookService()
        .send("webhook-name", null, Arrays.asList(embed1, embed2))
        .thenAccept(response -> {
            // Handle response
        });
```

### Managing Webhooks

```java
// Register a webhook
api.getWebhookService().registerWebhook("webhook-name", "https://discord.com/api/webhooks/your-webhook-url");

// Check if a webhook exists
boolean exists = api.getWebhookService().webhookExists("webhook-name");

// Unregister a webhook
api.getWebhookService().unregisterWebhook("webhook-name");

// Get all webhooks
Map<String, String> webhooks = api.getWebhookService().getWebhooks();
```

### Listening to Events

```java
@EventHandler
public void onWebhookEvent(WebhookEvent event) {
    switch (event.getEventType()) {
        case PRE_SEND:
            // Webhook is about to be sent
            break;
        case SENT:
            // Webhook was sent successfully
            break;
        case FAILED:
            // Webhook failed to send
            break;
        case RATE_LIMITED:
            // Webhook was rate limited
            break;
    }
}
```

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## Support

If you need help with this plugin, please open an issue on GitHub or contact us on Discord.