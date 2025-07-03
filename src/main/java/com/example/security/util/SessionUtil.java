
package com.example.security.util;

import org.springframework.security.core.context.ReactiveSecurityContextHolder;

import com.example.secret.SessionUser;

import reactor.core.publisher.Mono;

public class SessionUtil {

	public static Mono<SessionUser> getUser() {
	    return ReactiveSecurityContextHolder.getContext()
	        .map(ctx -> {
	            Object principal = ctx.getAuthentication().getPrincipal();
	            if (principal instanceof SessionUser) {
	                return (SessionUser) principal;
	            } else {
	                throw new RuntimeException("Invalid user principal.");
	            }
	        })
	        .switchIfEmpty(Mono.error(new RuntimeException("User not authenticated.")));
	}

}

