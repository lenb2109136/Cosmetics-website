package com.example.e_commerce.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.Fetch;

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
@Table(name = "danhmuc")
public class DanhMuc {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DM_ID")
	private int id;
	
	@Column(name = "DM_TEN")
	private String ten;
	
	@Column(name = "DM_ANH")
	private String duongDanAnh;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "DM_CHA_ID")
	private DanhMuc danhMucCha;
	
	@OneToMany(mappedBy = "danhMucCha")
    private List<DanhMuc> danhSach;

    
	
	public List<DanhMuc> getDanhSach() {
		return danhSach;
	}

	public void setDanhSach(List<DanhMuc> danhSach) {
		this.danhSach = danhSach;
	}

	@ManyToMany
	@JoinTable(
		name = "danhmuc_thongso",
		joinColumns = @JoinColumn(name ="DM_ID"),
	inverseJoinColumns = @JoinColumn(name="TS_ID")
			)
	private Set<ThongSo> thongSo= new HashSet<ThongSo>();
	
	
	@JsonIgnore
	@OneToMany(mappedBy ="danhMuc",fetch = FetchType.LAZY)
	private List<SanPham> sanPham= new ArrayList<SanPham>();
	
	@OneToMany(mappedBy = "danhMucCha")
	private List<DanhMuc> danhMucCon= new ArrayList<DanhMuc>();
	

	public List<DanhMuc> getDanhMucCon() {
		return danhMucCon;
	}

	public void setDanhMucCon(List<DanhMuc> danhMucCon) {
		this.danhMucCon = danhMucCon;
	}

	public List<SanPham> getSanPham() {
		return sanPham;
	}

	public void setSanPham(List<SanPham> sanPham) {
		this.sanPham = sanPham;
	}

	public Set<ThongSo> getThongSo() {
		return thongSo;
	}

	public void setThongSo(Set<ThongSo> thongSo) {
		this.thongSo = thongSo;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTen() {
		return ten;
	}

	public void setTen(String ten) {
		this.ten = ten;
	}

	public String getDuongDanAnh() {
		return duongDanAnh;
	}

	public void setDuongDanAnh(String duongDanAnh) {
		this.duongDanAnh = duongDanAnh;
	}

	public DanhMuc getDanhMucCha() {
		return danhMucCha;
	}

	public void setDanhMucCha(DanhMuc danhMucCha) {
		this.danhMucCha = danhMucCha;
	}
	
	public static List<Integer> layTatCaIdDanhMucCon(DanhMuc danhMucCha) {
	    List<Integer> ketQua = new ArrayList<>();
	    if (danhMucCha == null) return ketQua;

	    ketQua.add(danhMucCha.getId());
	    if (danhMucCha.getDanhMucCon() != null) {
	        for (DanhMuc con : danhMucCha.getDanhMucCon()) {
	            ketQua.addAll(layTatCaIdDanhMucCon(con));
	        }
	    }

	    return ketQua;
	}
	public static List<Integer> layTatCaIdDanhMucConKhongLayNo(DanhMuc danhMucCha) {
	    List<Integer> ketQua = new ArrayList<>();
	    if (danhMucCha == null) return ketQua;

	    if (danhMucCha.getDanhMucCon() != null) {
	        for (DanhMuc con : danhMucCha.getDanhMucCon()) {
	            ketQua.add(con.getId()); // thêm id của con
	            ketQua.addAll(layTatCaIdDanhMucConKhongLayNo(con)); // đệ quy lấy các cháu, chắt
	        }
	    }

	    return ketQua;
	}

}

