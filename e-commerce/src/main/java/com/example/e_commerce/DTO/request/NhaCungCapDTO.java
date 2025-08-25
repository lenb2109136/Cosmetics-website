package com.example.e_commerce.DTO.request;

import com.example.e_commerce.DTO.response.AdressDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class NhaCungCapDTO {
	private Long code;
	@NotBlank(message = "Tên không được để trống")
//    @Size(min = 2, max = 100, message = "Tên phải từ 2 đến 100 ký tự")
    @Pattern(regexp = "^[\\p{L} ]+$", message = "Tên chỉ được chứa chữ và khoảng trắng")
    private String name;

    @NotBlank(message = "Địa chỉ không được để trống")
    @Pattern(regexp = "^[\\p{L}0-9 ,.-]+$", message = "Địa chỉ chứa ký tự không hợp lệ")
    private String diaChi;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^(\\+84|0)[35789]\\d{8}$", message = "Số điện thoại không hợp lệ")
    private String soDienThoai;

    @NotBlank(message = "Mã số thuế không được để trống")
    @Pattern(regexp = "^\\d{10}(\\d{3})?$", message = "Mã số thuế phải có 10 hoặc 13 chữ số")
    private String maSoThue;

    @NotBlank(message = "Số tài khoản không được để trống")
    @Pattern(regexp = "^\\d{8,20}$", message = "Số tài khoản phải có từ 8 đến 20 chữ số")
    private String soTaiKhoan;
	AdressDTO tinh;
	AdressDTO huyen;
	AdressDTO xa;
	public long getCode() {
		return code;
	}
	public void setCode(long code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDiaChi() {
		return diaChi;
	}
	public void setDiaChi(String diaChi) {
		this.diaChi = diaChi;
	}
	public String getSoDienThoai() {
		return soDienThoai;
	}
	public void setSoDienThoai(String soDienThoai) {
		this.soDienThoai = soDienThoai;
	}
	public String getMaSoThue() {
		return maSoThue;
	}
	public void setMaSoThue(String maSoThue) {
		this.maSoThue = maSoThue;
	}
	public String getSoTaiKhoan() {
		return soTaiKhoan;
	}
	public void setSoTaiKhoan(String soTaiKhoan) {
		this.soTaiKhoan = soTaiKhoan;
	}
	public AdressDTO getTinh() {
		return tinh;
	}
	public void setTinh(AdressDTO tinh) {
		this.tinh = tinh;
	}
	public AdressDTO getHuyen() {
		return huyen;
	}
	public void setHuyen(AdressDTO huyen) {
		this.huyen = huyen;
	}
	public AdressDTO getXa() {
		return xa;
	}
	public void setXa(AdressDTO xa) {
		this.xa = xa;
	}
	
	
}
