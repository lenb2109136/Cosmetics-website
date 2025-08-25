package com.example.e_commerce.model.embeded;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class EBienTheDongGoiChan implements Serializable{
	@Column(name = "DGC_ID")
    private int DGC_ID;

    @Column(name = "BT_ID")
    private int BT_ID;
    
    @Column(name = "SOLUONG")
    private int SOLUONG;
    
    
    

	public int getSOLUONG() {
		return SOLUONG;
	}

	public void setSOLUONG(int sOLUONG) {
		SOLUONG = sOLUONG;
	}

	public EBienTheDongGoiChan() {
		super();
	}

	public EBienTheDongGoiChan(int dGC_ID, int bT_ID,Integer SOLUONG) {
		super();
		DGC_ID = dGC_ID;
		BT_ID = bT_ID;
		this.SOLUONG=SOLUONG;
	}

	
	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EBienTheDongGoiChan)) return false;
        EBienTheDongGoiChan that = (EBienTheDongGoiChan) o;
        return DGC_ID == that.DGC_ID &&
        	       BT_ID == that.BT_ID &&
        	       SOLUONG == that.SOLUONG;

               
    }

    @Override
    public int hashCode() {
        return Objects.hash(BT_ID, DGC_ID,SOLUONG);
    }

	public int getDGC_ID() {
		return DGC_ID;
	}

	public void setDGC_ID(int dGC_ID) {
		DGC_ID = dGC_ID;
	}

	public int getBT_ID() {
		return BT_ID;
	}

	public void setBT_ID(int bT_ID) {
		BT_ID = bT_ID;
	}
    
    
}
