package com.example.e_commerce.DTO.response;

public class ThongSoMangerDTO {
	long id;
	String ten;
	int tongSoDanhMuc;
	int tongSoThongSoCuThe;
	long TongSoSanPhamApDung;
	public ThongSoMangerDTO(long id,String ten, int tongSoDanhMuc, int tongSoThongSoCuThe, long tongSoSanPhamApDung) {
		super();
		this.id=id;
		this.ten = ten;
		this.tongSoDanhMuc = tongSoDanhMuc;
		this.tongSoThongSoCuThe = tongSoThongSoCuThe;
		TongSoSanPhamApDung = tongSoSanPhamApDung;
	}
	public ThongSoMangerDTO() {
		super();
	}
	public String getTen() {
		return ten;
	}
	public void setTen(String ten) {
		this.ten = ten;
	}
	public int getTongSoDanhMuc() {
		return tongSoDanhMuc;
	}
	public void setTongSoDanhMuc(int tongSoDanhMuc) {
		this.tongSoDanhMuc = tongSoDanhMuc;
	}
	public int getTongSoThongSoCuThe() {
		return tongSoThongSoCuThe;
	}
	public void setTongSoThongSoCuThe(int tongSoThongSoCuThe) {
		this.tongSoThongSoCuThe = tongSoThongSoCuThe;
	}
	public long getTongSoSanPhamApDung() {
		return TongSoSanPhamApDung;
	}
	public void setTongSoSanPhamApDung(long tongSoSanPhamApDung) {
		TongSoSanPhamApDung = tongSoSanPhamApDung;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	
}
