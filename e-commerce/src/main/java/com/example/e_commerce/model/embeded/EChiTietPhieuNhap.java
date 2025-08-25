package com.example.e_commerce.model.embeded;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class EChiTietPhieuNhap implements Serializable {
	@Column(name = "BT_ID")
    private int btId;

    @Column(name = "PN_ID")
    private long pnId;
    
    public EChiTietPhieuNhap() {}

    public EChiTietPhieuNhap(int btId, Long pn_id) {
        this.btId = btId;
        this.pnId = pn_id;
    }

    

    public int getBtId() {
		return btId;
	}

	public void setBtId(int btId) {
		this.btId = btId;
	}

	public long getPnId() {
		return pnId;
	}

	public void setPnId(long pnId) {
		this.pnId = pnId;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EChiTietPhieuNhap)) return false;
        EChiTietPhieuNhap that = (EChiTietPhieuNhap) o;
        return Objects.equals(pnId, that.pnId) &&
               Objects.equals(btId, that.btId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pnId, btId);
    }
}
