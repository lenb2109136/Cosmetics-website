package com.example.e_commerce.model;

import java.time.LocalDateTime;

import com.example.e_commerce.model.embeded.EDonGiaBanHang;
import com.example.e_commerce.model.embeded.EThueVATSanPham;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
@Entity
@Table(name = "dongiabanhang")
public class DonGiaBanHang {
	@EmbeddedId
    private EDonGiaBanHang id;

    @MapsId("bienTheId")
    @ManyToOne
    @JoinColumn(name = "BT_ID")
    private BienThe bienThe;

    @Column(name = "DGB_GIA")
    private float gia;

    

    public EDonGiaBanHang getId() {
		return id;
	}

	public void setId(EDonGiaBanHang id) {
		this.id = id;
	}

	public BienThe getBienThe() {
        return bienThe;
    }

    public void setBienThe(BienThe bienThe) {
        this.bienThe = bienThe;
        if (id == null) {
            id = new EDonGiaBanHang();
        }
        this.id.setIdSanPham(bienThe.getId()); 
    }

    public float getGia() {
        return gia;
    }

    public void setGia(float tiLe) {
        this.gia = tiLe;
    }

    public LocalDateTime getThoiDiem() {
        return id != null ? id.getThoiDiem() : null;
    }

    public void setThoiDiem(LocalDateTime thoiDiem) {
        if (id == null) {
            id = new EDonGiaBanHang();
        }
        id.setThoiDiem(thoiDiem);
    }
}

