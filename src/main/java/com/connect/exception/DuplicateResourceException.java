package com.connect.exception;

public class DuplicateResourceException extends RuntimeException{

    public DuplicateResourceException() {
        super("EXCEPTION: Duplicate Resource Exists");
    }

    public DuplicateResourceException(String message) {
        super(message);
    }
}
