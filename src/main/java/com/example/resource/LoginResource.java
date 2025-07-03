package com.example.resource;


import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.domain.vo.LoginVO;
import com.example.domain.vo.SecretVO;
import com.example.service.ILogInService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("login")
@RequiredArgsConstructor
public class LoginResource {
	
	private final ILogInService logInService;
	
	@PostMapping
	public Mono<ResponseEntity<SecretVO>> login(@RequestBody LoginVO vo) {
	     return logInService.login(vo)
	             .map(this::signResponse)
	             .switchIfEmpty(unauthorized());
	}
	
	private ResponseEntity<SecretVO> signResponse(SecretVO secret) {
	    HttpHeaders httpHeaders = new HttpHeaders();
	    httpHeaders.add("Authorization", "Bearer " + secret.getToken()); // assuming `getToken()` returns JWT
	    return new ResponseEntity<>(secret, httpHeaders, HttpStatus.OK);
	}

	private Mono<ResponseEntity<SecretVO>> unauthorized() {
	    return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
	}


}


