package com.example.real_time_order_processing.kafka;

import com.example.real_time_order_processing.modules.orderService.dto.InventoryProcessResponse;
import com.example.real_time_order_processing.modules.orderService.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InventoryKafkaListener
{
    private final OrderService orderService;

    @KafkaListener(
            topics = "inventory-process-completed",
            containerFactory = "inventoryKafkaListenerContainerFactory"
    )
    public void onInventoryProcessComplete(InventoryProcessResponse response)
    {
        orderService.processInventoryResponse(response);
    }
}
