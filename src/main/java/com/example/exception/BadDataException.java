package com.example.exception;

public class BadDataException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	private Freak freak;

	public BadDataException(String message) {
		super(message);
		this.freak = new Freak(message);
	}

	public Freak getFreak() {
		return freak;
	}
}