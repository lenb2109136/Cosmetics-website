package com.example.e_commerce.model;

import java.time.LocalDateTime;

import com.example.e_commerce.model.embeded.EPhanHoi;
import com.example.e_commerce.model.embeded.ESanPhamKiem;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "phanhoi")
public class PhanHoi {
	 @EmbeddedId
	 private EPhanHoi id;
	@Column(name = "PH_NOIDUNGPHANHOI")
	private String noiDungPhanHoi;
	@Column(name = "PH_SOSAO")
	private int soSao;
	@Column(name = "PH_NGAYGIOPHANHOI")
	private LocalDateTime thoiGianPhanHoi;
	@MapsId("spId")
    @ManyToOne
    @JoinColumn(name = "SP_ID")
	private SanPham sanPham;
	
	@MapsId("khId")
    @ManyToOne
    @JoinColumn(name = "KH_ID")
	private KhachHang khachHang;

	public String getNoiDungPhanHoi() {
		return noiDungPhanHoi;
	}

	public void setNoiDungPhanHoi(String noiDungPhanHoi) {
		this.noiDungPhanHoi = noiDungPhanHoi;
	}

	public int getSoSao() {
		return soSao;
	}

	public void setSoSao(int soSao) {
		this.soSao = soSao;
	}

	public LocalDateTime getThoiGianPhanHoi() {
		return thoiGianPhanHoi;
	}

	public void setThoiGianPhanHoi(LocalDateTime thoiGianPhanHoi) {
		this.thoiGianPhanHoi = thoiGianPhanHoi;
	}

	public SanPham getSanPham() {
		return sanPham;
	}

	public void setSanPham(SanPham sanPham) {
		this.getId().setSpId(sanPham.getId());
		this.sanPham = sanPham;
	}

	public KhachHang getKhachHang() {
		this.getId().setPkhId(khachHang.getId());
		return khachHang;
	}

	public void setKhachHang(KhachHang khachHang) {
		this.khachHang = khachHang;
	}

	public EPhanHoi getId() {
		return id;
	}

	public void setId(EPhanHoi id) {
		this.id = id;
	}
	
	
	
	
}
 