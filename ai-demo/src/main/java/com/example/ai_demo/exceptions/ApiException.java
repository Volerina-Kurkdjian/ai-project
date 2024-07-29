package com.example.ai_demo.exceptions;


import org.springframework.http.HttpStatus;

public class ApiException {
    private final String message;
    private final HttpStatus httpStatus;

    public ApiException(String message, ApiRequestException apiRequestException, HttpStatus httpStatus) {
        this.message = message;

        this.httpStatus = httpStatus;
    }

    public String getMessage() {
        return message;
    }



    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
