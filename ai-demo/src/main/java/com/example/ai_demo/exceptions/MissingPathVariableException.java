package com.example.ai_demo.exceptions;

public class MissingPathVariableException extends RuntimeException{

    public MissingPathVariableException(String message) {
        super(message);
    }

    public MissingPathVariableException(String message, Throwable cause) {
        super(message, cause);
    }
}
