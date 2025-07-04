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
}
