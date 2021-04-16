package com.xtra.api.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import javax.validation.ConstraintViolation;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime timestamp;
    private String path;
    private String errorCode;
    private String message;
    private String details;
    private List<String> validationErrors;

    private ApiError() {
        timestamp = LocalDateTime.now();
    }

    ApiError(Throwable ex) {
        this();
        this.message = "Unexpected error";
        this.details = ex.getLocalizedMessage();
    }

    ApiError(String message, Throwable ex, String path) {
        this();
        this.message = message;
        this.details = ex.getLocalizedMessage();
        this.path = path;
    }

    ApiError(String path, String errorCode, String message, String details) {
        this();
        this.message = message;
        this.errorCode = errorCode;
        this.details = details;
        this.path = path;
    }

    ApiError(String path, String errorCode, String message, Set<ConstraintViolation<?>> violations) {
        this();
        this.message = message;
        this.errorCode = errorCode;
        this.path = path;
        this.validationErrors = violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.toList());
    }

}
