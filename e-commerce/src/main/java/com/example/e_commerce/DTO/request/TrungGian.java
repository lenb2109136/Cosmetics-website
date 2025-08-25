package com.example.e_commerce.DTO.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TrungGian {
	@NotNull(message = "Vui lòng không chỉnh sửa thông tin mặc định")
	Integer id;
	@NotBlank(message = "Tên danh mục không được để trống")
	String ten;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTen() {
		return ten;
	}
	public void setTen(String ten) {
		this.ten = ten;
	}
	
}
