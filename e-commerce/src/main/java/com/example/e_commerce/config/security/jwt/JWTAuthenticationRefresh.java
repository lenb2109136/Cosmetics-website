package com.example.e_commerce.config.security.jwt;

import java.security.SecureRandom;
import java.util.Base64;

public class JWTAuthenticationRefresh {
	 private static final SecureRandom secureRandom = new SecureRandom(); 
	    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder().withoutPadding();
	    public static String generateSecureToken() {
	        byte[] randomBytes = new byte[64]; 
	        secureRandom.nextBytes(randomBytes);
	        return base64Encoder.encodeToString(randomBytes);
	    }
}
