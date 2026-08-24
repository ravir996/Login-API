package com.example.service.impl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.example.constant.EmailFormat;
import com.example.service.IEmailService;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements IEmailService {

    private static final String OTP_TEMPLATE_PATH = "activation.html";
    private static final String OTP_PLACEHOLDER = "{{OTP}}";

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public Mono<Void> sendOtpEmail(String toEmail, String otp) {
        return sendOtpEmail(toEmail, otp, null);
    }

    @Override
    public Mono<Void> sendOtpEmail(String toEmail, String otp, EmailFormat format) {
        return Mono.fromRunnable(() -> {
            try {
                EmailFormat resolvedFormat = format == null ? EmailFormat.TEXT : format;

                MimeMessage mimeMessage = javaMailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                helper.setFrom(fromEmail);
                helper.setTo(toEmail);
                helper.setSubject("Your OTP Code");

                switch (resolvedFormat) {
                    case HTML -> helper.setText(loadOtpTemplate(otp), true);
                    case TEXT -> helper.setText(buildPlainTextFallback(otp), false);
                }

                javaMailSender.send(mimeMessage);
                log.info("OTP email sent successfully to {} in {} format", toEmail, resolvedFormat);
            } catch (Exception e) {
                log.error("Failed to send OTP email to {}: {}", toEmail, e.getMessage(), e);
                throw new RuntimeException("Email sending failed for: " + toEmail, e);
            }
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }

    private String loadOtpTemplate(String otp) throws IOException {
        String template = new String(
                new ClassPathResource(OTP_TEMPLATE_PATH).getInputStream().readAllBytes(),
                StandardCharsets.UTF_8
        );
        return template
        		//.replace(OTP_PLACEHOLDER, otp)
        		.replace("{{user_name}}", "Ravi")
	            .replace("{{User_id}}", "123")
	            .replace("{{custom_link}}", "http://localhost:8080")
	            .replace("{{actId}}", "qaerfrew23Asde43DSAer")
	            .replace("{{id}}", "01")
	            .replace("{{User_name}}", "Ravi")
	            .replace("{{institutionId}}", "28");
    }

    private String buildPlainTextFallback(String otp) {
        return "Verify Your Account\n\n"
                + "Your OTP is: " + otp + "\n\n"
                + "This code is valid for 1 minute. If you didn't request this, you can safely ignore this email.";
    }
}
