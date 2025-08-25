package com.example.e_commerce.model;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "thongsocuthe")
public class ThongSoCuThe {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TSCT_ID")
	@Min(value = 0,message = "Không tìm thấy thông số cụ thể cần thao tác")
	private long id;
	
	@Column(name = "TSCT_TEN")
	@NotBlank(message = "Tên thông số cụ thể không được để trống")
	private String ten;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "TS_ID")
	private ThongSo thongSo;
	
	@JsonIgnore
	@ManyToMany(mappedBy = "thongSoCuThe")
	private Set<SanPham> sanPham;
	
	
	

	public Set<SanPham> getSanPham() {
		return sanPham;
	}

	public void setSanPham(Set<SanPham> sanPham) {
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

	public ThongSo getThongSo() {
		return thongSo;
	}

	public void setThongSo(ThongSo thongSo) {
		this.thongSo = thongSo;
	}
	@Override
	public boolean equals(Object o) {
	    if (this == o) return true;
	    if (o == null || getClass() != o.getClass()) return false;
	    ThongSoCuThe that = (ThongSoCuThe) o;
	    return id == that.id;
	}

	@Override
	public int hashCode() {
	    return Long.hashCode(id);
	}

	
}
