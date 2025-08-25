package com.example.e_commerce.DTO.request;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class BienThe {
	private int id;
	@Min(value = 1, message = "Giá sản phẩm không được nhỏ hơn 0")
	private float gia;
	@NotBlank(message = "Tên Phân loại không được để trống")
	private String ten;
	List<ItemQuyCachDongGoi> dongGoiNhap= new ArrayList<ItemQuyCachDongGoi>();
	private boolean consuDung;
	@NotBlank(message = "Mã vạch phân loại không được để trống")
	String maVach;
	private long soLuong;
	private String duongDanAnh;
	private boolean old;
	@Min(value = 1, message = "Trọng lượng phân loại phải lớn hơn 0")
	private int khoiLuong;

	
	
	
	
	
	
	
	public int getKhoiLuong() {
		return khoiLuong;
	}
	public void setKhoiLuong(int khoiLuong) {
		this.khoiLuong = khoiLuong;
	}
	public String getMaVach() {
		return maVach;
	}
	public void setMaVach(String maVach) {
		this.maVach = maVach;
	}
	public List<ItemQuyCachDongGoi> getDongGoiNhap() {
		return dongGoiNhap;
	}
	public void setDongGoiNhap(List<ItemQuyCachDongGoi> dongGoiNhap) {
		this.dongGoiNhap = dongGoiNhap;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public boolean isOld() {
		return old;
	}
	public void setOld(boolean old) {
		this.old = old;
	}
	public boolean isConsuDung() {
		return consuDung;
	}
	public void setConsuDung(boolean consuDung) {
		this.consuDung = consuDung;
	}
	public long getSoLuong() {
		return soLuong;
	}
	public void setSoLuong(long soLuong) {
		this.soLuong = soLuong;
	}
	public String getDuongDanAnh() {
		return duongDanAnh;
	}
	public void setDuongDanAnh(String duongDanAnh) {
		this.duongDanAnh = duongDanAnh;
	}
	public float getGia() {
		return gia;
	}
	public void setGia(float gia) {
		this.gia = gia;
	}
	public String getTen() {
		return ten;
	}
	public void setTen(String ten) {
		this.ten = ten;
	}
	
	
	
}
