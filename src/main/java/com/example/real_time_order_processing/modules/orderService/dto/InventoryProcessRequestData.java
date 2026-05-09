package com.example.real_time_order_processing.modules.orderService.dto;

import lombok.Data;

@Data
public class InventoryProcessRequestData
{
    private Long productId;
    private Long quantity;
}
