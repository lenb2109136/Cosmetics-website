package com.example.e_commerce.model.embeded;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
@Embeddable
public class EHoaDonHaoHut implements Serializable{
	@Column(name = "HD_ID")
    private Long HD_ID;
	@Column(name = "BT_ID")
    private int BT_ID;
    @Column(name = "PKH_ID")
    private Long PKH_ID;

    
    
    public Long getHD_ID() {
		return HD_ID;
	}

	public void setHD_ID(Long hD_ID) {
		HD_ID = hD_ID;
	}

	public int getBT_ID() {
		return BT_ID;
	}

	public void setBT_ID(int bT_ID) {
		BT_ID = bT_ID;
	}

	public Long getPN_ID() {
		return PKH_ID;
	}

	public void setPN_ID(Long pN_ID) {
		PKH_ID = pN_ID;
	}

	public EHoaDonHaoHut() {
		super();
	}

	public EHoaDonHaoHut(Long hD_ID, int bT_ID, Long pN_ID) {
		super();
		HD_ID = hD_ID;
		BT_ID = bT_ID;
		PKH_ID = pN_ID;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EHoaDonHaoHut)) return false;
        EHoaDonHaoHut that = (EHoaDonHaoHut) o;
        return Objects.equals(HD_ID, that.HD_ID) &&
               Objects.equals(BT_ID, that.BT_ID) &&
               Objects.equals(PKH_ID, that.PKH_ID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(HD_ID, BT_ID, PKH_ID);
    }
}
