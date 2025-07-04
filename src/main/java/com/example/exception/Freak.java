package com.example.exception;

import lombok.Data;

@Data
public class Freak {

	private String message;

	public Freak(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}