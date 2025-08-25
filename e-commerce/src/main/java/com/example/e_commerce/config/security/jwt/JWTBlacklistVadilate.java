package com.example.e_commerce.config.security.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import com.example.e_commerce.service.CacheService;
import com.nimbusds.jwt.JWT;

@Component
public class JWTBlacklistVadilate implements OAuth2TokenValidator<Jwt>{
	
		@Autowired
		private CacheService cacheService;
		
		public boolean isContainBlackList(Jwt jwt) {
			String key= jwt.getId();
			System.out.println("KEY THU ĐƯỢC: "+key);
			if(cacheService.getKey(key)!=null) {
				
				return true;
			}
			else {
				return false;
			}
		}

		@Override
		public OAuth2TokenValidatorResult validate(Jwt token) {
			if(isContainBlackList(token)==true) {
				OAuth2Error error= new OAuth2Error("Token không hợp lệ");
				return OAuth2TokenValidatorResult.failure(error);
			}
			else {
				return OAuth2TokenValidatorResult.success();
			}
		}
}
