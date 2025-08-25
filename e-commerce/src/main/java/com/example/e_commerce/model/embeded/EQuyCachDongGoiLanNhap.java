package com.example.e_commerce.model.embeded;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class EQuyCachDongGoiLanNhap implements Serializable{
	
	
	@Column(name = "PN_ID")
    private Long PN_ID;
	@Column(name = "DGC_ID")
    private int DGC_ID;
    @Column(name = "BT_ID")
    private int BT_ID;
    @Column(name = "BTDGC_SOLUONG")
    private int BTDGC_SOLUONG;
    
   

    public EQuyCachDongGoiLanNhap() {
		super();
	}

	public EQuyCachDongGoiLanNhap(Long pN_ID, int dGC_ID, int bT_ID, int BTDGC_SOLUONG) {
		super();
		PN_ID = pN_ID;
		DGC_ID = dGC_ID;
		BT_ID = bT_ID;
		BTDGC_SOLUONG=BTDGC_SOLUONG;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EQuyCachDongGoiLanNhap)) return false;
        EQuyCachDongGoiLanNhap that = (EQuyCachDongGoiLanNhap) o;
        return Objects.equals(PN_ID, that.PN_ID) &&
               Objects.equals(BT_ID, that.BT_ID) &&
               Objects.equals(DGC_ID, that.DGC_ID) &&
        Objects.equals(BTDGC_SOLUONG, that.BTDGC_SOLUONG);
        
    }

    @Override
    public int hashCode() {
        return Objects.hash(DGC_ID, BT_ID, PN_ID,BTDGC_SOLUONG);
    }

	public int getBTDGC_SOLUONG() {
		return BTDGC_SOLUONG;
	}

	public void setBTDGC_SOLUONG(int bTDGC_SOLUONG) {
		BTDGC_SOLUONG = bTDGC_SOLUONG;
	}

	public Long getPN_ID() {
		return PN_ID;
	}

	public void setPN_ID(Long pN_ID) {
		PN_ID = pN_ID;
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
