package com.example.e_commerce.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "hoadononline")
public class HoaDonOnline extends HoaDon{
	@Column(name = "HDO_GHNCODE")
	private String ghnCode;

	@Column(name = "HDO_PHIBAOHIEM")
	private int phiBaoHiemGHN=0;
	@Column(name = "HDO_PHIHOANTRA")
	private int phiHoanTraGHN=0;
	@Column(name = "HDO_TONGPHI")
	private float tongPhiGHN=0;
	@Column(name = "HDO_DUKIENGIAO")
	private LocalDateTime thoiGianDuKienGiao;
	
	
	@Column(name = "HDO_DALENGHN")
	private boolean daLenDonGHN=false;
	
	@Column(name = "HDO_TONGKHOILUONG")
	// này là tổng khối lượng của các sản phẩm + tổng khối lượng của hộp đựng luôn rồi
	private int tongKhoiLuong=0;
	
	
	
	@ManyToOne
	@JoinColumn(name = "DH_ID")
	public DongHop dongHop;



	public String getGhnCode() {
		return ghnCode;
	}



	public void setGhnCode(String ghnCode) {
		this.ghnCode = ghnCode;
	}



	public int getPhiBaoHiemGHN() {
		return phiBaoHiemGHN;
	}



	public void setPhiBaoHiemGHN(int phiBaoHiemGHN) {
		this.phiBaoHiemGHN = phiBaoHiemGHN;
	}



	public int getPhiHoanTraGHN() {
		return phiHoanTraGHN;
	}



	public void setPhiHoanTraGHN(int phiHoanTraGHN) {
		this.phiHoanTraGHN = phiHoanTraGHN;
	}



	public float getTongPhiGHN() {
		return tongPhiGHN;
	}



	public void setTongPhiGHN(float tongPhiGHN) {
		this.tongPhiGHN = tongPhiGHN;
	}



	public LocalDateTime getThoiGianDuKienGiao() {
		return thoiGianDuKienGiao;
	}



	public void setThoiGianDuKienGiao(LocalDateTime thoiGianDuKienGiao) {
		this.thoiGianDuKienGiao = thoiGianDuKienGiao;
	}



	public boolean isDaLenDonGHN() {
		return daLenDonGHN;
	}



	public void setDaLenDonGHN(boolean daLenDonGHN) {
		this.daLenDonGHN = daLenDonGHN;
	}



	public int getTongKhoiLuong() {
		return tongKhoiLuong;
	}



	public void setTongKhoiLuong(int tongKhoiLuong) {
		this.tongKhoiLuong = tongKhoiLuong;
	}



	public DongHop getDongHop() {
		return dongHop;
	}



	public void setDongHop(DongHop dongHop) {
		this.dongHop = dongHop;
	}



	
	
//	
	
	
}
