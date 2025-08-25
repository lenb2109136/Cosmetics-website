package com.example.e_commerce.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.e_commerce.model.embeded.ETrangThaiHoaDon;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "trangthaihoadon")
public class TrangThaiHoaDon {

    @EmbeddedId
    private ETrangThaiHoaDon id = new ETrangThaiHoaDon();

    @ManyToOne
    @MapsId("hoaDonId")
    @JoinColumn(name = "HD_ID")
    @JsonIgnore
    private HoaDon hoaDon;

    @ManyToOne
    @MapsId("trangThaiId")
    @JoinColumn(name = "TT_ID")
    private TrangThai trangThai;
    
    
    
    @Column(name = "TTHD_GHICHU")
    private String ghiChu;
    
    
    
    

    public String getGhiChu() {
		return ghiChu;
	}

	public void setGhiChu(String ghiChu) {
		this.ghiChu = ghiChu;
	}

	public void setHoaDon(HoaDon hoaDon) {
        this.hoaDon = hoaDon;
        if (hoaDon != null) {
            this.id.setHoaDonId(hoaDon.getId());
        }
    }

    public void setTrangThai(TrangThai trangThai) {
        this.trangThai = trangThai;
        if (trangThai != null) {
            this.id.setTrangThaiId(trangThai.getId());
        }
    }

    public void setThoiDiem(LocalDateTime thoiDiem) {
        this.id.setThoiDiem(thoiDiem);
    }

    public LocalDateTime getThoiDiem() {
        return id != null ? id.getThoiDiem() : null;
    }

    public HoaDon getHoaDon() {
        return hoaDon;
    }

    public TrangThai getTrangThai() {
        return trangThai;
    }

    public ETrangThaiHoaDon getId() {
        return id;
    }

    public void setId(ETrangThaiHoaDon id) {
        this.id = id;
    }
}

