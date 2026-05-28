package com.example.real_time_order_processing.client;

import com.example.real_time_order_processing.config.InventoryProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryWakeService
{
    private final RestClient.Builder restClientBuilder;
    private final InventoryProperties inventoryProperties;

    /**
     * Calls inventory /health until it responds or timeout. Wakes Render free-tier instances.
     */
    public void ensureAwake()
    {
        String baseUrl = inventoryProperties.getBaseUrl().replaceAll("/$", "");
        RestClient client = restClientBuilder.baseUrl(baseUrl).build();

        long deadlineMs = System.currentTimeMillis() + inventoryProperties.getWakeTimeoutSeconds() * 1000L;
        int attempt = 0;

        while (System.currentTimeMillis() < deadlineMs)
        {
            attempt++;
            try
            {
                client.get()
                        .uri("/health")
                        .retrieve()
                        .toBodilessEntity();
                log.info("Inventory service ready at {} (attempt {})", baseUrl, attempt);
                return;
            }
            catch (RestClientResponseException e)
            {
                log.warn("Inventory /health returned {} (attempt {})", e.getStatusCode(), attempt);
            }
            catch (Exception e)
            {
                log.debug("Inventory not ready yet (attempt {}): {}", attempt, e.getMessage());
            }

            long remainingMs = deadlineMs - System.currentTimeMillis();
            if (remainingMs <= 0)
            {
                break;
            }

            long sleepMs = Math.min(inventoryProperties.getWakeRetryIntervalMs(), remainingMs);
            try
            {
                Thread.sleep(sleepMs);
            }
            catch (InterruptedException e)
            {
                Thread.currentThread().interrupt();
                throw new ResponseStatusException(
                        HttpStatus.SERVICE_UNAVAILABLE,
                        "Interrupted while waiting for inventory service");
            }
        }

        log.error("Inventory service did not become ready at {} within {}s",
                baseUrl, inventoryProperties.getWakeTimeoutSeconds());
        throw new ResponseStatusException(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Inventory service is starting. Please try again in a moment.");
    }
}
