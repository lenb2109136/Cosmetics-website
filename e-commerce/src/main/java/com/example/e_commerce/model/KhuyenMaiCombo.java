package com.example.e_commerce.model;

import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "khuyenmaicombo")
public class KhuyenMaiCombo extends KhuyenMai {
	@Column(name = "KMCB_GIA")
	private float gia;
	@Column(name = "KMCB_SOLUONG")
	private int soLuong;
	@Column(name = "KMCB_TOIDATRENNGUOI")
	private int toiDaTrenNguoi;
	
	@ManyToMany
	@JoinTable(
		name = "khuyenmaicombo_bienthe",
		joinColumns = @JoinColumn(name ="KM_ID"),
	inverseJoinColumns = @JoinColumn(name="BT_ID")
			)
	private Set<BienThe> bienThe;
	
	public float getGia() {
		return gia;
	}
	public void setGia(float gia) {
		this.gia = gia;
	}
	public int getSoLuong() {
		return soLuong;
	}
	public void setSoLuong(int soLuong) {
		this.soLuong = soLuong;
	}
	public int getToiDaTrenNguoi() {
		return toiDaTrenNguoi;
	}
	public void setToiDaTrenNguoi(int toiDaTrenNguoi) {
		this.toiDaTrenNguoi = toiDaTrenNguoi;
	}
	
	
}
