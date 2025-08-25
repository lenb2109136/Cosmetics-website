package com.example.e_commerce.model;

import com.example.e_commerce.model.embeded.EBienTheDealChinh;
import com.example.e_commerce.model.embeded.EBienTheFlashSale;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "bienthedealchinh")
public class BienTheDealChinh {
	
	@EmbeddedId
    private EBienTheDealChinh id;

    @MapsId("btId")
    @ManyToOne
    @JoinColumn(name = "BT_ID")
    private BienThe bienThe;

    @MapsId("kmId")
    @ManyToOne
    @JoinColumn(name = "KM_ID")
    private Deal deal;
    
    @Column(name = "BTDC_SOLUONGTU")
	private int  soLuongTu;
    @Column(name = "BTDC_CONSUDUNG")
   	private boolean  conSuDung;
	public EBienTheDealChinh getId() {
		return id;
	}
	public void setId(EBienTheDealChinh id) {
		this.id = id;
	}
	public BienThe getBienThe() {
		return bienThe;
	}
	public void setBienThe(BienThe bienThe) {
		id.setBtId(bienThe.getId());;
		this.bienThe = bienThe;
	}
	
	
	public Deal getDeal() {
		return deal;
	}
	public void setDeal(Deal deal) {
		id.setKmId(deal.getId());
		this.deal = deal;
	}
	public int getSoLuongTu() {
		return soLuongTu;
	}
	public void setSoLuongTu(int soLuongTu) {
		this.soLuongTu = soLuongTu;
	}
	public boolean isConSuDung() {
		return conSuDung;
	}
	public void setConSuDung(boolean conSuDung) {
		this.conSuDung = conSuDung;
	}
    
    
}