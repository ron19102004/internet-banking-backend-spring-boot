package com.ronial.internet_banking.common.exceptions;

import com.ronial.internet_banking.common.utils.ResponseLayout;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class ExceptionGlobal {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }
    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ResponseLayout<Object>> handler(ApplicationException e) {
        return ResponseLayout.builder()
                .message(e.getMessage())
                .code(e.getErrorCode())
                .success(false)
                .build()
                .toResponseEntity();
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ResponseLayout<Object>> handler(AuthException e) {
        return ResponseLayout.builder()
                .message(e.getMessage())
                .success(false)
                .code(e.getErrorCode())
                .build()
                .toResponseEntity();
    }
    @ExceptionHandler(RateLimitingException.class)
    public ResponseEntity<ResponseLayout<Object>> handler(RateLimitingException e) {
        return ResponseLayout.builder()
                .message(e.getMessage())
                .success(false)
                .code(e.getErrorCode())
                .build()
                .toResponseEntity();
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ResponseLayout<Object>> handler(AuthenticationException e) {
        return ResponseLayout.builder()
                .message(e.getMessage())
                .code(400)
                .success(false)
                .build()
                .toResponseEntity();
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ResponseLayout<Object>> handler(ValidationException e) {
        return ResponseLayout.builder()
                .message(e.getMessage())
                .code(e.getErrorCode())
                .success(false)
                .build()
                .toResponseEntity();
    }
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ResponseLayout<Object>> handler(UsernameNotFoundException e) {
        return ResponseLayout.builder()
                .message(e.getMessage())
                .code(400)
                .success(false)
                .build()
                .toResponseEntity();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseLayout<Object>> handleValidationException(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .toList();
        return ResponseLayout.builder()
                .message(errors.toString())
                .code(400)
                .success(false)
                .build()
                .toResponseEntity();
    }
}
