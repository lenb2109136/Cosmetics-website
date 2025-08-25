package com.example.e_commerce.model.embeded;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class EChiTietHoaDon implements Serializable{
	@Column(name = "BT_ID")
    private int btId;

    @Column(name = "HD_ID")
    private long hdId;
    
    public EChiTietHoaDon() {}

    public EChiTietHoaDon(int btId, Long hdId) {
        this.btId = btId;
        this.hdId = hdId;
    }

    

    public int getBtId() {
		return btId;
	}

	public void setBtId(int btId) {
		this.btId = btId;
	}

	

	public long getHdId() {
		return hdId;
	}

	public void setHdId(long hdId) {
		this.hdId = hdId;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EChiTietHoaDon)) return false;
        EChiTietHoaDon that = (EChiTietHoaDon) o;
        return Objects.equals(hdId, that.hdId) &&
               Objects.equals(btId, that.btId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hdId, btId);
    }
}
