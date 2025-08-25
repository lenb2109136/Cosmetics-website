package com.example.e_commerce.DTO.request;

import jakarta.validation.constraints.NotBlank;

public class Login {
	@NotBlank(message = "Thông tin đăng nhập không hợp lệ")
	String soDienThoai;
	@NotBlank(message = "Thông tin đăng nhập không hợp lệ")
	String passWord;
	
	public String getSoDienThoai() {
		return soDienThoai;
	}
	public void setSoDienThoai(String soDienThoai) {
		this.soDienThoai = soDienThoai;
	}
	public String getPassWord() {
		return passWord;
	}
	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}
	
	
}
