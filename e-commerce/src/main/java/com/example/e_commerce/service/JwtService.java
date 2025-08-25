package com.example.e_commerce.service;

import java.time.Duration;
import java.time.Instant;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class JwtService {
	public int getIdUser() {
		JwtAuthenticationToken authenticationToken = (JwtAuthenticationToken) SecurityContextHolder.getContext()
				.getAuthentication();
		if(authenticationToken!=null) {
			int id = Integer.parseInt(authenticationToken.getName());
			return id;
		}
		else {
			return 0;
		}
	}
	
	public String getIdToken() {
		JwtAuthenticationToken authenticationToken = (JwtAuthenticationToken) SecurityContextHolder.getContext()
				.getAuthentication();
		String idtoken=((Jwt)authenticationToken.getPrincipal()).getClaimAsString("jti");
		return idtoken;
	}
	
	public int getTimeSecondExpToken() {
		JwtAuthenticationToken authenticationToken = (JwtAuthenticationToken) SecurityContextHolder.getContext()
				.getAuthentication();
		 Jwt jwt = (Jwt) authenticationToken.getPrincipal();
		    Instant expiresAt = jwt.getExpiresAt();
		    Instant now = Instant.now();
		    long remainingSeconds = Duration.between(now, expiresAt).getSeconds();
		    return (int) Math.max(remainingSeconds, 0);
	}
}
