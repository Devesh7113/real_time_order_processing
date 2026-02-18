package com.example.real_time_order_processing.auth.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AddNewUserRequest
{
    @NotBlank(message = "Username cannot be blank")
    @Size(min = 5, message = "Username must be at least 5 characters long")
    private String name;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotBlank
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[#@\\$!&*?_\\-])[A-Za-z\\d#@\\$!&*?_\\-]{8,}$"
    , message = "Password must be at least 8 characters long, contain at least one uppercase letter, one lowercase letter, one number, and one special character (such as #, @, $, !, &, *, ?, _, -).")
    private String password;
}
