package com.example.secret;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

import com.example.security.jwt.JwtUtil;

import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
	
	@Autowired
	private JwtUtil jwtUtil;


	@Bean
	public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
	    return http
	        .csrf().disable()
	        .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
	        .authorizeExchange()
	        .pathMatchers("/login", "/signup/init", "/signup/confirm", "/signup/reSendOtp").permitAll()
	        .anyExchange().authenticated()
	        .and()
	        .addFilterAt(jwtAuthenticationFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
	        .build();
	}

	private AuthenticationWebFilter jwtAuthenticationFilter() {
	    AuthenticationWebFilter filter = new AuthenticationWebFilter(jwtAuthenticationManager());

	    filter.setServerAuthenticationConverter(jwtConverter());
	    filter.setSecurityContextRepository(NoOpServerSecurityContextRepository.getInstance());

	    return filter;
	}

	private ReactiveAuthenticationManager jwtAuthenticationManager() {
	    return authentication -> {
	        try {
	            String token = authentication.getCredentials().toString(); // token from jwtConverter

	            // Validate token (throws if invalid)
	            String username = jwtUtil.extractUsername(token);

	            // You can also extract SessionUser if needed
	            SessionUser sessionUser = jwtUtil.extractSessionUser(token);

	            // Build authenticated token with authorities (empty if no roles)
	            UsernamePasswordAuthenticationToken authenticated =
	                    new UsernamePasswordAuthenticationToken(
	                            sessionUser, // principal
	                            token,       // credentials (you may set null)
	                            Collections.emptyList() // or set roles if you extract them
	                    );

	            return Mono.just(authenticated);
	        } catch (Exception e) {
	            // If token is invalid or expired
	            return Mono.error(new BadCredentialsException("Invalid token"));
	        }
	    };
	}

	private ServerAuthenticationConverter jwtConverter() {
	    return exchange -> {
	        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
	        if (authHeader != null && authHeader.startsWith("Bearer ")) {
	            String token = authHeader.substring(7);
	            // Return authentication token object
	            return Mono.just(new UsernamePasswordAuthenticationToken(token, token));
	        }
	        return Mono.empty();
	    };
	}


}
