package com.example.e_commerce.model;

import java.time.LocalDateTime;

import com.example.e_commerce.model.embeded.EThueVATSanPham;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Table(name = "thuevatsanpham")
@Entity
public class ThueVATSanPham {

    @EmbeddedId
    private EThueVATSanPham id;

    @MapsId("idSanPham")
    @ManyToOne
    @JoinColumn(name = "SP_ID")
    private SanPham sanPham;

    @Column(name = "TSP_GIATRI")
    private float tiLe;

    public EThueVATSanPham getId() {
        return id;
    }

    public void setId(EThueVATSanPham id) {
        this.id = id;
    }

    public SanPham getSanPham() {
        return sanPham;
    }

    public void setSanPham(SanPham sanPham) {
        this.sanPham = sanPham;
        if (id == null) {
            id = new EThueVATSanPham();
        }
        this.id.setIdSanPham(sanPham.getId()); 
    }

    public float getTiLe() {
        return tiLe;
    }

    public void setTiLe(float tiLe) {
        this.tiLe = tiLe;
    }

    public LocalDateTime getThoiDiem() {
        return id != null ? id.getThoiDiem() : null;
    }

    public void setThoiDiem(LocalDateTime thoiDiem) {
        if (id == null) {
            id = new EThueVATSanPham();
        }
        id.setThoiDiem(thoiDiem);
    }
}
