package com.example.e_commerce.config.security.jwt;

import java.beans.BeanProperty;
import java.util.Base64.Decoder;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

@Configuration
public class JWTDecoder {
	
	@Autowired
	private JWTBlacklistVadilate oAuth2TokenValidatorBlackList;
	@Bean(name = "decoderForBlacklist")
	public JwtDecoder decoderforbaseblacklist() {
		SecretKeySpec secretKeySpec= new SecretKeySpec(JWTAuthenticationGenerate.getSerectkey().getBytes(),"HS512");
		NimbusJwtDecoder nimbusJwtDecoder= NimbusJwtDecoder
				.withSecretKey(secretKeySpec)
				.macAlgorithm(MacAlgorithm.HS512)
				.build();
		OAuth2TokenValidator<Jwt> oAuth2TokenValidatorDefault= JwtValidators.createDefault();
		OAuth2TokenValidator<Jwt> combine= new DelegatingOAuth2TokenValidator<Jwt>(oAuth2TokenValidatorBlackList,oAuth2TokenValidatorDefault);
		nimbusJwtDecoder.setJwtValidator(combine);
		return nimbusJwtDecoder;
	}
}
