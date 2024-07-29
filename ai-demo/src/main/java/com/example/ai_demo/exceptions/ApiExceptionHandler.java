package com.example.ai_demo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(value={ApiRequestException.class})
    public ResponseEntity<Object> handleApiRequestException(ApiRequestException apiRequestException){
        //1.create the payload containing the exception details

        ApiException apiException=  new ApiException(apiRequestException.getMessage(),apiRequestException,
                HttpStatus.BAD_REQUEST);

        //2. return ResponseEntity
        return new ResponseEntity<>(apiException,HttpStatus.BAD_REQUEST);


    }

    @ExceptionHandler(MissingPathVariableException.class)
    public ResponseEntity<String> handleMissingPathVariable(MissingPathVariableException ex)
    {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing required path variable: " + ex.getVariableName());
    }

}
