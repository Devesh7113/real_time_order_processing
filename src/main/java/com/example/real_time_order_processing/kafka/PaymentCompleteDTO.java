package com.example.real_time_order_processing.kafka;

import com.example.real_time_order_processing.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentCompleteDTO
{
    private Long orderId;
    private PaymentStatus paymentStatus;
}
