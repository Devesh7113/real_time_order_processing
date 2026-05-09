package com.example.real_time_order_processing.modules.orderService.dto;

import lombok.Data;

@Data
public class InventoryProcessResponseData
{
    private Long productId;
    private Boolean available;
}
