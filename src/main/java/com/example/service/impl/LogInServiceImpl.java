package com.example.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.constant.Status;
import com.example.domain.vo.LoginVO;
import com.example.domain.vo.SecretVO;
import com.example.entity.Secret;
import com.example.exception.BadDataException;
import com.example.repo.ISecretRepo;
import com.example.repo.IUserRepo;
import com.example.security.jwt.JwtUtil;
import com.example.service.ILogInService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class LogInServiceImpl implements ILogInService {

	public final IUserRepo userRepo;

	public final JwtUtil jwtUtil;

	public final PasswordEncoder passwordEncoder;

	public final ISecretRepo secretRepo;

	@Override
	public Mono<SecretVO> login(LoginVO vo) {
		return userRepo.findByUserNameAndStatus(vo.getUsername(), Status.ACTIVE.toString())
				.switchIfEmpty(Mono.error(new BadDataException("User not found or inactive."))).flatMap(user -> {
					if (passwordEncoder.matches(vo.getPassword(), user.getPassword())) {
						String token = jwtUtil.generateToken(vo.getUsername());
						return secretRepo.save(toEntity(token)).map(this::toVO);
					} else {
						return Mono.error(new BadDataException("Password does not match."));
					}
				});
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

}
