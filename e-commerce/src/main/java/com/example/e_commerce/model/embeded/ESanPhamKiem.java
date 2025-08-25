package com.example.e_commerce.model.embeded;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class ESanPhamKiem implements Serializable {

    @Column(name = "BT_ID")
    private int btId;

    @Column(name = "PKH_ID")
    private long pkhId;

    public ESanPhamKiem() {}

    public ESanPhamKiem(int spId, Long pkhId) {
        this.btId = spId;
        this.pkhId = pkhId;
    }

    

    public int getBtId() {
		return btId;
	}

	public void setBtId(int btId) {
		this.btId = btId;
	}

	public void setPkhId(long pkhId) {
		this.pkhId = pkhId;
	}

	public Long getPkhId() {
        return pkhId;
    }

    public void setPkhId(Long pkhId) {
        this.pkhId = pkhId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ESanPhamKiem)) return false;
        ESanPhamKiem that = (ESanPhamKiem) o;
        return Objects.equals(btId, that.btId) &&
               Objects.equals(pkhId, that.pkhId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(btId, pkhId);
    }
}
