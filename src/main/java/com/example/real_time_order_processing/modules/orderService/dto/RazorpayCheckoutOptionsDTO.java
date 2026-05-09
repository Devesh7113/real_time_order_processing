package com.example.real_time_order_processing.modules.orderService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RazorpayCheckoutOptionsDTO
{
    private String keyId;
    private String razorpayOrderId;
    private long amount;
    private String currency;
}
