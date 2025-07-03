package com.example.security.jwt;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.secret.SessionUser;

@Component
public class JwtUtil {

	@Value("${jwt.secret-key}")
	private String secretKey;

	@Value("${jwt.expiration-time}")
	private String expirationTime;

    
	public String generateToken(String username) {
	    long expirationMillis = 1000 * 60 * 60 * 10; // e.g., 10 hours
	    return Jwts.builder()
	        .setSubject(username)
	        .setIssuedAt(new Date())
	        .setExpiration(new Date(System.currentTimeMillis() + expirationMillis))
	        .signWith(SignatureAlgorithm.HS256, secretKey)
	        .compact();
	}


    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    public Claims extractClaims(String token) {
        return Jwts.parser()
                   .setSigningKey(secretKey)
                   .parseClaimsJws(token)
                   .getBody();
    }

    public SessionUser extractSessionUser(String token) {
        Claims claims = extractClaims(token);
        SessionUser user = new SessionUser();
        user.setUserName(claims.getSubject());
        return user;
    }
}
