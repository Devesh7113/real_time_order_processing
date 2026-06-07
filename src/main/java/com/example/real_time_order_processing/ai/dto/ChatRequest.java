package com.example.real_time_order_processing.ai.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChatRequest
{
    @NotBlank(message = "message is required")
    private String message;
}
