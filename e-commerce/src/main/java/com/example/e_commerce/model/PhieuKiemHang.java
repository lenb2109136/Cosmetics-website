package com.example.e_commerce.model;

import java.time.LocalDateTime;
import java.util.List;

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
@Table(name = "phieukiemhang")
public class PhieuKiemHang {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PKH_ID")
	private long id;
	
	@Column(name = "PKH_NGAYLAP")
	private LocalDateTime ngayYeuCau;
	
	@ManyToOne
	@JoinColumn(name = "NV_ID")
	private NhanVien nhanVien;
	
	
	@Column(name = "PKH_DAXACNHAN")
	private boolean daXacNhan;
	
	
	@OneToMany(mappedBy = "phieuKiemHang")
	private List<SanPhamKiem> sanPhamKiem;
	
	
	

	public List<SanPhamKiem> getSanPhamKiem() {
		return sanPhamKiem;
	}

	public void setSanPhamKiem(List<SanPhamKiem> sanPhamKiem) {
		this.sanPhamKiem = sanPhamKiem;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public LocalDateTime getNgayYeuCau() {
		return ngayYeuCau;
	}

	public void setNgayYeuCau(LocalDateTime ngayYeuCau) {
		this.ngayYeuCau = ngayYeuCau;
	}

	
	public boolean isDaXacNhan() {
		return daXacNhan;
	}

	public void setDaXacNhan(boolean daXacNhan) {
		this.daXacNhan = daXacNhan;
	}

	public NhanVien getNhanVien() {
		return nhanVien;
	}

	public void setNhanVien(NhanVien nhanVien) {
		this.nhanVien = nhanVien;
	}

	
}


