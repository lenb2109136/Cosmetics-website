package com.example.e_commerce.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="QUANGCAO")
public class QuangCao {
	@Column(name = "QC_ID")
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@Column(name = "QC_DUONGDANANH")
	private String duongDanAnh;
	@Column(name = "QC_NGAYCHAY")
	private LocalDateTime ngayChay;
	@Column(name = "QC_NGAYNGUNG")
	private LocalDateTime ngayNgung;
	@ManyToOne
	@JoinColumn(name = "ND_ID")
	private Admin admin;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getDuongDanAnh() {
		return duongDanAnh;
	}
	public void setDuongDanAnh(String duongDanAnh) {
		this.duongDanAnh = duongDanAnh;
	}
	public LocalDateTime getNgayChay() {
		return ngayChay;
	}
	public void setNgayChay(LocalDateTime ngayChay) {
		this.ngayChay = ngayChay;
	}
	public LocalDateTime getNgayNgung() {
		return ngayNgung;
	}
	public void setNgayNgung(LocalDateTime ngayNgung) {
		this.ngayNgung = ngayNgung;
	}
	public Admin getAdmin() {
		return admin;
	}
	public void setAdmin(Admin admin) {
		this.admin = admin;
	}
	
	
	  
}
