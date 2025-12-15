package com.example.real_time_order_processing.utils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {
    private int status;  // HTTP status code

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ", timezone = "IST")
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    private String message;       // Main error message
    private List<String> errors;  // Detailed error messages (optional)
}
