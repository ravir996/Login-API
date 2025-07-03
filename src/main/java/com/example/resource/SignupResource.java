package com.example.resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.domain.vo.UserVO;
import com.example.exception.BadDataException;
import com.example.service.ISignUpService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("signup")
@RequiredArgsConstructor
public class SignupResource {
	
	public final ISignUpService signUpService;
	
	@PostMapping
	public Mono<ResponseEntity<UserVO>> create(@RequestBody final UserVO vo) {
		return signUpService.create(vo).map(v -> new ResponseEntity<>(v, HttpStatus.OK));
	}
	
	 @GetMapping("/error")
	    public Mono<String> testError() {
	        return Mono.error(new BadDataException("Test error from handler"));
	    }

}
