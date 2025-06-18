package com.connect.exception;

import java.util.Map;

import com.mongodb.MongoTimeoutException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<Map<String, String>> handleDuplicateResource(DuplicateResourceException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("response", ex.getMessage()));
    }

    @ExceptionHandler(InvalidUserException.class)
    public ResponseEntity<Map<String, String>> handleInvalidUser(InvalidUserException ex) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(Map.of("response", ex.getMessage()));
    }

    @ExceptionHandler(UserCreationException.class)
    public ResponseEntity<Map<String, String>> handleUserCreationException(UserCreationException ex) {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Map.of("response", ex.getMessage()));
    }

    @ExceptionHandler(MongoTimeoutException.class)
    public ResponseEntity<Map<String, String>> mongodbTimeoutExceptionHandler() {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("response", "Something went wrong at the Server! Try again later."));
    }

    @ExceptionHandler(TimeoutException.class)
    public ResponseEntity<Map<String, String>> handleTimeoutException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.GATEWAY_TIMEOUT)
                .body(Map.of("response", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleOtherExceptions(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("response", ex.getMessage()));
    }

}
