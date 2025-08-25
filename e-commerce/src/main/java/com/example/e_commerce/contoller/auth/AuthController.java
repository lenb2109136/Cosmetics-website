package com.example.e_commerce.contoller.auth;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.e_commerce.DTO.request.Login;
import com.example.e_commerce.DTO.response.APIResponse;
import com.example.e_commerce.constants.Routes;
import com.example.e_commerce.service.JwtService;
import com.example.e_commerce.service.auth.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping(Routes.AUTH)
public class AuthController {
	
	@Autowired
	private JwtService jwtService;
	
	@Autowired
	private AuthService authService;
	
	@Autowired
	private RedisTemplate<String,Object> redisTemplate;

	@PostMapping("/login")
	public ResponseEntity<APIResponse> login(@Valid @RequestBody Login login, HttpServletResponse response){
		return new ResponseEntity<APIResponse>( authService.loginAuth(login,response),HttpStatus.OK);
	}
	
	@PostMapping("/refresh-token")
	public ResponseEntity<APIResponse> login(HttpServletRequest httpServletRequest){
		return new ResponseEntity<APIResponse>( authService.refreshToken(httpServletRequest),HttpStatus.OK);
	}
	
	
	@PostMapping("/logout")
	public ResponseEntity<APIResponse> logOut(HttpServletRequest httpServletRequest){
		return new ResponseEntity<APIResponse>( authService.logOut(httpServletRequest),HttpStatus.OK);
	}
	
	
	@PostMapping("/checkauth")
	public ResponseEntity<APIResponse> get() {
		return new ResponseEntity<APIResponse>(new APIResponse("succes", true),HttpStatus.OK); 
	}
}
