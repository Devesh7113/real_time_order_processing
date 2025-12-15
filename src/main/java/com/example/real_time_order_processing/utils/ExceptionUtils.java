package com.example.real_time_order_processing.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.List;

@Slf4j
public class ExceptionUtils
{
    public static ResponseEntity<Response> handleException(Exception exception)
    {
        log.error("Exception occurred : ", exception);
        HttpStatus status =
                exception instanceof IllegalArgumentException ? HttpStatus.BAD_REQUEST :
                        exception instanceof AccessDeniedException ? HttpStatus.FORBIDDEN :
                                exception instanceof BadCredentialsException ? HttpStatus.UNAUTHORIZED :
                                        HttpStatus.INTERNAL_SERVER_ERROR;

        String message = exception.getMessage() != null ? exception.getMessage() : "Unexpected error, Please try again later.";

        Response resp = Response.builder()
                .status(status.value())
                .errors(List.of(message))
                .build();

        return ResponseEntity.status(status).body(resp);
    }
}
