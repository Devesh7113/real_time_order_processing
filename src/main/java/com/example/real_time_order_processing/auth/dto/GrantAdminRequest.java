package com.example.real_time_order_processing.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GrantAdminRequest
{
    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email address")
    private String email;
}
