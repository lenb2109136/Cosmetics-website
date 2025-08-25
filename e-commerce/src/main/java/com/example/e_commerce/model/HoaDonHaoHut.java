package com.example.e_commerce.model;

import java.time.LocalDateTime;

import com.example.e_commerce.model.embeded.EApDungKhuyenMai;
import com.example.e_commerce.model.embeded.EHoaDonHaoHut;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "hoadonhaohut")
public class HoaDonHaoHut {
	@EmbeddedId
    private EHoaDonHaoHut id = new EHoaDonHaoHut(); 

    @ManyToOne
    @MapsId("HD_ID")
    @JoinColumn(name = "HD_ID")
    private HoaDonOnline hoaDonOnline;

    @ManyToOne
    @MapsId("BT_ID")
    @JoinColumn(name = "BT_ID")
    private BienThe bienThe;

    @ManyToOne
    @MapsId("PKH_ID")
    @JoinColumn(name = "PKH_ID")
    private PhieuKiemHang phieuKiemHang;

    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "PKH_ID", referencedColumnName = "PKH_ID", insertable = false, updatable = false),
        @JoinColumn(name = "BT_ID", referencedColumnName = "BT_ID", insertable = false, updatable = false)
    })
    private SanPhamKiem sanPhamKiem;

	public EHoaDonHaoHut getId() {
		return id;
	}

	public void setId(EHoaDonHaoHut id) {
		this.id = id;
	}

	public HoaDonOnline getHoaDonOnline() {
		return hoaDonOnline;
	}

	public void setHoaDonOnline(HoaDonOnline hoaDonOnline) {
		this.id.setHD_ID(hoaDonOnline.getId());
		this.hoaDonOnline = hoaDonOnline;
	}

	public BienThe getBienThe() {
		return bienThe;
	}

	public void setBienThe(BienThe bienThe) {
		this.id.setBT_ID(bienThe.getId());
		this.bienThe = bienThe;
	}

	public PhieuKiemHang getPhieuKiemHang() {
		return phieuKiemHang;
	}

	public void setPhieuKiemHang(PhieuKiemHang phieuKiemHang) {
		this.id.setPN_ID(phieuKiemHang.getId());
		this.phieuKiemHang = phieuKiemHang;
	}

	public SanPhamKiem getSanPhamKiem() {
		return sanPhamKiem;
	}

	public void setSanPhamKiem(SanPhamKiem sanPhamKiem) {
		this.sanPhamKiem = sanPhamKiem;
	}

    
}
