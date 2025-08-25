package com.example.e_commerce.DTO.response;

import java.time.LocalDateTime;

public class LanNhapKho {
	long soLuong;
	float gia;
	long id;
	LocalDateTime ngayNhap;
	String donViCungCap;
	
	
	public String getDonViCungCap() {
		return donViCungCap;
	}
	public void setDonViCungCap(String donViCungCap) {
		this.donViCungCap = donViCungCap;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public LocalDateTime getNgayNhap() {
		return ngayNhap;
	}
	public void setNgayNhap(LocalDateTime ngayNhap) {
		this.ngayNhap = ngayNhap;
	}
	public long getSoLuong() {
		return soLuong;
	}
	public void setSoLuong(long soLuong) {
		this.soLuong = soLuong;
	}
	public float getGia() {
		return gia;
	}
	public void setGia(float gia) {
		this.gia = gia;
	}
}