package com.example.exception;

public class MimicDataException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private Freak freak;

	public MimicDataException(String message) {
		super(message);
		this.freak = new Freak(message);
	}

	public Freak getFreak() {
		return freak;
	}
}
