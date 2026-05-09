package com.example.real_time_order_processing.modules.orderService.dto;

import com.example.real_time_order_processing.enums.OrderStatus;
import com.example.real_time_order_processing.enums.PaymentStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
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
    private String notes;

    private Boolean customerEditUsed;

    public OrderDTO(Long id, Long userId, Double totalAmount, OrderStatus orderStatus, LocalDateTime createdAt, LocalDateTime updatedAt,
                    PaymentStatus paymentStatus, String shippingAddress, String notes,
                    Boolean customerEditUsed)
    {
        this.id = id;
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.orderStatus = orderStatus;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.paymentStatus = paymentStatus;
        this.shippingAddress = shippingAddress;
        this.notes = notes;
        this.customerEditUsed = customerEditUsed;
        this.orderItems = null;
    }
}
