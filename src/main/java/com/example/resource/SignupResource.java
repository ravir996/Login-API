package com.example.resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.domain.vo.UserVO;
import com.example.service.ISignUpService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("signup")
@RequiredArgsConstructor
public class SignupResource {
	
	public final ISignUpService signUpService;
	
	@PostMapping("/init")
	public Mono<ResponseEntity<UserVO>> init(@RequestBody final UserVO vo) {
		return signUpService.init(vo).map(v -> new ResponseEntity<>(v, HttpStatus.OK));
	}
	
	@PostMapping("/confirm")
	public Mono<ResponseEntity<UserVO>> confirm(@RequestBody final UserVO vo) {
		return signUpService.confirm(vo).map(v -> new ResponseEntity<>(v, HttpStatus.OK));
	}
	
	@PutMapping("/reSendOtp")
	public Mono<ResponseEntity<UserVO>> reSend(@RequestBody final UserVO vo) {
		return signUpService.reSend(vo).map(v -> new ResponseEntity<>(v, HttpStatus.OK));
	}
}
