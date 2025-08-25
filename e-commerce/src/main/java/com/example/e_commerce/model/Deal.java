package com.example.e_commerce.model;

import java.security.PrivateKey;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "deal")
public class Deal extends KhuyenMai{
	@Column(name = "D_SOLUONGGIOIHAN")
	private int soLuongGioiHan;
	@Column(name = "D_SOLUONGDADUNG")
	private int soLuongDaDung;
	
	@OneToMany(mappedBy = "deal")
	private List<BienTheDealChinh> bienTheDealChinh;
	@OneToMany(mappedBy = "deal")
	private List<BienTheDealPhu> bienTheDealPhu;
	
//	@NotBlank(message = "Tên chương trình không được để trống")
	@Column(name = "D_TENCHUONGTRINH")
	private String tenChuongTrinh;
	
	
	
	
	public String getTenChuongTrinh() {
		return tenChuongTrinh;
	}
	public void setTenChuongTrinh(String tenChuongTrinh) {
		this.tenChuongTrinh = tenChuongTrinh;
	}
	public List<BienTheDealChinh> getBienTheDealChinh() {
		return bienTheDealChinh;
	}
	public void setBienTheDealChinh(List<BienTheDealChinh> bienTheDealChinh) {
		this.bienTheDealChinh = bienTheDealChinh;
	}
	public List<BienTheDealPhu> getBienTheDealPhu() {
		return bienTheDealPhu;
	}
	public void setBienTheDealPhu(List<BienTheDealPhu> bienTheDealPhu) {
		this.bienTheDealPhu = bienTheDealPhu;
	}
	public int getSoLuongGioiHan() {
		return soLuongGioiHan;
	}
	public void setSoLuongGioiHan(int soLuongGioiHan) {
		this.soLuongGioiHan = soLuongGioiHan;
	}
	public int getSoLuongDaDung() {
		return soLuongDaDung;
	}
	public void setSoLuongDaDung(int soLuongDaDung) {
		this.soLuongDaDung = soLuongDaDung;
	}
	
	
	
}
