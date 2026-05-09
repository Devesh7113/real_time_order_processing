package com.example.real_time_order_processing.modules.orderService.controller;

import com.example.real_time_order_processing.enums.PaymentStatus;
import com.example.real_time_order_processing.kafka.PaymentCompleteDTO;
import com.example.real_time_order_processing.kafka.PaymentEventDTO;
import com.example.real_time_order_processing.modules.orderService.dto.RazorpayCheckoutOptionsDTO;
import com.example.real_time_order_processing.modules.orderService.dto.RazorpayCheckoutPrepareDTO;
import com.example.real_time_order_processing.modules.orderService.dto.RazorpayClientVerifyDTO;
import com.example.real_time_order_processing.modules.orderService.entity.Order;
import com.example.real_time_order_processing.modules.orderService.service.OrderService;
import com.example.real_time_order_processing.payment.RazorpayService;
import com.example.real_time_order_processing.utils.ExceptionUtils;
import com.razorpay.RazorpayException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders/razorpay")
@RequiredArgsConstructor
public class OrderRazorpayController
{
    private final OrderService orderService;

    private final RazorpayService razorpayService;

    @PostMapping("/checkout-options")
    public ResponseEntity<?> prepareCheckout(@RequestBody @Valid RazorpayCheckoutPrepareDTO dto)
    {
        try
        {
            Order order = orderService.requireOrderForCurrentUser(dto.getOrderId());

            if (order.getPaymentStatus() == PaymentStatus.SUCCESS)
            {
                return ResponseEntity.badRequest().body("This order is already paid.");
            }

            PaymentEventDTO payload = new PaymentEventDTO(order.getId(), order.getTotalAmount());
            RazorpayCheckoutOptionsDTO options = razorpayService.createCheckoutOrder(payload);
            return ResponseEntity.ok(options);
        }
        catch (ResourceNotFoundException e)
        {
            return ExceptionUtils.handleException(e);
        }
        catch (RazorpayException e)
        {
            return ResponseEntity.badRequest().body("Razorpay rejected checkout preparation.");
        }
        catch (Exception e)
        {
            return ExceptionUtils.handleException(e);
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verify(@RequestBody @Valid RazorpayClientVerifyDTO dto)
    {
        try
        {
            Order order = orderService.requireOrderForCurrentUser(dto.getOrderId());

            if (order.getPaymentStatus() == PaymentStatus.SUCCESS)
            {
                return ResponseEntity.ok("Payment already recorded.");
            }

            try
            {
                razorpayService.assertPaymentSignature(
                        dto.getRazorpayOrderId(),
                        dto.getRazorpayPaymentId(),
                        dto.getRazorpaySignature()
                );
            }
            catch (RazorpayException ex)
            {
                return ResponseEntity.badRequest().body("Payment verification failed.");
            }

            orderService.paymentStatus(new PaymentCompleteDTO(order.getId(), PaymentStatus.SUCCESS));
            return ResponseEntity.ok("Payment verified successfully.");
        }
        catch (ResourceNotFoundException e)
        {
            return ExceptionUtils.handleException(e);
        }
        catch (Exception e)
        {
            return ExceptionUtils.handleException(e);
        }
    }
}
