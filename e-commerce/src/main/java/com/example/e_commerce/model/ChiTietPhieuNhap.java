package com.example.e_commerce.model;

import java.time.LocalDate;

import com.example.e_commerce.model.embeded.EChiTietPhieuNhap;
import com.example.e_commerce.model.embeded.ESanPhamKiem;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "chitietphieunhap")
public class ChiTietPhieuNhap {
	 @EmbeddedId
	 @JsonIgnore
	    private EChiTietPhieuNhap id= new EChiTietPhieuNhap();

	    @MapsId("btId")
	    @ManyToOne()
	    @JoinColumn(name = "BT_ID")
	    private BienThe bienThe;

	    @JsonIgnore
	    @MapsId("pnId")
	    @ManyToOne()
	    @JoinColumn(name = "PN_ID")
	    private PhieuNhap phieuNhap;

	    @Column(name = "CTPN_SOLUONG")
	    private Integer soLuong;
	    
	    @Column(name = "CTPN_DONGIA")
	    private float donGia;
	    @Column(name = "CTPN_HANSUDUNG")
	    private LocalDate hanSuDung;
		public EChiTietPhieuNhap getId() {
			return id;
		}
		public void setId(EChiTietPhieuNhap id) {
			this.id = id;
		}
		public BienThe getBienThe() {
			return bienThe;
		}
		public void setBienThe(BienThe bienThe) {
			this.id.setBtId(bienThe.getId());
			this.bienThe = bienThe;
		}
		public PhieuNhap getPhieuNhap() {
			return phieuNhap;
		}
		public void setPhieuNhap(PhieuNhap phieuNhap) {
			this.id.setPnId(phieuNhap.getId());
			this.phieuNhap = phieuNhap;
		}
		public Integer getSoLuong() {
			return soLuong;
		}
		public void setSoLuong(Integer soLuong) {
			this.soLuong = soLuong;
		}
		public float getDonGia() {
			return donGia;
		}
		public void setDonGia(float donGia) {
			this.donGia = donGia;
		}
		public LocalDate getHanSuDung() {
			return hanSuDung;
		}
		public void setHanSuDung(LocalDate hanSuDung) {
			this.hanSuDung = hanSuDung;
		}
	    
	    
}
