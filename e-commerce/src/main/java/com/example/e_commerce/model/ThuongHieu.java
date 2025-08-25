package com.example.e_commerce.model;

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
@Table(name = "thuonghieu")
public class ThuongHieu {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "TH_ID")
	private long id;

	@Column(name = "TH_TEN")
	private String ten;
	
	@Column(name = "TH_ANHDAIDIEN")
	private String anhDaiDien;
	@Column(name = "TH_ANHBIA")
	private String anhBia;
	@Column(name = "TH_ANHNEN")
	private String anhNen;
	@ManyToOne
	@JoinColumn(name = "QG_ID")
	private QuocGia quocGia;
	
	@OneToMany(mappedBy = "thuongHieu")
	@JsonIgnore
	private List<SanPham> sanPham;
	
	public List<SanPham> getSanPham() {
		return sanPham;
	}
	public void setSanPham(List<SanPham> sanPham) {
		this.sanPham = sanPham;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getTen() {
		return ten;
	}
	public void setTen(String ten) {
		this.ten = ten;
	}
	public String getAnhDaiDien() {
		return anhDaiDien;
	}
	public void setAnhDaiDien(String anhDaiDien) {
		this.anhDaiDien = anhDaiDien;
	}
	public String getAnhBia() {
		return anhBia;
	}
	public void setAnhBia(String anhBia) {
		this.anhBia = anhBia;
	}
	public String getAnhNen() {
		return anhNen;
	}
	public void setAnhNen(String anhNen) {
		this.anhNen = anhNen;
	}
	public QuocGia getQuocGia() {
		return quocGia;
	}
	public void setQuocGia(QuocGia quocGia) {
		this.quocGia = quocGia;
	}
	
	
}



