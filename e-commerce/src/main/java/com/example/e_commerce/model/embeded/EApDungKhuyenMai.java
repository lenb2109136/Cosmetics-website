package com.example.e_commerce.model.embeded;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class EApDungKhuyenMai implements Serializable {

	@Column(name = "HD_ID")
    private Long HD_ID;
	@Column(name = "BT_ID")
    private int BT_ID;
    @Column(name = "KM_ID")
    private Long KM_ID;

    public EApDungKhuyenMai() {}

    public EApDungKhuyenMai(Long hdId, int btId, Long kmId) {
        this.HD_ID = hdId;
        this.BT_ID = btId;
        this.KM_ID = kmId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EApDungKhuyenMai)) return false;
        EApDungKhuyenMai that = (EApDungKhuyenMai) o;
        return Objects.equals(HD_ID, that.HD_ID) &&
               Objects.equals(BT_ID, that.BT_ID) &&
               Objects.equals(KM_ID, that.KM_ID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(HD_ID, BT_ID, KM_ID);
    }

	public Long getHdId() {
		return HD_ID;
	}

	public void setHdId(Long hdId) {
		this.HD_ID = hdId;
	}

	public int getBtId() {
		return BT_ID;
	}

	public void setBtId(int btId) {
		this.BT_ID = btId;
	}

	public Long getKmId() {
		return KM_ID;
	}

	public void setKmId(Long kmId) {
		this.KM_ID = kmId;
	}

    
}