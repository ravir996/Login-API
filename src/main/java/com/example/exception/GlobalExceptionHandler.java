package com.example.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadDataException.class)
    public ResponseEntity<Freak> handleBadData(BadDataException ex) {
    	return throwException(HttpStatus.BAD_REQUEST, ex.getFreak());
    }
    
    @ExceptionHandler(value = MimicDataException.class)
	public ResponseEntity<Freak> exception(MimicDataException ex) {
		return throwException(HttpStatus.CONFLICT, ex.getFreak());
	}

    private ResponseEntity<Freak> throwException(HttpStatus status, Freak freak) {
        return new ResponseEntity<>(freak, status);
    }
}

