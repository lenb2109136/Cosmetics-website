package com.example.e_commerce.DTO.response;

import java.util.List;
import java.util.stream.Collectors;

import com.example.e_commerce.model.DanhMuc;

public class DanhMucDTO {
	private int id;
	private String ten;
	
	

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




	public DanhMucDTO(int id, String ten) {
		super();
		this.id = id;
		this.ten = ten;
	}




	public DanhMucDTO() {
		super();
	}

	
}
