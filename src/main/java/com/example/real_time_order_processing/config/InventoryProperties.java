package com.example.real_time_order_processing.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app.inventory")
public class InventoryProperties
{
    /**
     * Base URL of the inventory service (Render URL or http://localhost:9090 locally).
     */
    private String baseUrl = "http://localhost:9090";

    /**
     * Max time to wait for inventory to respond on /health after a cold start.
     */
    private int wakeTimeoutSeconds = 90;

    /**
     * Delay between /health retries while waking Render free tier.
     */
    private int wakeRetryIntervalMs = 5000;
}
