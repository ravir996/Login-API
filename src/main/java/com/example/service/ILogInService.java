package com.example.service;

import com.example.domain.vo.LoginVO;
import com.example.domain.vo.SecretVO;

import reactor.core.publisher.Mono;

public interface ILogInService {
	
	public Mono<SecretVO> login(LoginVO vo);

}
