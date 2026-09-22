package com.ecommerce.Service.exceptionHandler;

import com.ecommerce.Service.dto.FieldErrorDetail;
import com.ecommerce.Service.dto.StockInsufficientErrorDTO;
import com.ecommerce.Service.exceptionHandler.exception.InsufficientStockException;
import com.ecommerce.Service.exceptionHandler.exception.InvalidQuantityException;
import com.ecommerce.Service.exceptionHandler.exception.InventoryNotFoundException;
import com.ecommerce.Service.exceptionHandler.exception.NoReservedStockException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private List<FieldErrorDetail> errors;

    @ExceptionHandler(jakarta.validation.ConstraintViolationException.class)
    public ResponseEntity<StockInsufficientErrorDTO> handleConstraintViolation(
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

        StockInsufficientErrorDTO response = StockInsufficientErrorDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Validation Failed")
                .message("Request validation failed")
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

    // INVENTORY NOT FOUND
    @ExceptionHandler(InventoryNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleInventoryNotFound(
            InventoryNotFoundException ex
    ) {

        Map<String, Object> error = new HashMap<>();

        error.put("success",false);
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.NOT_FOUND.value());
        error.put("error", HttpStatus.NOT_FOUND.getReasonPhrase());
        error.put("message", ex.getMessage());

        if(ex.getFailedProductIds() != null){
            error.put("failedProductIds", ex.getFailedProductIds());
        }

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    // INSUFFICIENT STOCK
    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<Map<String, Object>> handleInsufficientStock(
            InsufficientStockException ex
    ) {

        Map<String, Object> error = new HashMap<>();

        error.put("success",false);

        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.BAD_REQUEST.value());
        error.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());

        error.put("message", ex.getMessage());

        error.put("failedProductIds", ex.getFailedProductIds());

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // NO RESERVED STOCK
    @ExceptionHandler(NoReservedStockException.class)
    public ResponseEntity<Map<String, Object>> handleNoReservedStock(
            NoReservedStockException ex
    ) {

        Map<String, Object> error = new HashMap<>();

        error.put("success",false);

        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.CONFLICT.value());
        error.put("error", HttpStatus.CONFLICT.getReasonPhrase());

        error.put("message", ex.getMessage());

        error.put("failedProductIds", ex.getFailedProductIds());

        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    // INVALID QUANTITY
    @ExceptionHandler(InvalidQuantityException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidQuantity(
            InvalidQuantityException ex
    ) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
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
}