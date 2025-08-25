package com.example.e_commerce.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "donvicuncap")
public class DonViCungCap {

	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    @Column(name = "DVCC_ID")
	    private Long id;

	    @Column(name = "DVCC_TEN")
	    private String ten;

	    @Column(name = "DVCC_DIACHI")
	    private String diaChi;

	    @Column(name = "DVCC_MASOTHUE")
	    private String maSoThue;

	    @Column(name = "DVCC_SODIENTHOAI")
	    private String soDienThoai;

	    @Column(name = "DVCC_SOTAIKHOAN")
	    private String soTaiKhoan;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getTen() {
			return ten;
		}

		public void setTen(String ten) {
			this.ten = ten;
		}

		public String getDiaChi() {
			return diaChi;
		}

		public void setDiaChi(String diaChi) {
			this.diaChi = diaChi;
		}

		public String getMaSoThue() {
			return maSoThue;
		}

		public void setMaSoThue(String maSoThue) {
			this.maSoThue = maSoThue;
		}

		public String getSoDienThoai() {
			return soDienThoai;
		}

		public void setSoDienThoai(String soDienThoai) {
			this.soDienThoai = soDienThoai;
		}

		public String getSoTaiKhoan() {
			return soTaiKhoan;
		}

		public void setSoTaiKhoan(String soTaiKhoan) {
			this.soTaiKhoan = soTaiKhoan;
		}
	    
	    
}
