package com.example.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Freak {

	private String message;

	public Freak(String message) {
		this.message = message;
	}

	
}