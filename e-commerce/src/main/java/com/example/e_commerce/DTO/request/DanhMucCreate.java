package com.example.e_commerce.DTO.request;

import java.util.ArrayList;
import java.util.List;

import com.example.e_commerce.model.DanhMuc;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class DanhMucCreate {
	private TrungGian trunggian;
	@NotNull(message = "Vui lòng không chỉnh sửa thông tin mặc định")
	Integer id;
	@NotBlank(message = "Tên danh mục không được để trống ")
	String ten;
	List<DanhMuc> pickCategory;
	List<Long> thongSoChon;
	boolean coCon=false;
	private TrungGian donDanhMuc;
	List<Integer> danhMucCon= new ArrayList<Integer>();
	
	boolean coSanPhamCon=false;
	
	
	
	
	
	public boolean isCoSanPhamCon() {
		return coSanPhamCon;
	}
	public void setCoSanPhamCon(boolean coSanPhamCon) {
		this.coSanPhamCon = coSanPhamCon;
	}
	public List<Integer> getDanhMucCon() {
		return danhMucCon;
	}
	public void setDanhMucCon(List<Integer> danhMucCon) {
		this.danhMucCon = danhMucCon;
	}
	public TrungGian getDonDanhMuc() {
		return donDanhMuc;
	}
	public void setDonDanhMuc(TrungGian donDanhMuc) {
		this.donDanhMuc = donDanhMuc;
	}
	public boolean isCoCon() {
		return coCon;
	}
	public void setCoCon(boolean coCon) {
		this.coCon = coCon;
	}
	public TrungGian getTrunggian() {
		return trunggian;
	}
	public void setTrunggian(TrungGian trunggian) {
		this.trunggian = trunggian;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getTen() {
		return ten;
	}
	public void setTen(String ten) {
		this.ten = ten;
	}
	public List<DanhMuc> getPickCategory() {
		return pickCategory;
	}
	public void setPickCategory(List<DanhMuc> pickCategory) {
		this.pickCategory = pickCategory;
	}
	public List<Long> getThongSoChon() {
		return thongSoChon;
	}
	public void setThongSoChon(List<Long> thongSoChon) {
		this.thongSoChon = thongSoChon;
	}
	
	
}
