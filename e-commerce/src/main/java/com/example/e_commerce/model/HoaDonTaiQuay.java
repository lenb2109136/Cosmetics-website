package com.example.e_commerce.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "hoadontaiquay")
public class HoaDonTaiQuay extends HoaDon{
	@ManyToOne
	@JoinColumn(name = "HTTT_ID")
	private HinhThucThanhToan hinhThucThanhToan;
	
	@ManyToOne
	@JoinColumn(name = "NV_ID")
	private NhanVien nhanVien;

	public HinhThucThanhToan getHinhThucThanhToan() {
		return hinhThucThanhToan;
	}

	public void setHinhThucThanhToan(HinhThucThanhToan hinhThucThanhToan) {
		this.hinhThucThanhToan = hinhThucThanhToan;
	}

	public NhanVien getNhanVien() {
		return nhanVien;
	}

	public void setNhanVien(NhanVien nhanVien) {
		this.nhanVien = nhanVien;
	}
	
	
}
