package com.boatsnbunnies;

import com.boatsnbunnies.config.WebhookConfig;
import com.boatsnbunnies.model.WebhookEmbed;
import com.boatsnbunnies.service.WebhookResponse;
import com.boatsnbunnies.service.WebhookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the WebhookService class.
 */
public class WebhookServiceTest {

    @Mock
    private BnBWebhookAPI plugin;
    
    @Mock
    private WebhookConfig webhookConfig;
    
    @Mock
    private Logger logger;
    
    private WebhookService webhookService;
    
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Setup mocks
        when(plugin.getWebhookConfig()).thenReturn(webhookConfig);
        when(plugin.getLogger()).thenReturn(logger);
        
        // Create service
        webhookService = new WebhookService(plugin);
    }
    
    @Test
    public void testWebhookNotFound() throws ExecutionException, InterruptedException {
        // Setup
        String webhookName = "non-existent-webhook";
        when(webhookConfig.getWebhookUrl(webhookName)).thenReturn(null);
        
        WebhookEmbed embed = new WebhookEmbed.Builder()
                .title("Test")
                .description("Test Description")
                .build();
        
        // Execute
        CompletableFuture<WebhookResponse> future = webhookService.send(webhookName, null, embed);
        WebhookResponse response = future.get();
        
        // Verify
        assertFalse(response.isSuccess());
        assertEquals(404, response.getStatusCode());
        assertTrue(response.getMessage().contains("not found"));
    }
    
    @Test
    public void testRegisterWebhook() {
        // Setup
        String webhookName = "test-webhook";
        String webhookUrl = "https://discord.com/api/webhooks/test";
        when(webhookConfig.registerWebhook(webhookName, webhookUrl)).thenReturn(true);
        
        // Execute
        boolean result = webhookService.registerWebhook(webhookName, webhookUrl);
        
        // Verify
        assertTrue(result);
        verify(webhookConfig).registerWebhook(webhookName, webhookUrl);
    }
    
    @Test
    public void testUnregisterWebhook() {
        // Setup
        String webhookName = "test-webhook";
        when(webhookConfig.unregisterWebhook(webhookName)).thenReturn(true);
        
        // Execute
        boolean result = webhookService.unregisterWebhook(webhookName);
        
        // Verify
        assertTrue(result);
        verify(webhookConfig).unregisterWebhook(webhookName);
    }
    
    @Test
    public void testWebhookExists() {
        // Setup
        String webhookName = "test-webhook";
        when(webhookConfig.getWebhookUrl(webhookName)).thenReturn("https://discord.com/api/webhooks/test");
        
        // Execute
        boolean result = webhookService.webhookExists(webhookName);
        
        // Verify
        assertTrue(result);
        verify(webhookConfig).getWebhookUrl(webhookName);
    }
}