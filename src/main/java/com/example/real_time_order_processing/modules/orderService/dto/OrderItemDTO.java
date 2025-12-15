package com.example.real_time_order_processing.modules.orderService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDTO
{
    private Long id;
    private Long productId;
    private Long quantity;
    private Double price;
}