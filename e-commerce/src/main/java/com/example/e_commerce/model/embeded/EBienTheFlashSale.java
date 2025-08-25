package com.example.e_commerce.model.embeded;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class EBienTheFlashSale implements Serializable {
	@Column(name = "KM_ID")
    private long kmId;

    @Column(name = "BT_ID")
    private int btId;
    
    public EBienTheFlashSale() {}

    public EBienTheFlashSale(long kmId, int btId) {
        this.kmId = kmId;
        this.btId = btId;
    }

    

	public long getKmId() {
		return kmId;
	}

	public void setKmId(long kmId) {
		this.kmId = kmId;
	}

	public int getBtId() {
		return btId;
	}

	public void setBtId(int btId) {
		this.btId = btId;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EBienTheFlashSale)) return false;
        EBienTheFlashSale that = (EBienTheFlashSale) o;
        return Objects.equals(kmId, that.kmId) &&
               Objects.equals(btId, that.btId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kmId, btId);
    }
}
