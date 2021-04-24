package com.xtra.api.exception.handler;

import com.xtra.api.exception.ActionNotAllowedException;
import com.xtra.api.exception.ApiError;
import com.xtra.api.exception.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;


@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    private ResponseEntity<Object> buildResponseEntity(ApiError apiError, HttpStatus status) {
        return new ResponseEntity<>(apiError, status);
    }

    @NonNull
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(@NonNull HttpMessageNotReadableException ex, @NonNull HttpHeaders headers, @NonNull HttpStatus status, @NonNull WebRequest request) {
        String error = "Malformed JSON request";
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();
        return buildResponseEntity(new ApiError(error, ex, path), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<Object> handleEntityNotFound(EntityNotFoundException ex, WebRequest request) {
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();
        ApiError apiError = new ApiError(path, "", ex.getMessage(), ex.getDetails());
        return buildResponseEntity(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ActionNotAllowedException.class)
    protected ResponseEntity<Object> handleUnsuccessfulOperation(ActionNotAllowedException ex, WebRequest request) {
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();
        ApiError apiError = new ApiError(path, ex.getErrorCode(), ex.getMessage(), "");
        return buildResponseEntity(apiError, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<?> handleValidationExceptions(ConstraintViolationException ex,WebRequest request){
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();
        ApiError apiError = new ApiError(path, "", "Input validation failed!", ex.getConstraintViolations());
        return buildResponseEntity(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    protected ResponseEntity<?> handleDuplicationExceptions(DataIntegrityViolationException ex, WebRequest request){
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();
        ApiError apiError = new ApiError(path, "", "Duplicate Error!", "");
        return buildResponseEntity(apiError, HttpStatus.CONFLICT);
    }

    /*@ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<Object> handleValidationException(MethodArgumentNotValidException exception, WebRequest request) {
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, path, "val-ch_name", exception.getMessage(), "");
        return buildResponseEntity(apiError);
    }*/

}
