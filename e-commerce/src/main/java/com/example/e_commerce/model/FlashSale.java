package com.example.e_commerce.model;

import java.time.LocalTime;
import java.util.List;

import org.springframework.cglib.core.Local;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "flashsale")
public class FlashSale extends KhuyenMai{
	@Column(name = "FS_GIIOBATDAU")
	private LocalTime gioBatDau;
	@Column(name = "FS_GIOKETTHUC")
	private LocalTime gioKetThuc;
	public LocalTime getGioBatDau() {
		return gioBatDau;
	}
	
	@OneToMany(mappedBy = "flashSale")
	private List<BienTheFlashSale> bienTheFlashSale;
	
	
	public List<BienTheFlashSale> getBienTheFlashSale() {
		return bienTheFlashSale;
	}
	public void setBienTheFlashSale(List<BienTheFlashSale> bienTheFlashSale) {
		this.bienTheFlashSale = bienTheFlashSale;
	}
	public void setGioBatDau(LocalTime gioBatDau) {
		this.gioBatDau = gioBatDau;
	}
	public LocalTime getGioKetThuc() {
		return gioKetThuc;
	}
	public void setGioKetThuc(LocalTime gioKetThuc) {
		this.gioKetThuc = gioKetThuc;
	}
	
	
}