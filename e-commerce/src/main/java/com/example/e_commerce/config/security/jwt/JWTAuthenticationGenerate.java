package com.example.e_commerce.config.security.jwt;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.stereotype.Component;

import com.example.e_commerce.DTO.request.Login;
import com.example.e_commerce.Exception.Custom.InternalServerErrorException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;


@Component
public class JWTAuthenticationGenerate {
	private static String serectkey="6fonEJ2Cji1pFH0Vyun41iOFwHK0kzhRAjOKuzhG1eK3VXF7iC2r6bndfBtEjulJ\r\n";
	public static String getSerectkey() {
		return serectkey;
	}

	public static void setSerectkey(String serectkey) {
		JWTAuthenticationGenerate.serectkey=serectkey;
	}
	
	public String generateAccessToken(Long ID, List<String> role) {
		JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
	    String jti = UUID.randomUUID().toString();

	    JWTClaimsSet claim = new JWTClaimsSet.Builder()
	            .issuer("ECommerce")
	            .issueTime(new Date())
	            .expirationTime(Date.from(Instant.now().plus(2, ChronoUnit.HOURS)))

	            .claim("scope", role)
	            .subject(String.valueOf(ID))
	            .jwtID(jti)
	            .build();

	    Payload payload = new Payload(claim.toJSONObject());
	    JWSObject jws = new JWSObject(header, payload);
		try {
			jws.sign(new MACSigner(serectkey.getBytes()));
			return jws.serialize();
		} catch (Exception e) {
			throw new InternalServerErrorException("Lỗi server: không thể xác thực");
		}
	}
}
