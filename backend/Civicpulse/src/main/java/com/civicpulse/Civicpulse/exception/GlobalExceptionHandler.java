package com.civicpulse.Civicpulse.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(
        MethodArgumentNotValidException ex
    ) {
        String message = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error ->
                error.getField() + ": " + error.getDefaultMessage()
            )
            .collect(Collectors.joining(", "));

        return ResponseEntity.badRequest().body(
            Map.of(
                "status", 400,
                "error", "Validation failed",
                "message", message
            )
        );
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleStatus(
        ResponseStatusException ex
    ) {
        HttpStatus status = HttpStatus.valueOf(
            ex.getStatusCode().value()
        );

        return ResponseEntity.status(status).body(
            Map.of(
                "status", status.value(),
                "error", status.getReasonPhrase(),
                "message", ex.getReason() == null
                    ? "Request failed"
                    : ex.getReason()
            )
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleOther(
        Exception ex
    ) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Map.of(
                "status", 500,
                "error", "Internal Server Error",
                "message", "An unexpected error occurred"
            ));
    }
}