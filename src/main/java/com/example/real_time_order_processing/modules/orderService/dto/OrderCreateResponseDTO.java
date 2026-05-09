package com.example.real_time_order_processing.modules.orderService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateResponseDTO
{
    private String message;
    private Long orderId;
}
