package com.example.real_time_order_processing.modules.orderService.repository;

import com.example.real_time_order_processing.modules.orderService.dto.OrderItemDTO;
import com.example.real_time_order_processing.modules.orderService.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long>
{
    @Query("SELECT new com.example.real_time_order_processing.modules.orderService.dto.OrderItemDTO( oi.id, oi.productId, oi.quantity, oi.price ) " +
            "FROM OrderItem oi WHERE oi.order.id = :orderId")
    List<OrderItemDTO> findByOrderIdAsDto(Long orderId);
}
