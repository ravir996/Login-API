package com.example.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.constant.Status;
import com.example.domain.vo.LoginVO;
import com.example.domain.vo.UserVO;
import com.example.entity.User;
import com.example.exception.BadDataException;
import com.example.repo.ISecretRepo;
import com.example.repo.IUserRepo;
import com.example.security.util.SessionUtil;
import com.example.service.IChangePasswordService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ChangePasswordServiceImpl implements IChangePasswordService {
	
	public final IUserRepo userRepo;
	
	private final PasswordEncoder passwordEncoder;

	@Override
	public Mono<UserVO> chgePwd(LoginVO vo) {
	    return SessionUtil.getUser()
	        .flatMap(user -> 
	            userRepo.findByUserNameAndStatus(vo.getUsername(), Status.ACTIVE.toString())
	                .switchIfEmpty(Mono.error(new BadDataException("User not found or inactive.")))
	                .flatMap(existingUser -> {
	                    String encodedPassword = passwordEncoder.encode(vo.getPassword());
	                    existingUser.setPassword(encodedPassword);
	                    return userRepo.save(existingUser);
	                })
	                .map(this::toVO)
	        );
	}

	
	private UserVO toVO(User user) {
	    UserVO vo = new UserVO();
	    vo.setId(user.getId());
	    vo.setName(user.getUserName());
	    vo.setEmailId(user.getEmailId());
	    vo.setMobileNo(user.getMobileNo());
	    vo.setMessage("Password Change Successfully");
	    return vo;
	}


	

}
