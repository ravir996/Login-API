package com.example.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.constant.Status;
import com.example.domain.vo.LoginVO;
import com.example.domain.vo.UserVO;
import com.example.entity.User;
import com.example.exception.BadDataException;
import com.example.repo.IUserRepo;
import com.example.security.util.SessionUtil;
import com.example.service.IChangePasswordService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChangePasswordServiceImpl implements IChangePasswordService {

    public final IUserRepo userRepo;

    private final PasswordEncoder passwordEncoder;

    @Override
    public Mono<UserVO> chgePwd(LoginVO vo) {
        log.info("Change password request received for username: {}", vo.getUsername());

        return SessionUtil.getUser()
            .doOnNext(sessionUser -> log.debug("Session user: {}", sessionUser))
            .flatMap(user ->
                userRepo.findByUserNameAndStatus(vo.getUsername(), Status.ACTIVE.toString())
                    .switchIfEmpty(Mono.defer(() -> {
                        log.warn("User not found or inactive for username: {}", vo.getUsername());
                        return Mono.error(new BadDataException("User not found or inactive."));
                    }))
                    .flatMap(existingUser -> {
                        log.debug("User found: {}", existingUser.getUserName());
                        String encodedPassword = passwordEncoder.encode(vo.getPassword());
                        existingUser.setPassword(encodedPassword);
                        log.info("Password encoded and ready to be saved for user: {}", existingUser.getUserName());
                        return userRepo.save(existingUser);
                    })
                    .map(this::toVO)
                    .doOnSuccess(result -> log.info("Password changed successfully for username: {}", vo.getUsername()))
            )
            .doOnError(e -> log.error("Error while changing password for username: {}", vo.getUsername(), e));
    }

    private UserVO toVO(User user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUserName(user.getUserName());
        vo.setEmailId(user.getEmailId());
        vo.setMobileNo(user.getMobileNo());
        vo.setMessage("Password Change Successfully");
        return vo;
    }
}
