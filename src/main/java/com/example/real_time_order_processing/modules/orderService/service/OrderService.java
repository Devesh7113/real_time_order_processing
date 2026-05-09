package com.example.real_time_order_processing.modules.orderService.service;

import com.example.real_time_order_processing.kafka.PaymentCompleteDTO;
import com.example.real_time_order_processing.modules.orderService.dto.*;
import com.example.real_time_order_processing.modules.orderService.entity.Order;
import com.example.real_time_order_processing.modules.orderService.entity.OrderItem;

import java.util.List;

public interface OrderService
{
    List<OrderDTO> getAllOrder();

    List<OrderItemDTO> getOrder(Long id);

    Order requireOrderForCurrentUser(Long orderId);

    Long createOrder(OrderCreateDTO dto);

    void updateOder(OrderUpdateDTO dto);

    void paymentStatus(PaymentCompleteDTO dto);

    void processInventoryResponse(InventoryProcessResponse productList);
}
