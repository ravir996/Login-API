package com.example.service.impl;

import java.time.LocalTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.constant.Status;
import com.example.domain.mapper.IUserMapper;
import com.example.domain.vo.UserVO;
import com.example.entity.User;
import com.example.exception.BadDataException;
import com.example.exception.MimicDataException;
import com.example.repo.IUserRepo;
import com.example.service.IEmailService;
import com.example.service.ISignUpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class SignUpServiceImpl implements ISignUpService {

	private final IUserRepo userRepo;
	
	private final PasswordEncoder passwordEncoder;
	
	private final IUserMapper mapper;
	
	private final IEmailService emailService;

	@Override
	public Mono<UserVO> init(UserVO vo) {
		log.info("Initializing user registration for email: {}", vo.getEmailId());
		return checkDuplicateUser(vo)
			.then(Mono.defer(() -> {
				if (!vo.getConfrmPassword().equals(vo.getPassword())) {
					log.warn("Password and Confirm Password do not match for email: {}", vo.getEmailId());
					return Mono.error(new BadDataException("Password and Confirm Password do not match."));
				}

				String otp = generateOtp();
				LocalTime expiryTime = generateOtpExpiryTime();
				vo.setOtp(otp);
				vo.setExpiryTime(expiryTime);
				vo.setPassword(passwordEncoder.encode(vo.getPassword()));
				log.debug("Generated OTP: {} with expiry: {}", otp, expiryTime);

				return userRepo.save(toEntity(vo, mapper))
					.flatMap(savedUser -> {
						log.info("User saved with ID: {}. Sending OTP...", savedUser.getId());
						return emailService.sendOtpEmail(savedUser.getEmailId(), otp)
							.thenReturn(toVO(savedUser, mapper, "OTP sent to your email successfully."));
					});
			}));
	}

	private Mono<Void> checkDuplicateUser(UserVO vo) {
		log.debug("Checking for existing user with username/email/mobile: {}, {}, {}", 
			vo.getUserName(), vo.getEmailId(), vo.getMobileNo());
		return userRepo
				.findByUserNameOrEmailIdOrMobileNoAndStatus(vo.getUserName(), vo.getEmailId(), vo.getMobileNo(),
						Status.ACTIVE.toString())
				.flatMap(existingUser -> {
					log.warn("Duplicate user found for: {}", vo.getEmailId());
					return Mono.error(new MimicDataException(
						"User already exists with the given username, email ID, or mobile number."));
				})
				.then();
	}

	private String generateOtp() {
		int otp = (int) (Math.random() * 9000) + 1000;
		return String.valueOf(otp);
	}

	private LocalTime generateOtpExpiryTime() {
		return LocalTime.now().plusMinutes(1);
	}

	private User toEntity(UserVO vo, IUserMapper mapper) {
		User entity = mapper.toEntity(vo);
		entity.setStatus(Status.PENDING.toString());
		entity.setStage(Status.INITIATED.toString());
		return entity;
	}
	
	private UserVO toVO(User savedUser, IUserMapper mapper, String msg) {
		UserVO vo = mapper.toVo(savedUser);
		vo.setPassword(null);
		vo.setMessage(msg);
		return vo;
	}

	@Override
	public Mono<UserVO> confirm(UserVO vo) {
		log.info("Confirming OTP for email: {}", vo.getEmailId());
		return userRepo
				.findByUserNameOrEmailIdAndStageAndStatus(vo.getUserName(), vo.getEmailId(), Status.INITIATED.toString(), Status.PENDING.toString())
				.switchIfEmpty(Mono.error(new BadDataException("User not found.")))
				.flatMap(existing -> {
					if (existing.getOtp() == null || existing.getExpiryTime() == null) {
						log.warn("OTP or expiry time missing for user ID: {}", existing.getId());
						return Mono.error(new BadDataException("OTP not generated."));
					}

					if (existing.getExpiryTime().isBefore(LocalTime.now())) {
					    log.warn("Expired OTP (by time only) for user ID: {}", existing.getId());
					    return Mono.error(new BadDataException("OTP has expired."));
					}


					if (!existing.getOtp().equals(vo.getOtp())) {
						log.warn("Invalid OTP provided for user ID: {}", existing.getId());
						return Mono.error(new BadDataException("Invalid OTP."));
					}

					User updated = toAlt(existing, mapper);
					log.info("User confirmed. Updating status to ACTIVE for ID: {}", updated.getId());
					return userRepo.save(updated).map(u -> {
						UserVO v = mapper.toVo(u);
						v.setPassword(null);
						v.setOtp(null);
						v.setMessage("Account SignUp successfully.");
						return v;
					});
				});
	}

	private User toAlt(User existing, IUserMapper mapper) {
		existing.setStatus(Status.ACTIVE.toString());
		existing.setStage(Status.CONFIRM.toString());
		return existing;
	}

	@Override
	public Mono<UserVO> reSend(UserVO vo) {
		log.info("Resending OTP for user ID: {}", vo.getId());
		return userRepo
	        .findByIdAndStageAndStatus(vo.getId(), Status.INITIATED.toString(), Status.PENDING.toString())
	        .switchIfEmpty(Mono.error(new BadDataException("User not found.")))
	        .flatMap(existing -> {
	            String otp = generateOtp();
	            LocalTime expiryTime = generateOtpExpiryTime();

	            existing.setOtp(otp);
	            existing.setExpiryTime(expiryTime);
	            log.debug("New OTP: {} with expiry: {}", otp, expiryTime);

	            User updatedUser = updateExistingUser(existing);

	            return userRepo.save(updatedUser)
	                .flatMap(savedUser -> {
	                	log.info("OTP resent successfully for user ID: {}", savedUser.getId());
	                	return emailService.sendOtpEmail(savedUser.getEmailId(), otp)
	                        .thenReturn(toVO(savedUser, mapper, "OTP Resend to your email successfully."));
	                });
	        });
	}

	private User updateExistingUser(User existing) {
		existing.setStatus(Status.PENDING.toString());
		existing.setStage(Status.INITIATED.toString());
		return existing;
	}
}
