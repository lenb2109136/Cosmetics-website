package com.example.e_commerce.DTO.response;

import java.util.ArrayList;
import java.util.List;

public class BienTheNhap {
	long id;
	String maVach;
	String ten;
	String hinhanh;
	List<LanNhapKho> danhSachNhap= new ArrayList<LanNhapKho>();
	
	
	public String getMaVach() {
		return maVach;
	}
	public void setMaVach(String maVach) {
		this.maVach = maVach;
	}
	public String getHinhanh() {
		return hinhanh;
	}
	public void setHinhanh(String hinhanh) {
		this.hinhanh = hinhanh;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	public String getTen() {
		return ten;
	}
	public void setTen(String ten) {
		this.ten = ten;
	}
	public List<LanNhapKho> getDanhSachNhap() {
		return danhSachNhap;
	}
	public void setDanhSachNhap(List<LanNhapKho> danhSachNhap) {
		this.danhSachNhap = danhSachNhap;
	}
	
}
