package com.example.service;

import com.example.domain.vo.UserVO;

import reactor.core.publisher.Mono;

public interface ISignUpService {

	public Mono<UserVO> create(UserVO vo);
	
 
}
