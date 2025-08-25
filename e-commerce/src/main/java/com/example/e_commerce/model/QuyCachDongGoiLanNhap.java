package com.example.e_commerce.model;

import com.example.e_commerce.model.embeded.EApDungKhuyenMai;
import com.example.e_commerce.model.embeded.EQuyCachDongGoiLanNhap;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "bienthequycachdonggoi_lannhap")
public class QuyCachDongGoiLanNhap {
	
	 @EmbeddedId
	 private EQuyCachDongGoiLanNhap id = new EQuyCachDongGoiLanNhap(); 

	
	@ManyToOne
	@MapsId("BT_ID")
	@JoinColumn(name = "BT_ID")
	private BienThe bienThe;
	
	@ManyToOne
	@MapsId("DGC_ID")
	@JoinColumn(name = "DGC_ID")
	private DongGoiChan dongGoiChan;
	
	@ManyToOne
	@MapsId("PN_ID")
	@JoinColumn(name = "PN_ID")
	private PhieuNhap phieuNhap;
	
	@Column(name = "SOLUONG")
	private int soLuong;
	
	@Column(name = "DONGIA")
	private float donGia;

	public BienThe getBienThe() {
		return bienThe;
	}

//	@ManyToOne
//	@JsonIgnore
//	@JoinColumns({
//	    @JoinColumn(name = "BT_ID", referencedColumnName = "BT_ID", insertable = false, updatable = false),
//	    @JoinColumn(name = "DGC_ID", referencedColumnName = "DGC_ID", insertable = false, updatable = false),
//	    @JoinColumn(name = "SOLUONG", referencedColumnName = "SOLUONG", insertable = false, updatable = false)
//	})
//	private BienTheDongGoiChan bienTheDongGoiChan;

//	public BienTheDongGoiChan getBienTheDongGoiChan() {
//		return bienTheDongGoiChan;
//	}
//
//	public void setBienTheDongGoiChan(BienTheDongGoiChan bienTheDongGoiChan) {
//		this.bienTheDongGoiChan = bienTheDongGoiChan;
//	}

	public void setBienThe(BienThe bienThe) {
		this.id.setBT_ID(bienThe.getId());
		this.bienThe = bienThe;
	}

	public DongGoiChan getDongGoiChan() {
		return dongGoiChan;
	}

	public void setDongGoiChan(DongGoiChan dongGoiChan) {
		this.id.setDGC_ID(dongGoiChan.getId());
		this.dongGoiChan = dongGoiChan;
	}

	public PhieuNhap getPhieuNhap() {
		return phieuNhap;
	}

	public void setPhieuNhap(PhieuNhap phieuNhap) {
		this.id.setPN_ID(phieuNhap.getId());
		this.phieuNhap = phieuNhap;
	}

	public int getSoLuong() {
		return soLuong;
	}

	public void setSoLuong(int soLuong) {
		this.soLuong = soLuong;
	}

	public float getDonGia() {
		return donGia;
	}

	public void setDonGia(float donGia) {
		this.donGia = donGia;
	}

	public EQuyCachDongGoiLanNhap getId() {
		return id;
	}

	public void setId(EQuyCachDongGoiLanNhap id) {
		this.id = id;
	}
	
	
	
	
}
