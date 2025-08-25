package com.example.e_commerce.config.websocket;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.example.e_commerce.Exception.Custom.InvalidTokenException;
import com.example.e_commerce.constants.Role;
import com.example.e_commerce.model.Admin;
import com.example.e_commerce.model.NhanVien;
import com.example.e_commerce.model.RefreshToken;
import com.example.e_commerce.repository.RefreshTokenRepository;
import com.example.e_commerce.service.auth.AuthService;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class HansakeUpgrade implements HandshakeInterceptor{
	@Autowired
	private AuthService authServicel;
	
	@Autowired
	private RefreshTokenRepository refreshTokenRepository;
	
	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Map<String, Object> attributes) throws Exception {
		ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
		HttpServletRequest httpRequest = servletRequest.getServletRequest();
		String refresh=authServicel.getRefreshTokenFromCookie(httpRequest);
		RefreshToken refreshToken= refreshTokenRepository.findByRefresh(refresh).orElseThrow(()-> new InvalidTokenException("Thông tin hợp lệ", HttpStatus.UNAUTHORIZED));
		attributes.put("id", refreshToken.getNguoiDung().getId());
		if(refreshToken.isBiThuHoi()==true || refreshToken.getNgayHetHan().isBefore(LocalDateTime.now())) {
			return false;
		}
		if(refreshToken.getNguoiDung() instanceof Admin) {
			attributes.put("role", Role.ADMIN.name());
			return true;
		}
		else if(refreshToken.getNguoiDung() instanceof NhanVien) {
			attributes.put("role", Role.EMPLOYEE.name());
			return true;
		}
		else {
			attributes.put("role", Role.CUSTOMER.name());
			return true;
		}
	}

	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Exception exception) {
		
	}

}
