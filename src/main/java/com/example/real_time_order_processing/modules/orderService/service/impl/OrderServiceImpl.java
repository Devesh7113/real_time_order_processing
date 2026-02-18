package com.example.real_time_order_processing.modules.orderService.service.impl;

import com.example.real_time_order_processing.auth.entity.UserInfo;
import com.example.real_time_order_processing.auth.repository.UserInfoRepository;
import com.example.real_time_order_processing.enums.OrderStatus;
import com.example.real_time_order_processing.enums.PaymentStatus;
import com.example.real_time_order_processing.kafka.PaymentEventDTO;
import com.example.real_time_order_processing.modules.orderService.dto.OrderCreateDTO;
import com.example.real_time_order_processing.modules.orderService.dto.OrderDTO;
import com.example.real_time_order_processing.modules.orderService.dto.OrderItemDTO;
import com.example.real_time_order_processing.modules.orderService.dto.OrderUpdateDTO;
import com.example.real_time_order_processing.modules.orderService.entity.Order;
import com.example.real_time_order_processing.modules.orderService.entity.OrderItem;
import com.example.real_time_order_processing.modules.orderService.repository.OrderItemRepository;
import com.example.real_time_order_processing.modules.orderService.repository.OrderRepository;
import com.example.real_time_order_processing.modules.orderService.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService
{
    private final OrderRepository orderRepository;

    private final OrderItemRepository orderItemRepository;

    private final UserInfoRepository userInfoRepository;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public List<OrderDTO> getAllOrder()
    {
        try
        {
            Long userId = getCurrentUserId();
            return orderRepository.findByUserId(userId);
        }
        catch (Exception e)
        {
            throw new RuntimeException("An error occurred while fetching all the order for the user.", e);
        }

    }

    @Override
    public List<OrderItemDTO> getOrder(Long orderId)
    {
        try
        {
            Optional<OrderDTO> orderOptional = orderRepository.findByOrderId(orderId);
            if(orderOptional.isEmpty())
            {
                throw new ResourceNotFoundException("No order found with this order id " + orderId);
            }

            OrderDTO orderDTO = orderOptional.get();

            return orderItemRepository.findByOrderIdAsDto(orderDTO.getId());
        }
        catch (Exception e)
        {
            throw new RuntimeException("An error occurred while fetching the order.", e);
        }
    }

    @Override
    @Transactional
    public void createOrder(OrderCreateDTO dto)
    {
        try
        {
            Order order = new Order();
            order.setUserId(getCurrentUserId());

            List<OrderItem> orderItemList = mapOrderItems(dto.getOrderItems());
            for(OrderItem orderItemDTO : orderItemList)
            {
                orderItemDTO.setOrder(order);
            }
            order.setOrderItems(orderItemList);

            order.setTotalAmount(calculateTotalAmount(dto.getOrderItems()));
            order.setOrderStatus(OrderStatus.NEW);
            order.setPaymentStatus(PaymentStatus.PENDING);
            order.setShippingAddress(dto.getShippingAddress());
            order.setBillingAddress(dto.getBillingAddress());
            order.setPaymentMethod(dto.getPaymentMethod());
            order.setNotes(dto.getNotes());

            orderRepository.save(order);

            orderCreatedKafkaEvent(order);
        }
        catch (Exception e)
        {
            throw new RuntimeException("An error occurred while creating the order.", e);
        }
    }
    
    void orderCreatedKafkaEvent(Order order)
    {
        PaymentEventDTO paymentEventDTO = new PaymentEventDTO(order.getId(), order.getTotalAmount());
        kafkaTemplate.send(
                MessageBuilder
                        .withPayload(paymentEventDTO)
                        .setHeader(KafkaHeaders.TOPIC, "order-created")
                        .setHeader("__TypeId__", PaymentEventDTO.class.getName())
                        .build()
        );
    }

    @Override
    public void updateOder(OrderUpdateDTO dto)
    {
        try
        {
            Optional<Order> dtoOptional = orderRepository.findById(dto.getId());

            if(dtoOptional.isEmpty())
            {
                throw new ResourceNotFoundException("No order found with the specified ID.");
            }

            Order order = dtoOptional.get();

            if(order.getOrderStatus() == OrderStatus.InTransit)
            {
                throw new RuntimeException("Order is currently In Transit and cannot be updated.");
            }


//            order.setOrderItems(mapOrderItems(dto.getOrderItems()));
//            order.setTotalAmount(calculateTotalAmount(dto.getOrderItems()));
//            order.setOrderStatus(OrderStatus.NEW);
//            order.setPaymentStatus(PaymentStatus.PENDING);
            order.setShippingAddress(dto.getShippingAddress());
//            order.setBillingAddress(dto.getBillingAddress());
//            order.setPaymentMethod(dto.getPaymentMethod());
            order.setNotes(dto.getNotes());

            orderRepository.save(order);
        }
        catch (Exception e)
        {
            throw new RuntimeException("An error occurred while updating the order.", e);
        }
    }

    @Override
    @Transactional
    public void deleteOrder(Long id)
    {
        try
        {
            Optional<Order> orderOptional = orderRepository.findById(id);

            if(orderOptional.isEmpty())
            {
                throw new ResourceNotFoundException(("No order found with the specified ID."));
            }

            Order order = orderOptional.get();

            if(order.getOrderStatus() == OrderStatus.InTransit)
            {
                throw new RuntimeException("Order is currently In Transit and cannot be canceled.");
            }

            orderRepository.deleteById(id);
        }
        catch (Exception e)
        {
            throw new RuntimeException("An error occurred while canceling the order.", e);
        }
    }

    private Double calculateTotalAmount(List<OrderItemDTO> dtoList)
    {
        Double totalAmount = 0.0;

        for(OrderItemDTO dto : dtoList)
        {
            totalAmount += dto.getPrice() * dto.getQuantity();
        }

        return totalAmount;
    }

    private List<OrderItem> mapOrderItems(List<OrderItemDTO> orderItemDTOList)
    {
        List<OrderItem> list = new ArrayList<>();
        for (OrderItemDTO dto : orderItemDTOList)
        {
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(dto.getProductId());
            orderItem.setQuantity(dto.getQuantity());
            orderItem.setPrice(dto.getPrice());

            list.add(orderItem);
        }

        return list;
    }

    private List<OrderItemDTO> mapOrderItemsToDTO(List<OrderItem> orderItems)
    {
        return orderItems.stream()
                .map(oi -> new OrderItemDTO(
                        oi.getId(),
                        oi.getProductId(),
                        oi.getQuantity(),
                        oi.getPrice()
                ))
                .collect(Collectors.toList());
    }


    private Long getCurrentUserId() throws AuthenticationException
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated())
        {
            throw new AuthenticationException("Cannot find the current user.");
        }

        String username = authentication.getName();

        Optional<UserInfo> userInfoOptional = userInfoRepository.findByEmail(username);

        if(userInfoOptional.isEmpty())
        {
            throw new ResourceNotFoundException("User is not found.");
        }

        UserInfo user = userInfoOptional.get();

        return user.getId();
    }
}
