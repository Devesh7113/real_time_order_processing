package com.example.real_time_order_processing.modules.orderService.service.impl;

import com.example.real_time_order_processing.auth.entity.UserInfo;
import com.example.real_time_order_processing.auth.repository.UserInfoRepository;
import com.example.real_time_order_processing.enums.OrderStatus;
import com.example.real_time_order_processing.enums.PaymentStatus;
import com.example.real_time_order_processing.kafka.PaymentCompleteDTO;
import com.example.real_time_order_processing.modules.orderService.dto.*;
import com.example.real_time_order_processing.modules.orderService.entity.Order;
import com.example.real_time_order_processing.modules.orderService.entity.OrderItem;
import com.example.real_time_order_processing.modules.orderService.repository.OrderItemRepository;
import com.example.real_time_order_processing.modules.orderService.repository.OrderRepository;
import com.example.real_time_order_processing.modules.orderService.service.OrderService;
import com.example.real_time_order_processing.modules.productService.entity.Product;
import com.example.real_time_order_processing.modules.productService.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService
{
    private final OrderRepository orderRepository;

    private final OrderItemRepository orderItemRepository;

    private final UserInfoRepository userInfoRepository;

    private final ProductRepository productRepository;

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
            Long userId = getCurrentUserId();
            Order orderEntity = orderRepository.findById(orderId)
                    .orElseThrow(() -> new ResourceNotFoundException("No order found with this order id " + orderId));
            if (!orderEntity.getUserId().equals(userId))
            {
                throw new ResourceNotFoundException("No order found with this order id " + orderId);
            }

            return orderItemRepository.findByOrderIdAsDto(orderId);
        }
        catch (ResourceNotFoundException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new RuntimeException("An error occurred while fetching the order.", e);
        }
    }

    @Override
    public Order requireOrderForCurrentUser(Long orderId)
    {
        try
        {
            Long userId = getCurrentUserId();
            Order orderEntity = orderRepository.findById(orderId)
                    .orElseThrow(() -> new ResourceNotFoundException("No order found with this order id " + orderId));
            if (!orderEntity.getUserId().equals(userId))
            {
                throw new ResourceNotFoundException("No order found with this order id " + orderId);
            }
            return orderEntity;
        }
        catch (ResourceNotFoundException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new RuntimeException("An error occurred while fetching the order.", e);
        }
    }

    @Override
    @Transactional
    public Long createOrder(OrderCreateDTO dto)
    {
        try
        {
            Order order = new Order();
            order.setUserId(getCurrentUserId());

            List<OrderItem> orderItemList = mapOrderItems(dto.getOrderItems());
            for (OrderItem orderItemDTO : orderItemList)
            {
                orderItemDTO.setOrder(order);
            }
            order.setOrderItems(orderItemList);

            order.setTotalAmount(calculateTotalAmount(dto.getOrderItems()));
            order.setOrderStatus(OrderStatus.PROCESSING);
            order.setPaymentStatus(PaymentStatus.PENDING);
            order.setShippingAddress(dto.getShippingAddress());
            order.setNotes(dto.getNotes());

            orderRepository.save(order);

            List<InventoryProcessRequestData> productList = orderItemList.stream()
                    .map(item -> {
                        InventoryProcessRequestData check = new InventoryProcessRequestData();
                        check.setProductId(item.getProductId());
                        check.setQuantity(item.getQuantity());
                        return check;
                    })
                    .toList();
            processInventory(new InventoryProcessRequest(order.getId(), productList));

            return order.getId();
        }
        catch (Exception e)
        {
            throw new RuntimeException("An error occurred while creating the order.", e);
        }
    }

    void processInventory(InventoryProcessRequest productList)
    {
        kafkaTemplate.send("process-inventory", productList);
    }

    public void processInventoryResponse(InventoryProcessResponse productList)
    {
        Optional<Order> orderOptional = orderRepository.findById(productList.getOrderId());
        if (orderOptional.isEmpty())
        {
            log.error("No order found with this order id in processInventoryResponse " + productList.getOrderId());
            return;
        }

        Order order = orderOptional.get();
        OrderStatus orderStatus = OrderStatus.CONFIRMED;
        for(InventoryProcessResponseData data : productList.getProductList())
        {
            if(Boolean.FALSE.equals(data.getAvailable()))
            {
                orderStatus = OrderStatus.ACTION_NEEDED;
            }
        }

        order.setOrderStatus(orderStatus);
        orderRepository.save(order);
    }

    @Override
    @Transactional
    public void updateOder(OrderUpdateDTO dto)
    {
        try
        {
            Long userId = getCurrentUserId();
            Order order = orderRepository.findById(dto.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("No order found with the specified ID."));

            if (!order.getUserId().equals(userId))
            {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot update this order.");
            }
            if (order.isCustomerEditUsed())
            {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "This order has already been edited once.");
            }
            if (isOrderLockedForAddressUpdate(order.getOrderStatus()))
            {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "This order cannot be updated in its current status.");
            }

            String ship = dto.getShippingAddress() != null ? dto.getShippingAddress().trim() : "";
            if (ship.isBlank())
            {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Shipping address is required.");
            }

            order.setShippingAddress(ship);
            order.setCustomerEditUsed(true);
            orderRepository.save(order);
        }
        catch (ResponseStatusException | ResourceNotFoundException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new RuntimeException("An error occurred while updating the order.", e);
        }
    }

    @Override
    public void paymentStatus(PaymentCompleteDTO dto)
    {
        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("No order found with the specified ID."));

        order.setPaymentStatus(dto.getPaymentStatus());
        orderRepository.save(order);
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

    private static double calculateTotalAmountFromOrderItems(List<OrderItem> list)
    {
        double total = 0.0;
        for (OrderItem oi : list)
        {
            double price = oi.getPrice() != null ? oi.getPrice() : 0.0;
            long qty = oi.getQuantity() != null ? oi.getQuantity() : 0L;
            total += price * qty;
        }
        return total;
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


    /** Address updates only while order is not in terminal / shipped state. */
    private static boolean isOrderLockedForAddressUpdate(OrderStatus status)
    {
        return status == OrderStatus.IN_TRANSIT
                || status == OrderStatus.SHIPPED
                || status == OrderStatus.CANCELLED;
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
