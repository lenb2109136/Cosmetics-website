package com.example.e_commerce.model;

import java.util.ArrayList;
import java.util.List;

import com.example.e_commerce.model.embeded.EChiTietHoaDon;
import com.example.e_commerce.model.embeded.EChiTietPhieuNhap;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "chitiethoadon")
public class ChiTietHoaDon {
	@EmbeddedId
    private EChiTietHoaDon id= new EChiTietHoaDon();

    @MapsId("btId")
    @ManyToOne
    @JoinColumn(name = "BT_ID")
    private BienThe bienThe;

    @MapsId("hdId")
    @ManyToOne
    @JoinColumn(name = "HD_ID")
    private HoaDon hoaDon;

    @Column(name = "CTHD_DONGIA")
    private float donGia;
    
    @Column(name = "CTHD_TONGTIEN")
    private float tongTien;
    
    @Column(name = "CTHD_SOLUONG")
    private int soLuong;
    
    @OneToMany(mappedBy = "chiTietHoaDon")
    private List<ApDungKhuyenMai> danhSachKhuyenMai;
    
    

    public List<ApDungKhuyenMai> getDanhSachKhuyenMai() {
        return danhSachKhuyenMai != null ? danhSachKhuyenMai : new ArrayList<>();
    }

	public void setDanhSachKhuyenMai(List<ApDungKhuyenMai> danhSachKhuyenMai) {
		this.danhSachKhuyenMai = danhSachKhuyenMai;
	}

	public EChiTietHoaDon getId() {
		return id;
	}

	public void setId(EChiTietHoaDon id) {
		this.id = id;
	}

	public BienThe getBienThe() {
		
		return bienThe;
	}

	public void setBienThe(BienThe bienThe) {
		this.id.setBtId(bienThe.getId());
		this.bienThe = bienThe;
	}

	public HoaDon getHoaDon() {
		return hoaDon;
	}

	public void setHoaDon(HoaDon hoaDon) {
		this.id.setHdId(hoaDon.getId());
		this.hoaDon = hoaDon;
	}

	public float getDonGia() {
		return donGia;
	}

	public void setDonGia(float donGia) {
		this.donGia = donGia;
	}

	public float getTongTien() {
		return tongTien;
	}

	public void setTongTien(float tongTien) {
		this.tongTien = tongTien;
	}

	public int getSoLuong() {
		return soLuong;
	}

	public void setSoLuong(int soLuong) {
		this.soLuong = soLuong;
	}
    
    
    
}
