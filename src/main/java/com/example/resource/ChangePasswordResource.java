package com.example.resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.domain.vo.LoginVO;
import com.example.domain.vo.UserVO;
import com.example.service.IChangePasswordService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("chgepwd")
@RequiredArgsConstructor
public class ChangePasswordResource {
	
	public final IChangePasswordService passwordService;
	
	@PostMapping
	public Mono<ResponseEntity<UserVO>> create(@RequestBody final LoginVO vo) {
		return passwordService.chgePwd(vo).map(v -> new ResponseEntity<>(v, HttpStatus.OK));
	}

}
