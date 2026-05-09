package com.example.real_time_order_processing.exception;

import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
public class GlobalExceptionHandler
{
    private static final String INVALID_LOGIN_MESSAGE = "Invalid email or password.";

    @ExceptionHandler({AuthenticationException.class})
    public ResponseEntity<Object> handleSpringAuthenticationException(AuthenticationException exception)
    {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(INVALID_LOGIN_MESSAGE);
    }

    @ExceptionHandler({UsernameNotFoundException.class})
    public ResponseEntity<Object> handleUsernameNotFoundException(UsernameNotFoundException exception)
    {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(INVALID_LOGIN_MESSAGE);
    }

    @ExceptionHandler({DataIntegrityViolationException.class})
    public ResponseEntity<Object> handleDataIntegrityViolationException(DataIntegrityViolationException exception)
    {
        String message = "A record with the same details already exists.";

        Throwable cause = exception.getRootCause();
        if (cause != null && cause.getMessage() != null)
        {
            String rootMsg = cause.getMessage().toLowerCase();
            if (rootMsg.contains("username"))
                message = "Username is already taken.";
            else if (rootMsg.contains("email"))
                message = "Email is already registered.";
        }

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(message);
    }

    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<Object> handleRuntimeException(RuntimeException exception)
    {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An unexpected error occurred. Please try again later.");
    }

    @ExceptionHandler({ResourceNotFoundException.class})
    public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(exception.getMessage());
    }

    @ExceptionHandler({ResponseStatusException.class})
    public ResponseEntity<Object> handleResponseStatusException(ResponseStatusException exception)
    {
        return ResponseEntity
                .status(exception.getStatusCode())
                .body(exception.getReason());
    }
}
