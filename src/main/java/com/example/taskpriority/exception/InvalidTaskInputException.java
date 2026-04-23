package com.example.taskpriority.exception;

public class InvalidTaskInputException extends RuntimeException {
    public InvalidTaskInputException(String message) {
        super(message);
    }
}

