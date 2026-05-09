package com.example.real_time_order_processing.modules.orderService.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderCreateDTO
{
    private List<OrderItemDTO> orderItems;
    private String shippingAddress;
    private String notes;
}
