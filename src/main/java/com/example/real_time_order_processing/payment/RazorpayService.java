package com.example.real_time_order_processing.payment;

import com.example.real_time_order_processing.kafka.PaymentEventDTO;
import com.example.real_time_order_processing.modules.orderService.dto.RazorpayCheckoutOptionsDTO;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RazorpayService
{
    @Value("${razorpay.key}")
    private String razorpayKeyId;

    @Value("${razorpay.key_secret}")
    private String razorpayKeySecret;

    public RazorpayCheckoutOptionsDTO createCheckoutOrder(PaymentEventDTO dto) throws RazorpayException
    {
        RazorpayClient razorpay = new RazorpayClient(razorpayKeyId, razorpayKeySecret);

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", Math.round(dto.getTotalAmount() * 100));
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", String.valueOf(dto.getOrderId()));

        Order order = razorpay.orders.create(orderRequest);
        JSONObject json = order.toJson();

        String razorpayOrderId = json.optString("id", null);
        long amount = json.has("amount") && !json.isNull("amount") ? json.getLong("amount") : 0L;
        String currency = json.optString("currency", "INR");

        return new RazorpayCheckoutOptionsDTO(razorpayKeyId, razorpayOrderId, amount, currency);
    }

    public void assertPaymentSignature(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature)
            throws RazorpayException
    {
        JSONObject attributes = new JSONObject();
        attributes.put("razorpay_order_id", razorpayOrderId);
        attributes.put("razorpay_payment_id", razorpayPaymentId);
        attributes.put("razorpay_signature", razorpaySignature);
        Utils.verifyPaymentSignature(attributes, razorpayKeySecret);
    }
}

