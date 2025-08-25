package com.example.e_commerce.model;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "khuyenmaitangkem")
public class KhuyenMaiTangKem extends KhuyenMai {
	@Column(name = "KMTK_GIOIHAN")
	private int soLuongGioiHan;
	
	@Column(name = "KMTK_SOLUONGDADUNG")
	private int soLuongDaDung=0;
	
	@OneToMany(mappedBy = "khuyenMaiTangKem")
	private List<SanPhamChinh> sanPhamChinh;
	
	@OneToMany(mappedBy = "khuyenMaiTangKem")
	private List<SanPhamPhu> sanPhamPhu ;
	
	
	
	public List<SanPhamChinh> getSanPhamChinh() {
		return sanPhamChinh;
	}
	public void setSanPhamChinh(List<SanPhamChinh> sanPhamChinh) {
		this.sanPhamChinh = sanPhamChinh;
	}
	public List<SanPhamPhu> getSanPhamPhu() {
		return sanPhamPhu;
	}
	public void setSanPhamPhu(List<SanPhamPhu> sanPhamPhu) {
		this.sanPhamPhu = sanPhamPhu;
	}
	public int getSoLuongDaDung() {
		return soLuongDaDung;
	}
	public void setSoLuongDaDung(int soLuongDaDung) {
		this.soLuongDaDung = soLuongDaDung;
	}
	public int getSoLuongGioiHan() {
		return soLuongGioiHan;
	}
	public void setSoLuongGioiHan(int soLuongGioiHan) {
		this.soLuongGioiHan = soLuongGioiHan;
	}
}
