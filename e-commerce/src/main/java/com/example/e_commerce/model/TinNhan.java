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
@Table(name = "tinnhan")
public class TinNhan {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TN_ID")
	private long id;
	@Column(name = "TN_NOIDUNGTINNHAN")
	private String noiDungTinNhan;
	
	
	
	@Column(name = "TN_HETHONG")
	private boolean tinNhanHeThong=false;
	
	
	@Column(name = "TN_DATIM")
	private boolean daTim=false;
	
	@Column(name = "TN_STATUSKHACHHANG")
	private int statusKhachHang=0;
	@Column(name = "TN_STATUSNHANVIEN")
	private int statusNhanVien=0;
	
	@Column(name = "TN_NGAYGIONHAN")
	private LocalDateTime ngayGioNhan=LocalDateTime.now();
	
	

	
	@ManyToOne
	@JoinColumn(name = "KH_ID")
	private KhachHang khachHang;
	
	@ManyToOne
	@JoinColumn(name = "TN_REPLY")
	private TinNhan reply;
	
	@ManyToOne
	@JoinColumn(name = "NV_ID")
	private NhanVien nhanVien;
	
	@OneToMany(mappedBy = "tinNhan")
	private List<AnhChat> anhChat;
	
	public LocalDateTime getNgayGioNhan() {
		return ngayGioNhan;
	}

	public void setNgayGioNhan(LocalDateTime ngayGioNhan) {
		this.ngayGioNhan = ngayGioNhan;
	}


	public boolean isDaTim() {
		return daTim;
	}

	public void setDaTim(boolean daTim) {
		this.daTim = daTim;
	}

	
	
	public TinNhan getReply() {
		return reply;
	}

	public void setReply(TinNhan reply) {
		this.reply = reply;
	}

	public List<AnhChat> getAnhChat() {
		return anhChat;
	}

	public void setAnhChat(List<AnhChat> anhChat) {
		this.anhChat = anhChat;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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

	public int getStatusNhanVien() {
		return statusNhanVien;
	}

	public void setStatusNhanVien(int statusNhanVien) {
		this.statusNhanVien = statusNhanVien;
	}

	public boolean isTinNhanHeThong() {
		return tinNhanHeThong;
	}

	public void setTinNhanHeThong(boolean tinNhanHeThong) {
		this.tinNhanHeThong = tinNhanHeThong;
	}

	public KhachHang getKhachHang() {
		return khachHang;
	}

	public void setKhachHang(KhachHang khachHang) {
		this.khachHang = khachHang;
	}

	public NhanVien getNhanVien() {
		return nhanVien;
	}

	public void setNhanVien(NhanVien nhanVien) {
		this.nhanVien = nhanVien;
	}
	
	
}

