package com.example.real_time_order_processing.modules.productService.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
public class ProductCreateDTO
{
    @NotBlank(message = "Name can't be blank")
    @Pattern(regexp = "^[a-zA-Z0-9 ]+$")
    private String name;

    @NotBlank(message = "Description can't be blank")
    @Pattern(regexp = "^[a-zA-Z0-9_\\-@./#&+, ]+$")
    private String description;

    @Digits(integer = 8, fraction = 2, message = "Price must a positive number, 2 decimal places can be there.")
    @Min(value = 1, message = "Price must be 1 or greater than 1.")
    private Double price;

    @Digits(integer = 8, fraction = 0, message = "Quantity must a positive number without decimal places.")
    @Min(value = 1, message = "Quantity must be 1 or greater than 1.")
    private Integer stockQuantity;

    @NotBlank(message = "Category can't be blank")
    @Pattern(regexp = "^[a-zA-Z0-9 ]+$")
    private String category;

    @NotBlank(message = "Image url can't be blank")
    @URL(message = "Invalid URL formal")
    private String imageUrl;
}
