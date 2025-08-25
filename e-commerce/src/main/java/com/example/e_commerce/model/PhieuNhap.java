package com.example.e_commerce.model;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "phieunhap")
public class PhieuNhap {
	 @Id
	 @GeneratedValue(strategy = GenerationType.IDENTITY)
	 @Column(name = "PN_ID")
	 private long id;
	 @Column(name = "PN_NGAYNHAPHANG")
	 private LocalDateTime ngayNhapHang;
	 @Column(name = "PN_TONGTIEN")
	 private float tongTien;
	 @Column(name = "PN_THUEVAT")
	 private float thueVAT;
	 @ManyToOne
	 @JoinColumn(name = "DVCC_ID")
	 private DonViCungCap donViCungCap;
	 
	 @ManyToOne
	 @JoinColumn(name = "NV_ID")
	 private NguoiDung nguoiDung;
	 
	 @Column(name = "PN_DUYET")
	 private boolean duyet;
	 
	 @JsonIgnore
	 @OneToMany(mappedBy = "phieuNhap")
	 private List<ChiTietPhieuNhap> chiTietPhieuNhap;
	 
	 @JsonIgnore
	 @OneToMany(mappedBy = "phieuNhap")
	 private List<QuyCachDongGoiLanNhap> quyCachDongGoiLanNhap;

	 

	public List<QuyCachDongGoiLanNhap> getQuyCachDongGoiLanNhap() {
		return quyCachDongGoiLanNhap;
	}

	public void setQuyCachDongGoiLanNhap(List<QuyCachDongGoiLanNhap> quyCachDongGoiLanNhap) {
		this.quyCachDongGoiLanNhap = quyCachDongGoiLanNhap;
	}

	public boolean isDuyet() {
		return duyet;
	}

	public void setDuyet(boolean duyet) {
		this.duyet = duyet;
	}

	public List<ChiTietPhieuNhap> getChiTietPhieuNhap() {
		return chiTietPhieuNhap;
	}

	public void setChiTietPhieuNhap(List<ChiTietPhieuNhap> chiTietPhieuNhap) {
		this.chiTietPhieuNhap = chiTietPhieuNhap;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public LocalDateTime getNgayNhapHang() {
		return ngayNhapHang;
	}

	public void setNgayNhapHang(LocalDateTime ngayNhapHang) {
		this.ngayNhapHang = ngayNhapHang;
	}

	public float getTongTien() {
		return tongTien;
	}

	public void setTongTien(float tongTien) {
		this.tongTien = tongTien;
	}

	public float getThueVAT() {
		return thueVAT;
	}

	public void setThueVAT(float thueVAT) {
		this.thueVAT = thueVAT;
	}

	public DonViCungCap getDonViCungCap() {
		return donViCungCap;
	}

	public void setDonViCungCap(DonViCungCap donViCungCap) {
		this.donViCungCap = donViCungCap;
	}

	public NguoiDung getNguoiDung() {
		return nguoiDung;
	}

	public void setNguoiDung(NguoiDung nguoiDung) {
		this.nguoiDung = nguoiDung;
	}
	 
	 
	 
}