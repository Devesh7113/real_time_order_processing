package com.example.real_time_order_processing.modules.orderService.service;

import com.example.real_time_order_processing.kafka.PaymentCompleteDTO;
import com.example.real_time_order_processing.modules.orderService.dto.OrderCreateDTO;
import com.example.real_time_order_processing.modules.orderService.dto.OrderDTO;
import com.example.real_time_order_processing.modules.orderService.dto.OrderItemDTO;
import com.example.real_time_order_processing.modules.orderService.dto.OrderUpdateDTO;
import com.example.real_time_order_processing.modules.orderService.entity.Order;
import com.example.real_time_order_processing.modules.orderService.entity.OrderItem;

import java.util.List;

public interface OrderService
{
    List<OrderDTO> getAllOrder();

    List<OrderItemDTO> getOrder(Long id);

    void createOrder(OrderCreateDTO dto);

    void updateOder(OrderUpdateDTO dto);

    void deleteOrder(Long id);

    void paymentStatus(PaymentCompleteDTO dto);
}
