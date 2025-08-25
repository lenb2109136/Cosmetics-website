package com.example.e_commerce.config.security.jwt;

import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;

import jakarta.servlet.http.HttpServletRequest;

public class JwtFromCookie implements BearerTokenResolver{

	@Override
	public String resolve(HttpServletRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

}
