package com.example.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.example.service.IEmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements IEmailService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public Mono<Void> sendOtpEmail(String toEmail, String otp) {
        return Mono.fromRunnable(() -> {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom(fromEmail);
                message.setTo(toEmail);
                message.setSubject("Your OTP Code");
                message.setText("Your OTP is: " + otp + ". It is valid for 1 minute.");
                
                javaMailSender.send(message);
                log.info("OTP email sent successfully to {}", toEmail);
            } catch (Exception e) {
                log.error("Failed to send OTP email to {}: {}", toEmail, e.getMessage(), e);
                throw new RuntimeException("Email sending failed for: " + toEmail, e);
            }
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }
}
