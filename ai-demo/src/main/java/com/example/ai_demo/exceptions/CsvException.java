package com.example.ai_demo.exceptions;

public class CsvHeaderMismatchException extends RuntimeException{


    public CsvHeaderMismatchException( String message) {
        super(message);
    }

    public CsvHeaderMismatchException(String message, Throwable cause) {
        super(message, cause);
    }
}
