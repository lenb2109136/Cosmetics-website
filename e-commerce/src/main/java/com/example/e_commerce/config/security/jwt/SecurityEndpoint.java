package com.example.e_commerce.config.security.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.util.RouteMatcher.Route;

import com.example.e_commerce.constants.Role;
import com.example.e_commerce.constants.Routes;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityEndpoint {
	
	@Autowired
	@Qualifier("decoderForBlacklist")
	private JwtDecoder decoderForBlacklist;
	@Bean
	@Order(1)
	public SecurityFilterChain securityAuthEndpoint(HttpSecurity httpSecurity) throws Exception {
	    httpSecurity
	        .securityMatcher(Routes.AUTH + "/**")
	        .authorizeHttpRequests(auth -> auth
	            .requestMatchers(Routes.AUTH + "/login").permitAll()
	            .requestMatchers(Routes.AUTH + "/refresh-token").permitAll()
	            .requestMatchers(Routes.AUTH + "/checkauth").authenticated()
	            .anyRequest().authenticated()
	        )
	        .csrf(csrf -> csrf.disable())
	        .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.decoder(decoderForBlacklist)));

	    return httpSecurity.build();
	}
	@Bean
	@Order(2)
	public SecurityFilterChain securityApiEndpoint(HttpSecurity httpSecurity) throws Exception {
	    httpSecurity
	        .securityMatcher(Routes.API + "/**")
	        .authorizeHttpRequests(auth -> auth
	        	.requestMatchers(Routes.API + "/sanpham").hasAuthority(Role.ADMIN+ " "+ Role.CUSTOMER + " "+ Role.EMPLOYEE)
	   
	        	.requestMatchers(Routes.API + "/comment").hasAuthority(Role.CUSTOMER+"")
	        	.requestMatchers(Routes.API + "/thongso").hasAuthority(Role.ADMIN+" "+Role.CUSTOMER)
	        	.requestMatchers(Routes.API + "/thuonghieu").hasAuthority(Role.ADMIN+" "+Role.CUSTOMER)
	        	.requestMatchers(Routes.API + "/hoadononline").hasAuthority(Role.ADMIN+"")
	        	.requestMatchers(Routes.API + "/phieunhap").hasAuthority(Role.ADMIN+""+Role.EMPLOYEE)
	        	.requestMatchers(Routes.API + "/thuonghieu").hasAuthority(Role.ADMIN+" "+Role.CUSTOMER)
	        	.requestMatchers(Routes.API + "/pushnotifycation").hasAuthority(Role.EMPLOYEE+"")
	            .anyRequest().permitAll()
	        )
	        .csrf(csrf -> csrf.disable())
	        .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.decoder(decoderForBlacklist)));

	    return httpSecurity.build();
	}
	@Bean
	@Order(3)
	public SecurityFilterChain securityWebSocket(HttpSecurity httpSecurity) throws Exception {
	    httpSecurity
	        .securityMatcher("/ws/**")
	        .authorizeHttpRequests(auth -> auth
	            .anyRequest().permitAll()
	        )
	        .csrf(csrf -> csrf.disable())
	        .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.decoder(decoderForBlacklist)));

	    return httpSecurity.build();
	}
		
}
