package com.example.e_commerce.DTO.request;

import java.time.LocalDate;

public class FilterOrderEmployee {
	 String maHoaDon;
	 String khachHang;
	 long trangThai;
	 LocalDate ngayLap;
	 int sort;
	 int trang;
	public String getMaHoaDon() {
		return maHoaDon;
	}
	public void setMaHoaDon(String maHoaDon) {
		this.maHoaDon = maHoaDon;
	}
	public String getKhachHang() {
		return khachHang;
	}
	public void setKhachHang(String khachHang) {
		this.khachHang = khachHang;
	}
	public long getTrangThai() {
		return trangThai;
	}
	public void setTrangThai(long trangThai) {
		this.trangThai = trangThai;
	}
	public LocalDate getNgayLap() {
		return ngayLap;
	}
	public void setNgayLap(LocalDate ngayLap) {
		this.ngayLap = ngayLap;
	}
	public int getSort() {
		return sort;
	}
	public void setSort(int sort) {
		this.sort = sort;
	}
	public int getTrang() {
		return trang;
	}
	public void setTrang(int trang) {
		this.trang = trang;
	}
	 
	 
}
