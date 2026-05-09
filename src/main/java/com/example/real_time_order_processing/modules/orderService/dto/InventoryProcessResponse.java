package com.example.real_time_order_processing.modules.orderService.dto;

import lombok.Data;

import java.util.List;

@Data
public class InventoryProcessResponse
{
    private Long orderId;
    private List<InventoryProcessResponseData> productList;
}
