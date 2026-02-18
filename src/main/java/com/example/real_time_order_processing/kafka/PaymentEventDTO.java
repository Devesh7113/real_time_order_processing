package com.example.real_time_order_processing.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentEventDTO
{
    private Long orderId;
    private Double totalAmount;
}
