package com.example.e_commerce.model.chat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.example.e_commerce.Validator.TimeValidator;
import com.example.e_commerce.constants.MessageType;
import com.example.e_commerce.model.TinNhan;

public class Message extends chatRoot{
	Long khachHang;
	String tenKhach;
	String noiDungTinNhan;
	String ngayGui;
	List<String> listImage;
	int statusKhachHang;
	int statusNhanVien;
	boolean daTim;
	long nhanvien;
	String tenNhanVien;
	long IDTinNhan;
	String thoiHanNhan;
	LocalDateTime ngayGioNhan;
	long re;
	String noiDungRe;
	

	public static Message getMessgaeFromTinNhan(TinNhan t) {
	    Message m = new Message();

	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm");
	    String formattedDate = t.getNgayGioNhan().format(formatter);
	    m.setThoiHanNhan(TimeValidator.getThoiGianTuNgay(t.getNgayGioNhan()));
	    m.setNoiDungTinNhan(t.getNoiDungTinNhan());
	    m.setDaTim(t.isDaTim());
	    m.setIDTinNhan(t.getId());
	    m.setKhachHang(t.getKhachHang().getId());
	    m.setListImage(
	        t.getAnhChat() != null
	            ? t.getAnhChat().stream().map(d -> d.getDuongDan()).collect(Collectors.toList())
	            : new ArrayList<>()
	    );
	    m.setNgayGui(formattedDate); 
	    m.setNhanvien(t.getNhanVien() != null ? t.getNhanVien().getId() : 0);
	    m.setStatusKhachHang(t.getStatusKhachHang());
	    m.setStatusNhanVien(t.getStatusNhanVien());
	    m.setTenKhach(t.getKhachHang().getTen());
	    m.setTenNhanVien(t.getNhanVien() != null ? t.getNhanVien().getTen() : "");
	    if(t.getReply()!=null) {
	    	m.setRe(t.getReply().getId());
	    	m.setNoiDungRe(t.getReply().getNoiDungTinNhan());
	    }
	    else {
	    	m.setRe(0);
	    }
	    m.setType(MessageType.MESSAGE);
	    return m;
	}

	
	
	public String getNoiDungRe() {
		return noiDungRe;
	}



	public void setNoiDungRe(String noiDungRe) {
		this.noiDungRe = noiDungRe;
	}



	public String getThoiHanNhan() {
		return thoiHanNhan;
	}



	public long getRe() {
		return re;
	}



	public void setRe(long re) {
		this.re = re;
	}



	public void setThoiHanNhan(String thoiHanNhan) {
		this.thoiHanNhan = thoiHanNhan;
	}



	public String getTenNhanVien() {
		return tenNhanVien;
	}


	public void setTenNhanVien(String tenNhanVien) {
		this.tenNhanVien = tenNhanVien;
	}


	public Long getKhachHang() {
		return khachHang;
	}
	public void setKhachHang(Long khachHang) {
		this.khachHang = khachHang;
	}
	public String getTenKhach() {
		return tenKhach;
	}
	public void setTenKhach(String tenKhach) {
		this.tenKhach = tenKhach;
	}
	public long getNhanvien() {
		return nhanvien;
	}
	public void setNhanvien(long nhanvien) {
		this.nhanvien = nhanvien;
	}
	public long getIDTinNhan() {
		return IDTinNhan;
	}
	public void setIDTinNhan(long iDTinNhan) {
		IDTinNhan = iDTinNhan;
	}
	public boolean isDaTim() {
		return daTim;
	}
	public void setDaTim(boolean daTim) {
		this.daTim = daTim;
	}
	
	public int getStatusNhanVien() {
		return statusNhanVien;
	}
	public void setStatusNhanVien(int statusNhanVien) {
		this.statusNhanVien = statusNhanVien;
	}
	
	
	public String getNoiDungTinNhan() {
		return noiDungTinNhan;
	}



	public void setNoiDungTinNhan(String noiDungTinNhan) {
		this.noiDungTinNhan = noiDungTinNhan;
	}



	public int getStatusKhachHang() {
		return statusKhachHang;
	}



	public void setStatusKhachHang(int statusKhachHang) {
		this.statusKhachHang = statusKhachHang;
	}



	public LocalDateTime getNgayGioNhan() {
		return ngayGioNhan;
	}



	public void setNgayGioNhan(LocalDateTime ngayGioNhan) {
		this.ngayGioNhan = ngayGioNhan;
	}



	public List<String> getListImage() {
		return listImage;
	}
	
	
	
	public String getNgayGui() {
		return ngayGui;
	}


	public void setNgayGui(String ngayGui) {
		this.ngayGui = ngayGui;
	}


	public void setListImage(List<String> listImage) {
		this.listImage = listImage;
	}
	
}
