package com.raileasy.common.api;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiError> handleResponseStatusException(ResponseStatusException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.resolve(ex.getStatusCode().value());
        HttpStatus resolvedStatus = status == null ? HttpStatus.BAD_REQUEST : status;

        ApiError error = new ApiError(
                Instant.now(),
                request.getRequestURI(),
                resolvedStatus.name(),
                ex.getReason() == null ? "Request failed" : ex.getReason()
        );
        return ResponseEntity.status(ex.getStatusCode()).body(error);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidationException(Exception ex, HttpServletRequest request) {
        return new ApiError(
                Instant.now(),
                request.getRequestURI(),
                "VALIDATION_ERROR",
                ex.getMessage()
        );
    }

    @ExceptionHandler({MissingServletRequestParameterException.class, MethodArgumentTypeMismatchException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBadRequestException(Exception ex, HttpServletRequest request) {
        return new ApiError(
                Instant.now(),
                request.getRequestURI(),
                "BAD_REQUEST",
                ex.getMessage()
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleUnexpectedException(Exception ex, HttpServletRequest request) {
        return new ApiError(
                Instant.now(),
                request.getRequestURI(),
                "INTERNAL_SERVER_ERROR",
                "Unexpected error occurred"
        );
    }
}

