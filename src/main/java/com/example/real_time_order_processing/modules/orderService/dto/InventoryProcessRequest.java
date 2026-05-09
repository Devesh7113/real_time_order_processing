package com.example.real_time_order_processing.modules.orderService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryProcessRequest
{
    private Long orderId;
    private List<InventoryProcessRequestData> productList;
}
