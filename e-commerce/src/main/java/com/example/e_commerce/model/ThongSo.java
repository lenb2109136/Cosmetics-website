package com.example.e_commerce.model;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name ="thongso")
public class ThongSo {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TS_ID")
//	@Min(value = 1,message = "Không tìm thấy thông số cần thao tác")
	private long id;
	
	@NotBlank(message = "Tên thông số không được để trống")
	@Column(name = "TS_TEN")
	private String ten;
	
	
	@JsonIgnore
	@ManyToMany(mappedBy = "thongSo")
	private Set<DanhMuc> danhMuc;
	
	@OneToMany(mappedBy = "thongSo")
	private List<ThongSoCuThe> thongSoCuThe;
	
	

	public List<ThongSoCuThe> getThongSoCuThe() {
		return thongSoCuThe;
	}

	public void setThongSoCuThe(List<ThongSoCuThe> thongSoCuThe) {
		this.thongSoCuThe = thongSoCuThe;
	}

	public Set<DanhMuc> getDanhMuc() {
		return danhMuc;
	}

	public void setDanhMuc(Set<DanhMuc> danhMuc) {
		this.danhMuc = danhMuc;
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
	
	
}
