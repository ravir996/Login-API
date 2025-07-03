package com.example.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadDataException.class)
    public ResponseEntity<String> handleBadData(BadDataException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
    
    @ExceptionHandler(MimicDataException.class)
    public ResponseEntity<String> handleMimicDataException(MimicDataException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}

