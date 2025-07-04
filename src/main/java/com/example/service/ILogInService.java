package com.example.service;

import com.example.domain.vo.LoginVO;
import com.example.domain.vo.SecretVO;
import com.example.domain.vo.UserVO;

import reactor.core.publisher.Mono;

public interface ILogInService {
	
	public Mono<SecretVO> login(LoginVO vo);

	public Mono<UserVO> forgetPasswordInit(UserVO vo);

	public Mono<UserVO> forgetPasswordConfrm(UserVO vo);

	public Mono<UserVO> resetPassword(UserVO vo);

}
