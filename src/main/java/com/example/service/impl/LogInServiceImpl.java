package com.example.service.impl;

import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.constant.Status;
import com.example.domain.mapper.IUserMapper;
import com.example.domain.vo.LoginVO;
import com.example.domain.vo.SecretVO;
import com.example.domain.vo.UserVO;
import com.example.entity.Secret;
import com.example.entity.User;
import com.example.exception.BadDataException;
import com.example.repo.ISecretRepo;
import com.example.repo.IUserRepo;
import com.example.security.jwt.JwtUtil;
import com.example.service.IEmailService;
import com.example.service.ILogInService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogInServiceImpl implements ILogInService {

    private final IUserRepo userRepo;
    
    private final JwtUtil jwtUtil;
    
    private final PasswordEncoder passwordEncoder;
    
    private final ISecretRepo secretRepo;
    
    private final IEmailService emailService;
    
    private final IUserMapper mapper;

    @Override
    public Mono<SecretVO> login(LoginVO vo) {
        log.info("Login attempt for username: {}", vo.getUsername());

        return userRepo.findByUserNameAndStatus(vo.getUsername(), Status.ACTIVE.toString())
            .switchIfEmpty(Mono.defer(() -> {
                log.warn("Login failed: User not found or inactive for username: {}", vo.getUsername());
                return Mono.error(new BadDataException("User not found."));
            }))
            .flatMap(user -> {
                if (passwordEncoder.matches(vo.getPassword(), user.getPassword())) {
                    String token = jwtUtil.generateToken(vo.getUsername());
                    log.info("Password matched for username: {}. Token generated.", vo.getUsername());
                    return secretRepo.save(toEntity(token))
                        .map(this::toVO)
                        .doOnSuccess(voRes -> log.info("Login successful for username: {}", vo.getUsername()));
                } else {
                    log.warn("Login failed: Incorrect password for username: {}", vo.getUsername());
                    return Mono.error(new BadDataException("Password does not match."));
                }
            })
            .doOnError(e -> log.error("Login error for username: {}", vo.getUsername(), e));
    }

    private Secret toEntity(String token) {
        Secret secret = new Secret();
        secret.setToken(token);
        return secret;
    }

    private SecretVO toVO(Secret e) {
        SecretVO vo = new SecretVO();
        vo.setId(e.getId());
        vo.setToken(e.getToken());
        vo.setMessage("Login Successfully");
        return vo;
    }

	@Override
	public Mono<UserVO> forgetPasswordInit(UserVO vo) {
	    log.info("Initiating forget password process for user ID: {}", vo.getId());

	    return userRepo.findByUserNameOrEmailIdAndStatus(vo.getUserName(), vo.getEmailId(), Status.ACTIVE.toString())
	        .switchIfEmpty(Mono.defer(() -> {
	            log.warn("User with ID: {} not found or not active.", vo.getId());
	            return Mono.error(new BadDataException("User not found."));
	        }))
	        .flatMap(existing -> {
	            log.info("User found with ID: {}. Generating OTP...", existing.getId());

	            String otp = generateOtp();
	            LocalTime expiryTime = generateOtpExpiryTime();
	            
	            log.debug("OTP generated: {} with expiry: {}", otp, expiryTime);

	            return userRepo.save(toEntity(existing, mapper, otp, expiryTime))
	                .doOnSuccess(savedUser -> log.info("User updated with OTP. ID: {}", savedUser.getId()))
	                .flatMap(savedUser -> {
	                    log.info("Sending OTP email to: {}", savedUser.getEmailId());
	                    return emailService.sendOtpEmail(savedUser.getEmailId(), otp)
	                        .doOnSuccess(aVoid -> log.info("OTP email sent successfully to: {}", savedUser.getEmailId()))
	                        .thenReturn(toVO(savedUser, mapper, "OTP sent to your email successfully."));
	                });
	        })
	        .doOnError(e -> log.error("Error during forget password process for user ID: {}: {}", vo.getId(), e.getMessage(), e));
	}


	
	private String generateOtp() {
		int otp = (int) (Math.random() * 9000) + 1000;
		return String.valueOf(otp);
	}

	private LocalTime generateOtpExpiryTime() {
		return LocalTime.now().plusMinutes(1);
	}
	
	private User toEntity(User existing, IUserMapper mapper, String otp, LocalTime time) {
		existing.setOtp(otp);
        existing.setExpiryTime(time);
        existing.setStatus(Status.PENDING.toString());
        existing.setStage(Status.INITIATED.toString());
		return existing;
	}
	
	private UserVO toVO(User savedUser, IUserMapper mapper, String msg) {
		UserVO vo = mapper.toVo(savedUser);
		vo.setPassword(null);
		vo.setMessage(msg);
		return vo;
	}

	@Override
	public Mono<UserVO> forgetPasswordConfrm(UserVO vo) {
	    log.info("Initiating OTP confirmation for email: {}", vo.getEmailId());

	    return userRepo
	    		.findByUserNameOrEmailIdAndStageAndStatus(vo.getUserName(), vo.getEmailId(), Status.INITIATED.toString(), Status.PENDING.toString())
	        .switchIfEmpty(Mono.defer(() -> {
	            log.warn("No user found with email: {} in INITIATED and PENDING state", vo.getEmailId());
	            return Mono.error(new BadDataException("User not found."));
	        }))
	        .flatMap(existing -> {
	            log.info("User found for OTP confirmation. ID: {}", existing.getId());

	            if (existing.getOtp() == null || existing.getExpiryTime() == null) {
	                log.warn("Missing OTP or expiry time for user ID: {}", existing.getId());
	                return Mono.error(new BadDataException("OTP not generated."));
	            }

	            if (existing.getExpiryTime().isBefore(LocalTime.now())) {
	                log.warn("Expired OTP (by time only) for user ID: {}", existing.getId());
	                return Mono.error(new BadDataException("OTP has expired."));
	            }


	            if (!existing.getOtp().equals(vo.getOtp())) {
	                log.warn("Incorrect OTP entered for user ID: {}. Provided: {}, Expected: {}", 
	                         existing.getId(), vo.getOtp(), existing.getOtp());
	                return Mono.error(new BadDataException("Invalid OTP."));
	            }

	            log.info("OTP verified successfully for user ID: {}", existing.getId());

	            User updated = toAlt(existing, mapper);
	            log.info("Updating user status to ACTIVE and clearing OTP for user ID: {}", updated.getId());

	            return userRepo.save(updated)
	                .doOnSuccess(u -> log.info("User updated successfully. ID: {}", u.getId()))
	                .map(u -> {
	                    UserVO v = mapper.toVo(u);
	                    v.setPassword(null);
	                    v.setOtp(null);
	                    v.setMessage("Account SignUp successfully.");
	                    return v;
	                });
	        })
	        .doOnError(e -> log.error("Error confirming OTP for email: {}: {}", vo.getEmailId(), e.getMessage(), e));
	}

	
	private User toAlt(User existing, IUserMapper mapper) {
		existing.setStatus(Status.ACTIVE.toString());
		existing.setStage(Status.CONFIRM.toString());
		return existing;
	}

	@Override
	public Mono<UserVO> resetPassword(UserVO vo) {
	    return userRepo.findByUserNameOrEmailIdAndStatus(vo.getUserName(), vo.getEmailId(), Status.ACTIVE.toString())
	        .switchIfEmpty(Mono.defer(() -> {
	            log.warn("User not found or inactive for username: {}", vo.getUserName());
	            return Mono.error(new BadDataException("User not found or inactive."));
	        }))
	        .flatMap(existingUser -> {
	            log.debug("User found: {}", existingUser.getUserName());
	            String encodedPassword = passwordEncoder.encode(vo.getPassword());
	            existingUser.setPassword(encodedPassword);
	            log.info("Password encoded and ready to be saved for user: {}", existingUser.getUserName());
	            return userRepo.save(existingUser);
	        })
	        .map(u -> {
                UserVO v = mapper.toVo(u);
                v.setPassword(null);
                v.setOtp(null);
                v.setMessage("Password Reset Successfully.");
                return v;
            })
	        .doOnSuccess(result -> log.info("Password reset successfully for username: {}", vo.getUserName()))
	        .doOnError(e -> log.error("Error while resetting password for username: {}", vo.getUserName(), e));
	}



	private UserVO toVO(User user) {
		UserVO vo = new UserVO();
		vo.setId(user.getId());
		vo.setUserName(user.getUserName());
		vo.setEmailId(user.getEmailId());
		vo.setMobileNo(user.getMobileNo());
		vo.setMessage("Password Reset Successfully");
		return vo;
	}
}
