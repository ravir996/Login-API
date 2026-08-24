package com.example.service;

import com.example.constant.EmailFormat;

import reactor.core.publisher.Mono;

public interface IEmailService {
	
	public  Mono<Void> sendOtpEmail(String toEmail, String otp);

	Mono<Void> sendOtpEmail(String toEmail, String otp, EmailFormat format);

}
