package com.example.e_commerce.DTO.request;

public class DanhMucManager {
	int id;
	String ten;
	int tongDanhMucCon;
	int tongThongSo;
	long tongSanPham;
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
	public int getTongDanhMucCon() {
		return tongDanhMucCon;
	}
	public void setTongDanhMucCon(int tongDanhMucCon) {
		this.tongDanhMucCon = tongDanhMucCon;
	}
	public int getTongThongSo() {
		return tongThongSo;
	}
	public void setTongThongSo(int tongThongSo) {
		this.tongThongSo = tongThongSo;
	}
	public long getTongSanPham() {
		return tongSanPham;
	}
	public void setTongSanPham(long tongSanPham) {
		this.tongSanPham = tongSanPham;
	}
	
}
