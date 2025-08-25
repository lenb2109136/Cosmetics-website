package com.example.e_commerce.model;

import com.example.e_commerce.model.embeded.EBienTheDealChinh;
import com.example.e_commerce.model.embeded.EBienTheDealPhu;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "bienthedealphu")
public class BienTheDealPhu {
	@EmbeddedId
    private EBienTheDealPhu id;

    @MapsId("btId")
    @ManyToOne
    @JoinColumn(name = "BT_ID")
    private BienThe bienThe;

    @MapsId("kmId")
    @ManyToOne
    @JoinColumn(name = "KM_ID")
    private Deal deal;
    
    @Column(name = "BTDP_GIATRIGIAM")
	private float  giaTriGiam;
    
    @Column(name = "BTDP_LAPHANTRAM")
    private boolean laPhanTram;
    
    @Column(name = "BTDP_CONSUDUNG")
    private boolean conSuDung;
    
    @Column(name = "BTDP_TOIDATRENDON")
    private int toiDaTrenDonVi;
    
    

	public int getToiDaTrenDonVi() {
		return toiDaTrenDonVi;
	}

	public void setToiDaTrenDonVi(int toiDaTrenDon) {
		this.toiDaTrenDonVi = toiDaTrenDon;
	}

	public EBienTheDealPhu getId() {
		return id;
	}

	public void setId(EBienTheDealPhu id) {
		this.id = id;
	}

	public BienThe getBienThe() {
		return bienThe;
	}

	public void setBienThe(BienThe bienThe) {
		id.setBtId(bienThe.getId());
		this.bienThe = bienThe;
	}

	

	
	public Deal getDeal() {
		return deal;
	}

	public void setDeal(Deal deal) {
		id.setKmId(deal.getId());
		this.deal = deal;
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

	public boolean isConSuDung() {
		return conSuDung;
	}

	public void setConSuDung(boolean conSuDung) {
		this.conSuDung = conSuDung;
	}
    
    
}

