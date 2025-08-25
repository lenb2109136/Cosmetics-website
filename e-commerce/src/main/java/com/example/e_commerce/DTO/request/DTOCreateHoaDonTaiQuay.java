package com.example.e_commerce.DTO.request;

import java.util.List;

public class DTOCreateHoaDonTaiQuay {
	
	
	private long hinhThucThanhToan;
	private String soDienThoai;
	private String tenKhachHang;
	private List<CartItem> danhSachMatHang;
	public long getHinhThucThanhToan() {
		return hinhThucThanhToan;
	}
	public void setHinhThucThanhToan(long hinhThucThanhToan) {
		this.hinhThucThanhToan = hinhThucThanhToan;
	}
	public String getSoDienThoai() {
		return soDienThoai;
	}
	public void setSoDienThoai(String soDienThoai) {
		this.soDienThoai = soDienThoai;
	}
	public String getTenKhachHang() {
		return tenKhachHang;
	}
	public void setTenKhachHang(String tenKhachHang) {
		this.tenKhachHang = tenKhachHang;
	}
	public List<CartItem> getDanhSachMatHang() {
		return danhSachMatHang;
	}
	public void setDanhSachMatHang(List<CartItem> danhSachMatHang) {
		this.danhSachMatHang = danhSachMatHang;
	}
	
	
}
