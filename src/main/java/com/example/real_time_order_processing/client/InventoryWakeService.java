package com.example.real_time_order_processing.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@Slf4j
public class InventoryWakeService
{
    private final RestClient restClient;
    private final String healthUrl;
    private final long wakeTimeoutSeconds;
    private final long retryIntervalMs;

    public InventoryWakeService(
            @Value("${app.inventory.base-url}") String inventoryBaseUrl,
            @Value("${app.inventory.wake-timeout-seconds}") long wakeTimeoutSeconds,
            @Value("${app.inventory.wake-retry-interval-ms}") long retryIntervalMs)
    {
        this.healthUrl = inventoryBaseUrl.replaceAll("/$", "") + "/health";
        this.wakeTimeoutSeconds = wakeTimeoutSeconds;
        this.retryIntervalMs = retryIntervalMs;
        this.restClient = RestClient.create();
    }

    /**
     * Pings inventory /health on a background thread so callers are not blocked.
     */
    @Async
    public void ensureAwake()
    {
        log.info("Starting async inventory wake via {}", healthUrl);
        long deadline = System.currentTimeMillis() + wakeTimeoutSeconds * 1000L;
        int attempt = 0;

        while (System.currentTimeMillis() < deadline)
        {
            attempt++;
            try
            {
                ResponseEntity<String> response = restClient.get()
                        .uri(healthUrl)
                        .retrieve()
                        .toEntity(String.class);

                if (response.getStatusCode().is2xxSuccessful())
                {
                    log.info("Inventory service is awake after {} attempt(s), status={}",
                            attempt, response.getStatusCode());
                    return;
                }
                log.debug("Inventory health attempt {} returned status {}", attempt, response.getStatusCode());
            }
            catch (Exception e)
            {
                log.debug("Inventory health attempt {} failed: {}", attempt, e.getMessage());
            }

            try
            {
                Thread.sleep(retryIntervalMs);
            }
            catch (InterruptedException e)
            {
                Thread.currentThread().interrupt();
                log.warn("Inventory wake interrupted after {} attempt(s)", attempt);
                return;
            }
        }

        log.warn("Inventory wake timed out after {}s ({} attempts)", wakeTimeoutSeconds, attempt);
    }
}
