package com.ecommerce.Service.exceptionHandler;

import com.ecommerce.Service.dto.ErrorResponseDTO;
import com.ecommerce.Service.dto.FieldErrorDetail;
import com.ecommerce.Service.dto.ErrorResponseDTO;
import com.ecommerce.Service.exceptionHandler.exception.OrderNotFoundException;
import feign.FeignException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private List<FieldErrorDetail> errors;

    @Autowired
    private ObjectMapper objectMapper;

    // request body coming in list
    @ExceptionHandler(jakarta.validation.ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDTO> handleConstraintViolation(
            ConstraintViolationException ex
    ) {

        errors = ex.getConstraintViolations()
                .stream()
                .map(v -> FieldErrorDetail.builder()
                        .field(v.getPropertyPath().toString())
                        .message(v.getMessage())
                        .build()
                )
                .toList();

        ErrorResponseDTO response = ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Validation Failed")
                .message("Request body validation failed")
                .errors(errors)
                .success(false)
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // invalid DTO fields (not list type)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(
            MethodArgumentNotValidException ex
    ) {

        Map<String, Object> error = new HashMap<>();

        error.put("timestamp", LocalDateTime.now());
        error.put("status", 400);
        error.put("error", "Bad Request");

        Map<String, String> validationErrors = new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(fieldError -> {
                    validationErrors.put(
                            fieldError.getField(),
                            fieldError.getDefaultMessage()
                    );
                });

        error.put("messages", validationErrors);

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // Order NOT FOUND
    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleInventoryNotFound(
            OrderNotFoundException ex
    ) {

        Map<String, Object> error = new HashMap<>();

        error.put("success",false);
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.NOT_FOUND.value());
        error.put("error", HttpStatus.NOT_FOUND.getReasonPhrase());
        error.put("message", ex.getMessage());

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }


    // rest template exception - for now INVENTORY Errors
    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<Map<String, Object>> handleInventoryNotFound(
            HttpClientErrorException ex
    ) {

        Map<String, Object> error;
        try {

            error =
                    objectMapper.readValue(
                            ex.getResponseBodyAsString(),
                            Map.class
                    );

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse inventory error. "+e.getMessage());
        }

        return new ResponseEntity<>(error,ex.getStatusCode());
    }

    // open feign exception
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<Map<String, Object>> handleFeignInventoryNotFound(
            FeignException ex
    ) {

        Map<String, Object> error;
        try {

            error =
                    objectMapper.readValue(
                            ex.contentUTF8(),
                            Map.class
                    );

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse inventory error. "+e.getMessage());
        }

        return new ResponseEntity<>(error,HttpStatus.valueOf(ex.status()));
    }

    // COMMON RESPONSE BUILDER
    private ResponseEntity<Map<String, Object>> buildErrorResponse(
            RuntimeException ex,
            HttpStatus status
    ) {

        Map<String, Object> error = new HashMap<>();

        error.put("success",false);

        error.put("timestamp", LocalDateTime.now());
        error.put("status", status.value());
        error.put("error", status.getReasonPhrase());
        error.put("message", ex.getMessage());


        return new ResponseEntity<>(error, status);
    }


    // unknown errors
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handle(Exception ex) {

        Map<String, Object> error = new HashMap<>();

        error.put("success",false);
        error.put("timestamp", LocalDateTime.now());
        error.put("status", 500);
        error.put("error", "Internal Server Error");
        error.put("message", ex.getMessage());

        return ResponseEntity.status(500).body(error);
    }

}