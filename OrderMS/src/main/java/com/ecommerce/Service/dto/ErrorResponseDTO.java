package com.ecommerce.Service.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;


@Data
@Builder
public class ErrorResponseDTO {

    private Boolean success;
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;

    private List<FieldErrorDetail> errors;
}

