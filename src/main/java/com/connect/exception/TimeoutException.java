package com.connect.exception;

// Custom Exception for Timeout errors.

public class TimeoutException extends RuntimeException{
    public TimeoutException(String message) {
        super(message);
    }
}
