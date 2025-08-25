package com.example.e_commerce.DTO.response;

import java.lang.ref.PhantomReference;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.example.e_commerce.model.BienThe;
import com.example.e_commerce.model.ChiTietPhieuNhap;


public class SanPhamPhieuNhap {
	long idSanPham;
	String tenSanPham;
	String anhSanPham;
	public int soLuong;
	public float DonGia;
	String maVach;
	private String duongDanIcon="";
	private int id;
	String quyCachDongGoi;
	private int soLuongTronQuyCach=0;
	private String ten;
	private boolean macDinh;
	
	
	
	
	public String getDuongDanIcon() {
		return duongDanIcon;
	}
	public void setDuongDanIcon(String duongDanIcon) {
		this.duongDanIcon = duongDanIcon;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getQuyCachDongGoi() {
		return quyCachDongGoi;
	}
	public void setQuyCachDongGoi(String quyCachDongGoi) {
		this.quyCachDongGoi = quyCachDongGoi;
	}
	public int getSoLuongTronQuyCach() {
		return soLuongTronQuyCach;
	}
	public void setSoLuongTronQuyCach(int soLuongTronQuyCach) {
		this.soLuongTronQuyCach = soLuongTronQuyCach;
	}
	public String getTen() {
		return ten;
	}
	public void setTen(String ten) {
		this.ten = ten;
	}
	public boolean isMacDinh() {
		return macDinh;
	}
	public void setMacDinh(boolean macDinh) {
		this.macDinh = macDinh;
	}
	public String getMaVach() {
		return maVach;
	}
	public void setMaVach(String maVach) {
		this.maVach = maVach;
	}
	public int getSoLuong() {
		return soLuong;
	}
	public void setSoLuong(int soLuong) {
		this.soLuong = soLuong;
	}
	public float getDonGia() {
		return DonGia;
	}
	public void setDonGia(float donGia) {
		DonGia = donGia;
	}
	List<BienTheNhap> danhSachBienTheNhap= new ArrayList<BienTheNhap>();
	List<ChiTietPhieuNhap> bienTheNhap= new ArrayList<ChiTietPhieuNhap>();
	public String getAnhSanPham() {
		return anhSanPham;
	}
	public void setAnhSanPham(String anhSanPham) {
		this.anhSanPham = anhSanPham;
	}
	public long getIdSanPham() {
		return idSanPham;
	}
	public void setIdSanPham(long idSanPham) {
		this.idSanPham = idSanPham;
	}
	public String getTenSanPham() {
		return tenSanPham;
	}
	public void setTenSanPham(String tenSanPham) {
		this.tenSanPham = tenSanPham;
	}

	public List<BienTheNhap> getDanhSachBienTheNhap() {
		return danhSachBienTheNhap;
	}
	public void setDanhSachBienTheNhap(List<BienTheNhap> danhSachBienTheNhap) {
		this.danhSachBienTheNhap = danhSachBienTheNhap;
	}
	public List<ChiTietPhieuNhap> getBienTheNhap() {
		return bienTheNhap;
	}
	public void setBienTheNhap(List<ChiTietPhieuNhap> bienTheNhap) {
		this.bienTheNhap = bienTheNhap;
	}
	public void addBienTheNhap(ChiTietPhieuNhap bienTheNhap) {
		this.bienTheNhap.add(bienTheNhap);
	}
	
	public void addBienThe(long btnhap,String ten, String hinhAnh) {
		for(int i=0;i<danhSachBienTheNhap.size();i++) {
			if(danhSachBienTheNhap.get(i).getId()==btnhap) {
				return;
			}
		}
		BienTheNhap btn= new BienTheNhap();
		btn.setTen(ten);
		btn.setId(btnhap);
		btn.setHinhanh(hinhAnh);
		this.danhSachBienTheNhap.add(btn);
	}
	public void addLanNhap(long idBienThe,long soLuong,float gia, LocalDateTime nh, long id,String donViCungCap) {
		for(int i=0;i<danhSachBienTheNhap.size();i++) {
			if(idBienThe==danhSachBienTheNhap.get(i).getId()) {
				LanNhapKho lanNhapKho= new LanNhapKho();
				lanNhapKho.setGia(gia);
				lanNhapKho.setSoLuong(soLuong);
				lanNhapKho.setNgayNhap(nh);
				lanNhapKho.setId(id);
				lanNhapKho.setDonViCungCap(donViCungCap);
				danhSachBienTheNhap.get(i).getDanhSachNhap().add(lanNhapKho);
			}
		}
	}
}


	
