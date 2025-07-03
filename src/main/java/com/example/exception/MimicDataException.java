package com.example.exception;

public class MimicDataException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
    public MimicDataException(String message) {
        super(message);
    }
}
