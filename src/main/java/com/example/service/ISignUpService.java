package com.example.service;

import com.example.domain.vo.UserVO;

import reactor.core.publisher.Mono;

public interface ISignUpService {

	public Mono<UserVO> init(UserVO vo);
	
	public Mono<UserVO> confirm(UserVO vo);
	
	public Mono<UserVO> reSend(UserVO vo);
	
 
}
