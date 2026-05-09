package com.example.real_time_order_processing.modules.orderService.repository;

import com.example.real_time_order_processing.modules.orderService.dto.OrderDTO;
import com.example.real_time_order_processing.modules.orderService.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long>
{
    Optional<Order> findById(Long id);

    @Query("SELECT new com.example.real_time_order_processing.modules.orderService.dto.OrderDTO( o.id, o.userId, o.totalAmount, " +
            "o.orderStatus, o.createdAt, o.updatedAt, o.paymentStatus, o.shippingAddress, o.notes, " +
            "o.customerEditUsed ) " +
            "FROM Order o WHERE o.userId = :userId")
    List<OrderDTO> findByUserId(Long userId);
}
