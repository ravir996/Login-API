package com.example.service;

import reactor.core.publisher.Mono;

public interface IEmailService {
	
	public  Mono<Void> sendOtpEmail(String toEmail, String otp);

}
