package com.example.e_commerce.service.auth;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.e_commerce.DTO.request.Login;
import com.example.e_commerce.DTO.response.APIResponse;
import com.example.e_commerce.DTO.response.Auth;
import com.example.e_commerce.DTO.response.UserDTO;
import com.example.e_commerce.Exception.Custom.InvalidTokenException;
import com.example.e_commerce.Exception.Custom.UserNotFoundException;
import com.example.e_commerce.config.security.jwt.JWTAuthenticationGenerate;
import com.example.e_commerce.config.security.jwt.JWTAuthenticationRefresh;
import com.example.e_commerce.constants.Role;
import com.example.e_commerce.model.Admin;
import com.example.e_commerce.model.KhachHang;
import com.example.e_commerce.model.NguoiDung;
import com.example.e_commerce.model.NhanVien;
import com.example.e_commerce.model.RefreshToken;
import com.example.e_commerce.repository.NguoiDungRepository;
import com.example.e_commerce.repository.RefreshTokenRepository;
import com.example.e_commerce.service.JwtService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class AuthService {
	@Autowired
	private JWTAuthenticationGenerate jwtAuthenticationGenerate;
	
	@Autowired
	private NguoiDungRepository nguoiDungRepository;
	
	@Autowired RefreshTokenRepository refreshTokenRepository;
	
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	
	@Autowired
	private JwtService jwtService;
	
	public APIResponse loginAuth(Login login, HttpServletResponse response) {
		NguoiDung nguoiDung= nguoiDungRepository.findBySodienthoai(login.getSoDienThoai()).orElseThrow(()->{
			throw new UserNotFoundException("Thông tin đăng nhập không hợp lệ");
		});
		// nên thiết lập lại mã hóa mật khẩu nha lên
		if(login.getPassWord().equals(nguoiDung.getMatkhau())==false) {
			throw new InvalidTokenException("Thông tin hợp lệ", HttpStatus.UNAUTHORIZED);
		}
		List<String> quyen= new ArrayList<String>();
		String accesToken=jwtAuthenticationGenerate.generateAccessToken(nguoiDung.getId(), quyen);
		String refresh=JWTAuthenticationRefresh.generateSecureToken();
		RefreshToken r= new RefreshToken();
		r.setBiThuHoi(false);
		r.setNgayHetHan(LocalDateTime.now().plusDays(7));
		r.setNgayTao(LocalDateTime.now());
		r.setNguoiDung(nguoiDung);
		r.setRefresh(refresh);
		refreshTokenRepository.save(r);
		if(nguoiDung instanceof Admin) {
			quyen.add(Role.ADMIN.name());
		}
		else if(nguoiDung instanceof NhanVien) {
			quyen.add(Role.EMPLOYEE.name());
		}
		else {
			
			quyen.add(Role.CUSTOMER.name());
		}
		
		
		
		Cookie refreshTokenCookie = new Cookie("refresh_token", refresh);
	    refreshTokenCookie.setHttpOnly(true);  
	    refreshTokenCookie.setPath("/");      
	    refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60);
	    response.addCookie(refreshTokenCookie);
		
		Auth auth= new Auth();
		auth.setAccessToken(accesToken);
		auth.setTokenType("Bearer");
		auth.setExpiresIn(20*60);
			UserDTO user= new UserDTO();
			user.setFullName(nguoiDung.getTen());
			user.setSoDienThoai(nguoiDung.getSodienthoai());
			user.setId(nguoiDung.getId());
			user.setRole(quyen);
		auth.setUser(user);
		return new APIResponse("OK",auth);
	}

	
	public APIResponse refreshToken(HttpServletRequest httpServletRequest) {
		String RefreshToken= getRefreshTokenFromCookie(httpServletRequest);
		RefreshToken refreshToken= refreshTokenRepository.findByRefresh(RefreshToken).orElseThrow(()-> new InvalidTokenException("Thông tin hợp lệ", HttpStatus.UNAUTHORIZED));
		
		
		if(refreshToken.isBiThuHoi()==true || refreshToken.getNgayHetHan().isBefore(LocalDateTime.now())) {
			throw new InvalidTokenException("Thông tin hợp lệ", HttpStatus.UNAUTHORIZED);
		}
		Login login= new Login();
		login.setSoDienThoai(refreshToken.getNguoiDung().getSodienthoai());
		login.setPassWord(refreshToken.getNguoiDung().getMatkhau());
		List<String> quyen= new ArrayList<String>();

		if(refreshToken.getNguoiDung() instanceof Admin) {
			quyen.add(Role.ADMIN.name());
		}
		else if(refreshToken.getNguoiDung() instanceof NhanVien) {
			quyen.add(Role.EMPLOYEE.name());
		}
		else {
			quyen.add(Role.CUSTOMER.name());
		}
		
		String accesToken=jwtAuthenticationGenerate.generateAccessToken(refreshToken.getNguoiDung().getId(), quyen);
		Auth auth= new Auth();
		auth.setAccessToken(accesToken);
		auth.setTokenType("Bearer");
		auth.setExpiresIn(20*60);
			UserDTO user= new UserDTO();
			user.setFullName(refreshToken.getNguoiDung().getTen());
			user.setSoDienThoai(refreshToken.getNguoiDung().getSodienthoai());
			user.setId(refreshToken.getNguoiDung().getId());
			user.setRole(quyen);
		auth.setUser(user);
		return new APIResponse("OK",auth);
		
	}
	public  String getRefreshTokenFromCookie(HttpServletRequest request) {
	    Cookie[] cookies = request.getCookies();
	    if (cookies != null) {
	        for (Cookie cookie : cookies) {
	            if ("refresh_token".equals(cookie.getName())) {
	                String token = cookie.getValue();
	                if (token == null || token.isEmpty()) {
	                    break;
	                }
	                if (token.startsWith("Bearer ")) {
	                    token = token.substring(7);
	                }
	                return token;
	            }
	        }
	    }
	    throw new InvalidTokenException("Thông tin không hợp lệ",HttpStatus.UNAUTHORIZED);
	}
	
	public APIResponse logOut(HttpServletRequest htpHttpServletRequest) {
		String RefreshToken= getRefreshTokenFromCookie(htpHttpServletRequest);
		RefreshToken refreshToken= refreshTokenRepository.findByRefresh(RefreshToken).orElseThrow(()-> new InvalidTokenException("Thông tin hợp lệ", HttpStatus.UNAUTHORIZED));
		if(refreshToken.isBiThuHoi()==true || refreshToken.getNgayHetHan().isBefore(LocalDateTime.now())) {
			throw new InvalidTokenException("Thông tin hợp lệ", HttpStatus.UNAUTHORIZED);
		}
		refreshTokenRepository.delete(refreshToken);
		System.out.println("số lượng giây còn lại: "+jwtService.getTimeSecondExpToken());
		redisTemplate.opsForValue().set(jwtService.getIdToken(), "1",Duration.ofSeconds(jwtService.getTimeSecondExpToken()));
		return new APIResponse("Succes", null);
	}

}
