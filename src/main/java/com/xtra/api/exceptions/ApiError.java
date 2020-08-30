package com.xtra.api.exceptions;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ApiError {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime timestamp;
    private HttpStatus status;
    private String path;
    private String errorCode;
    private String message;
    private String details;
    private List<ApiSubError> subErrors;

    private ApiError() {
        timestamp = LocalDateTime.now();
    }

    ApiError(HttpStatus status) {
        this();
        this.status = status;
    }

    ApiError(HttpStatus status, Throwable ex) {
        this();
        this.status = status;
        this.message = "Unexpected error";
        this.details = ex.getLocalizedMessage();
    }

    ApiError(HttpStatus status, String message, Throwable ex, String path) {
        this();
        this.status = status;
        this.message = message;
        this.details = ex.getLocalizedMessage();
        this.path = path;
    }

    ApiError(HttpStatus status, String path, String errorCode, String message, String details) {
        this();
        this.status = status;
        this.message = message;
        this.errorCode = errorCode;
        this.details = details;
        this.path = path;
    }

}
