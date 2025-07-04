package com.example.repo;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.example.entity.User;

import reactor.core.publisher.Mono;

@Repository
public interface IUserRepo extends ReactiveCrudRepository<User, Long> {
	
	
	public Mono<User> findByUserNameAndStatus(String name, String status);
	
	public Mono<User> findByUserNameOrEmailIdOrMobileNoAndStatus(String name, String emailId, String mobileNo, String status);
	
	public Mono<User> findByUserNameOrEmailIdAndStageAndStatus(String name, String emailId, String stage, String status);
	
	public Mono<User> findByIdAndStageAndStatus(Long id, String stage, String status);
	
	public Mono<User> findByUserNameOrEmailIdAndStatus(String name, String emailId, String status);

	


}
