package com.example.real_time_order_processing.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserSummaryDTO
{
    private Long id;
    private String email;
    /** Registered display name from {@code user_info.username}, may be null. */
    private String username;
    /** Comma-separated roles as stored (e.g. {@code ROLE_USER,ROLE_ADMIN}). */
    private String roles;
}
