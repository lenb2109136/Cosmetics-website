package com.example.e_commerce.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name="TRUYCAP")
public class TruyCap {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "TC_ID")
	private long id;
	
	@JoinColumn(name = "KH_ID")
	@ManyToOne
	private KhachHang khachHang;
	
	@JoinColumn(name = "SP_ID")
	@ManyToOne
	private SanPham sanPham;
	
	@Column(name = "NGAYGIOTRUYCAP")
	private LocalDateTime ngayGioiTruyCap;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public KhachHang getKhachHang() {
		return khachHang;
	}

	public void setKhachHang(KhachHang khachHang) {
		this.khachHang = khachHang;
	}

	public SanPham getSanPham() {
		return sanPham;
	}

	public void setSanPham(SanPham sanPham) {
		this.sanPham = sanPham;
	}

	public LocalDateTime getNgayGioiTruyCap() {
		return ngayGioiTruyCap;
	}

	public void setNgayGioiTruyCap(LocalDateTime ngayGioiTruyCap) {
		this.ngayGioiTruyCap = ngayGioiTruyCap;
	}
	
	
}
