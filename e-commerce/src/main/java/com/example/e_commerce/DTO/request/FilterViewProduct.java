package com.example.e_commerce.DTO.request;

import java.util.ArrayList;
import java.util.List;

public class FilterViewProduct {
	private int category=0;
	private String tenSanPham="";
	private List<Long> danhSachThongSo= new ArrayList<Long>();
	private float giaBatDau=0;
	private float giaKetThuc=300000000;
	private List<Long> thuongHieuId= new ArrayList<Long>();
	private String orderBy="";
	public int getCategory() {
		return category;
	}
	public void setCategory(int category) {
		this.category = category;
	}
	public String getTenSanPham() {
		return tenSanPham;
	}
	public void setTenSanPham(String tenSanPham) {
		this.tenSanPham = tenSanPham;
	}
	public List<Long> getDanhSachThongSo() {
		return danhSachThongSo;
	}
	public void setDanhSachThongSo(List<Long> danhSachThongSo) {
		this.danhSachThongSo = danhSachThongSo;
	}
	public float getGiaBatDau() {
		return giaBatDau;
	}
	public void setGiaBatDau(float giaBatDau) {
		this.giaBatDau = giaBatDau;
	}
	public float getGiaKetThuc() {
		return giaKetThuc;
	}
	public void setGiaKetThuc(float giaKetThuc) {
		this.giaKetThuc = giaKetThuc;
	}
	public List<Long> getThuongHieuId() {
		return thuongHieuId;
	}
	public void setThuongHieuId(List<Long> thuongHieuId) {
		this.thuongHieuId = thuongHieuId;
	}
	public String getOrderBy() {
		return orderBy;
	}
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}
	
	
	
}