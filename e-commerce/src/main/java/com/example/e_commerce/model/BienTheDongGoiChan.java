package com.example.e_commerce.model;

import com.example.e_commerce.model.embeded.EBienTheDongGoiChan;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "bienthequycachdonggoi")
public class BienTheDongGoiChan {
	@EmbeddedId
	private EBienTheDongGoiChan id = new EBienTheDongGoiChan(); 

	@ManyToOne
	@MapsId("BT_ID")
	@JoinColumn(name = "BT_ID")
	private BienThe bienThe;
	
	@ManyToOne
	@JoinColumn(name = "DGC_ID")
	@MapsId("DGC_ID")
	private DongGoiChan dongGoiChan;
	
	
//	@MapsId("SOLUONG")
//	@Column(name="SOLUONG")
//	private int soLuong;
	
	@Column(name="MAVACH")
	private String maVach;
	
	

	public EBienTheDongGoiChan getId() {
		return id;
	}

	public void setId(EBienTheDongGoiChan id) {
		this.id = id;
	}

	public BienThe getBienThe() {
		return bienThe;
	}

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

//	public int getSoLuong() {
//		return soLuong;
//	}
//
//	public void setSoLuong(int soLuong) {
//		this.soLuong=soLuong;
//		this.soLuong = soLuong;
//	}

	public String getMaVach() {
		return maVach;
	}

	public void setMaVach(String maVach) {
		this.maVach = maVach;
	}
	
	
}
