package com.example.e_commerce.model;

import java.time.LocalTime;

import com.example.e_commerce.model.embeded.EBienTheFlashSale;
import com.example.e_commerce.model.embeded.EChiTietHoaDon;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "bientheflashsale")
public class BienTheFlashSale {
	@EmbeddedId
	@JsonIgnore
    private EBienTheFlashSale id;

    @MapsId("btId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BT_ID")
    @JsonIgnore
    private BienThe bienThe;

    @MapsId("kmId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "KM_ID")
    @JsonIgnore
    private FlashSale flashSale;
    
    @Column(name = "BTFS_GIATRIGIAM")
	private float giaTriGiam;
    @Column(name = "BTFS_LAPHANTRAM")
	private boolean laPhanTram;
    @Column(name = "BTFS_GIOIHANSOLUONG")
	private int soLuongGioiHan;
    
    // đúng như tên luôn số lượng còn lại của flashsale;
    @Column(name = "BTFS_SOLUONGDADUNG")
	private int soLuongDaDung;
    
    
    @Column(name = "BTFS_CONSUDUNG")
	private boolean conSuDung;
    
    

	

	public int getSoLuongDaDung() {
		return soLuongDaDung;
	}

	public void setSoLuongDaDung(int soLuongDaDung) {
		this.soLuongDaDung = soLuongDaDung;
	}

	public EBienTheFlashSale getId() {
		return id;
	}

	public void setId(EBienTheFlashSale id) {
		this.id = id;
	}

	public BienThe getBienThe() {
		return bienThe;
	}

	public void setBienThe(BienThe bienThe) {
		this.bienThe = bienThe;
		this.id.setBtId(bienThe.getId());
	}

	public FlashSale getFlashSale() {
		return flashSale;
	}

	public void setFlashSale(FlashSale flashSale) {
		this.flashSale = flashSale;
		this.id.setKmId(flashSale.getId());
	}

	public float getGiaTriGiam() {
		return giaTriGiam;
	}

	public void setGiaTriGiam(float giaTriGiam) {
		this.giaTriGiam = giaTriGiam;
	}

	public boolean isLaPhanTram() {
		return laPhanTram;
	}

	public void setLaPhanTram(boolean laPhanTram) {
		this.laPhanTram = laPhanTram;
	}

	public int getSoLuongGioiHan() {
		return soLuongGioiHan;
	}

	public void setSoLuongGioiHan(int soLuongGioiHan) {
		this.soLuongGioiHan = soLuongGioiHan;
	}

	public boolean isConSuDung() {
		return conSuDung;
	}

	public void setConSuDung(boolean conSuDung) {
		this.conSuDung = conSuDung;
	}
    
    
}