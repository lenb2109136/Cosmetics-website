package com.example.e_commerce.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "hoadon")
@Inheritance(strategy = InheritanceType.JOINED)
public class HoaDon {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "HD_ID")
	private long id;
	
	@Column(name = "HD_DIACHI")
	private String diaChi;
	
	
	
	@Column(name = "HD_ZPTR")
	private Long HD_zpTrans_Id;
	
	
	
	@Column(name = "HD_QRCODE")
	private String qRCode;
	
	
	@Column(name = "HD_HINHTHUCHOANTRA")
	private int hinhThucHoanTra=-1;
	
	
	
	// hiểu ngược lại là apptransid đi vì đổi sang zalopay rồi
	@Column(name = "HD_TRANSID")
	private String transId;
	
	
	@Column(name = "HD_DATHANHTOAN")
	private boolean daThanhTona=false;
	
	
	@Column(name = "HD_DAHOANTIEN")
	private boolean daHoanHang=false;
	
	@Column(name = "HD_NGAYLAP")
	private LocalDateTime ngayLap;
	
	@Column(name = "HD_TONGTIEN")
	private float tongTien;
	
	@JoinColumn(name = "KH_ID")
	@ManyToOne
	@JsonIgnore
	private KhachHang khachHang;
	
	@ManyToOne
	@JoinColumn(name = "TT_ID")
	private TrangThai trangThai;
	
	
	
	@OneToMany(mappedBy = "hoaDon")
	@JsonIgnore
	private List<ChiTietHoaDon> chiTietHoaDons;
	
	@OneToMany(mappedBy = "hoaDon")
	private List<TrangThaiHoaDon> trangThaiHoaDon;
	
	
	
	
	

	public long getHD_zpTrans_Id() {
		return HD_zpTrans_Id;
	}

	public void setHD_zpTrans_Id(long hD_zpTrans_Id) {
		HD_zpTrans_Id = hD_zpTrans_Id;
	}

	public boolean isDaThanhTona() {
		return daThanhTona;
	}

	public void setDaThanhTona(boolean daThanhTona) {
		this.daThanhTona = daThanhTona;
	}
	
	

	public String getDiaChi() {
		return diaChi;
	}

	public void setDiaChi(String diaChi) {
		this.diaChi = diaChi;
	}

	public int getHinhThucHoanTra() {
		return hinhThucHoanTra;
	}

	
	public void setHinhThucHoanTra(int hinhThucHoanTra) {
		this.hinhThucHoanTra = hinhThucHoanTra;
	}

	public boolean isDaHoanHang() {
		return daHoanHang;
	}

	public void setDaHoanHang(boolean daHoanHang) {
		this.daHoanHang = daHoanHang;
	}

	

	public List<TrangThaiHoaDon> getTrangThaiHoaDon() {
		return trangThaiHoaDon;
	}

	public void setTrangThaiHoaDon(List<TrangThaiHoaDon> trangThaiHoaDon) {
		this.trangThaiHoaDon = trangThaiHoaDon;
	}

	public String getqRCode() {
		return qRCode;
	}

	public void setqRCode(String qRCode) {
		this.qRCode = qRCode;
	}

	

	public String getTransId() {
		return transId;
	}

	public void setTransId(String transId) {
		this.transId = transId;
	}

	public List<ChiTietHoaDon> getChiTietHoaDons() {
		return chiTietHoaDons;
	}

	public void setChiTietHoaDons(List<ChiTietHoaDon> chiTietHoaDons) {
		this.chiTietHoaDons = chiTietHoaDons;
	}

	public TrangThai getTrangThai() {
		return trangThai;
	}

	public void setTrangThai(TrangThai trangThai) {
		this.trangThai = trangThai;
	}

	public KhachHang getKhachHang() {
		return khachHang;
	}

	public void setKhachHang(KhachHang khachHang) {
		this.khachHang = khachHang;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public LocalDateTime getNgayLap() {
		return ngayLap;
	}

	public void setNgayLap(LocalDateTime ngayLap) {
		this.ngayLap = ngayLap;
	}

	public float getTongTien() {
		return tongTien;
	}

	public void setTongTien(float tongTien) {
		this.tongTien = tongTien;
	}
	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HoaDon hoaDon = (HoaDon) o;
        return id == hoaDon.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    public String getRefund() {
        String appId = "2553";
        String datePart = ngayLap != null ? ngayLap.format(DateTimeFormatter.ofPattern("yyMMdd")) : LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
       
        String uniqueId=getId()+ "skinly";
        return String.format("%s_%s_%s", datePart, appId, uniqueId);
    }

	
}
