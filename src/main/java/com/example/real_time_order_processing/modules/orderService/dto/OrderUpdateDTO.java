package com.example.real_time_order_processing.modules.orderService.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderUpdateDTO
{
    @NotNull(message = "Order ID is required")
    Long id;

    @NotBlank(message = "Shipping address is required")
    private String shippingAddress;
}
