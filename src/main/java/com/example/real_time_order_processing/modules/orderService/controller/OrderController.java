package com.example.real_time_order_processing.modules.orderService.controller;

import com.example.real_time_order_processing.modules.orderService.dto.OrderCreateDTO;
import com.example.real_time_order_processing.modules.orderService.dto.OrderDTO;
import com.example.real_time_order_processing.modules.orderService.dto.OrderUpdateDTO;
import com.example.real_time_order_processing.modules.orderService.service.OrderService;
import com.example.real_time_order_processing.modules.productService.dto.ProductDTO;
import com.example.real_time_order_processing.modules.productService.service.ProductService;
import com.example.real_time_order_processing.utils.ExceptionUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController
{
    private final ProductService productService;

    private final OrderService orderService;

    @GetMapping("/products")
    ResponseEntity<?> getAllProduct()
    {
        try
        {
            List<ProductDTO> productDTOList = productService.getAllProducts();

            return ResponseEntity.ok(productDTOList);
        }
        catch (Exception e)
        {
            return ExceptionUtils.handleException(e);
        }
    }

    @GetMapping()
    ResponseEntity<?> getAllOrders()
    {
        try
        {
            return ResponseEntity.ok(orderService.getAllOrder());
        }
        catch (Exception e)
        {
            return ExceptionUtils.handleException(e);
        }
    }

    @GetMapping("/items")
    ResponseEntity<?> getOrder(@RequestParam Long orderId)
    {
        try
        {
            return ResponseEntity.ok(orderService.getOrder(orderId));
        }
        catch (Exception e)
        {
            return ExceptionUtils.handleException(e);
        }
    }

    @PostMapping()
    ResponseEntity<?> addOrder(@RequestBody @Valid OrderCreateDTO dto)
    {
        try
        {
            orderService.createOrder(dto);
            return ResponseEntity.ok("Your order has been placed successfully.");
        }
        catch (Exception e)
        {
            return ExceptionUtils.handleException(e);
        }
    }

    @PutMapping()
    ResponseEntity<?> updateOrder(@RequestBody @Valid OrderUpdateDTO dto)
    {
        try
        {
            orderService.updateOder(dto);
            return ResponseEntity.ok("Your order has been updated successfully.");
        }
        catch (Exception e)
        {
            return ExceptionUtils.handleException(e);
        }
    }

    @DeleteMapping
    ResponseEntity<?> deleteOder(Long id)
    {
        try
        {
            orderService.deleteOrder(id);
            return ResponseEntity.ok("Your order has been deleted successfully.");
        }
        catch (Exception e)
        {
            return ExceptionUtils.handleException(e);
        }
    }

}
