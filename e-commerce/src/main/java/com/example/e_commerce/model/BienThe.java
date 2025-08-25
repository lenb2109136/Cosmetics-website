package com.example.e_commerce.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "bienthe")
public class BienThe {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "BT_ID")
	private int id;
	@Column(name = "BT_CONSUDUNG")
	private boolean conSuDung;
	@Column(name = "BT_BANLE")
	private boolean banLe;
	@Column(name = "BT_TILEBANLE")
	private String tiLeBanLe;
	@Column(name = "BT_ANHBIA")
	private String anhBia;
	@Column(name = "BT_SOLUONG")
	private long soLuongKho=0;
	@Column(name = "BT_GIA")
	private float gia;
	
	@Column(name = "BT_MAVACH")
	private String maVach;
	
	@Column(name = "BT_TEN")
	private String ten;
	
	@JoinColumn(name = "SP_ID")
	@ManyToOne
	@JsonIgnore
	private SanPham sanPham;
	
	
	@JsonIgnore
	@ManyToMany(mappedBy = "bienThe")
	private List<KhuyenMaiCombo> khuyenMaiCombo;
	@JsonIgnore
	@ManyToMany(mappedBy = "bienThe")
	private List<BienTheFlashSale> bienTheFlashSale;
	
	@JsonIgnore
	@OneToMany(mappedBy = "bienThe")
	private List<BienTheDealChinh> bienTheDealChinh;
	
	@JsonIgnore
	@OneToMany(mappedBy = "bienThe")
	private List<BienTheDealPhu> bienTheDealPhu;
	
	
	@JsonIgnore
	@OneToMany(mappedBy = "bienThe")
	private List<BienTheDongGoiChan> bienTheDongGoiChans;
	
	
	@Column(name = "BT_KHOILUONG")
	private int khoiLuong;
	
	
	
	
	public int getKhoiLuong() {
		return khoiLuong;
	}
	public void setKhoiLuong(int khoiLuong) {
		this.khoiLuong = khoiLuong;
	}
	public List<BienTheDongGoiChan> getBienTheDongGoiChans() {
		return bienTheDongGoiChans;
	}
	public void setBienTheDongGoiChans(List<BienTheDongGoiChan> bienTheDongGoiChans) {
		this.bienTheDongGoiChans = bienTheDongGoiChans;
	}
	public String getMaVach() {
		return maVach;
	}
	public void setMaVach(String maVach) {
		this.maVach = maVach;
	}
	public List<BienTheDealChinh> getBienTheDealChinh() {
		return bienTheDealChinh;
	}
	public void setBienTheDealChinh(List<BienTheDealChinh> bienTheDealChinh) {
		this.bienTheDealChinh = bienTheDealChinh;
	}
	public List<BienTheDealPhu> getBienTheDealPhu() {
		return bienTheDealPhu;
	}
	public void setBienTheDealPhu(List<BienTheDealPhu> bienTheDealPhu) {
		this.bienTheDealPhu = bienTheDealPhu;
	}
	public SanPham getSanPham() {
		return sanPham;
	}
	public void setSanPham(SanPham sanPham) {
		this.sanPham = sanPham;
	}
	public List<KhuyenMaiCombo> getKhuyenMaiCombo() {
		return khuyenMaiCombo;
	}
	public String getTen() {
		return ten;
	}
	public void setTen(String ten) {
		this.ten = ten;
	}
	public List<BienTheFlashSale> getBienTheFlashSale() {
		return bienTheFlashSale;
	}
	public void setBienTheFlashSale(List<BienTheFlashSale> bienTheFlashSale) {
		this.bienTheFlashSale = bienTheFlashSale;
	}
	public void setKhuyenMaiCombo(List<KhuyenMaiCombo> khuyenMaiCombo) {
		this.khuyenMaiCombo = khuyenMaiCombo;
	}
	public List<BienTheFlashSale> getbienTheFlashSale() {
		return bienTheFlashSale;
	}
	public void setbienTheFlashSale(List<BienTheFlashSale> bienThèlashSale) {
		this.bienTheFlashSale = bienThèlashSale;
	}
	public float getGia() {
		return gia;
	}
	public void setGia(float gia) {
		this.gia = gia;
	}
	public String getAnhBia() {
		return anhBia;
	}
	public void setAnhBia(String anhBia) {
		this.anhBia = anhBia;
	}
	

	public long getSoLuongKho() {
		return soLuongKho;
	}
	public void setSoLuongKho(long soLuongKho) {
		this.soLuongKho = soLuongKho;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public boolean isConSuDung() {
		return conSuDung;
	}
	public void setConSuDung(boolean conSuDung) {
		this.conSuDung = conSuDung;
	}
	public boolean isBanLe() {
		return banLe;
	}
	public void setBanLe(boolean banLe) {
		this.banLe = banLe;
	}
	public String getTiLeBanLe() {
		return tiLeBanLe;
	}
	public void setTiLeBanLe(String tiLeBanLe) {
		this.tiLeBanLe = tiLeBanLe;
	}
	@Override
	public boolean equals(Object o) {
	    if (this == o) return true;
	    if (o == null || getClass() != o.getClass()) return false;
	    BienThe bienThe = (BienThe) o;
	    return id == bienThe.id;
	}

	@Override
	public int hashCode() {
	    return Integer.hashCode(id);
	}

	
}
