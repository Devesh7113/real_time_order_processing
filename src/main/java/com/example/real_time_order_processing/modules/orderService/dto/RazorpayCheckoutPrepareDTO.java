package com.example.real_time_order_processing.modules.orderService.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RazorpayCheckoutPrepareDTO
{
    @NotNull
    private Long orderId;
}
