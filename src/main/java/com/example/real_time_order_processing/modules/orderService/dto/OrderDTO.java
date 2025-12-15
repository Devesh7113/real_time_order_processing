package com.example.real_time_order_processing.modules.orderService.dto;

import com.example.real_time_order_processing.enums.OrderStatus;
import com.example.real_time_order_processing.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO
{
    private Long id;
    private Long userId;
    private List<OrderItemDTO> orderItems;
    private Double totalAmount;
    private OrderStatus orderStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private PaymentStatus paymentStatus;
    private String shippingAddress;
    private String billingAddress;
    private String paymentMethod;
    private String notes;

    public OrderDTO(Long id, Long userId, Double totalAmount, OrderStatus orderStatus, LocalDateTime createdAt, LocalDateTime updatedAt,
                    PaymentStatus paymentStatus, String shippingAddress, String billingAddress, String paymentMethod, String notes)
    {
        this.id = id;
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.orderStatus = orderStatus;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.paymentStatus = paymentStatus;
        this.shippingAddress = shippingAddress;
        this.billingAddress = billingAddress;
        this.paymentMethod = paymentMethod;
        this.notes = notes;
        this.orderItems = null;
    }
}
