package com.example.real_time_order_processing.kafka;

import com.example.real_time_order_processing.modules.orderService.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentKafkaListener
{
    private final OrderService orderService;

    @KafkaListener(
            topics = "payment-completed",
            groupId = "payment-service-group",
            containerFactory = "paymentKafkaListenerContainerFactory"
    )
    public void onPaymentEvent(PaymentCompleteDTO event)
    {
        System.out.println("Received payment event: " + event);
        orderService.paymentStatus(event);
    }
}
