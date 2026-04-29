package com.verveguard.sidecar.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RateLimitException.class)
    public ResponseEntity<Map<String, Object>> handleRateLimitException(RateLimitException ex) {

        Map<String, Object> response = new HashMap<>();
        response.put("status", "BLOCKED_RATE_LIMIT");
        response.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(response);
    }
}
