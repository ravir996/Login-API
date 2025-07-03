package com.example.service;

import com.example.domain.vo.LoginVO;
import com.example.domain.vo.UserVO;

import reactor.core.publisher.Mono;

public interface IChangePasswordService {
	
	public Mono<UserVO> chgePwd(LoginVO vo);

}
