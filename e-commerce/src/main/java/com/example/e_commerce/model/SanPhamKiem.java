package com.example.e_commerce.model;

import java.util.List;

import com.example.e_commerce.model.embeded.ESanPhamKiem;
import jakarta.persistence.*;

@Entity
@Table(name = "sanphamkiem")
public class SanPhamKiem {

    @EmbeddedId
    private ESanPhamKiem id= new ESanPhamKiem();

    @MapsId("btId")
    @ManyToOne
    @JoinColumn(name = "BT_ID")
    private BienThe bienThe;

    @MapsId("pkhId")
    @ManyToOne
    @JoinColumn(name = "PKH_ID")
    private PhieuKiemHang phieuKiemHang;

    @Column(name = "PKH_SOLUONG")
    private Integer soLuong;
    
    
    @OneToMany(mappedBy = "sanPhamKiem")
    private List<HoaDonHaoHut> hoaDonHaoHuts;

    @Column(name = "PKH_GHICHU")
    private String ghiChu;

    public SanPhamKiem() {}

    public ESanPhamKiem getId() {
        return id;
    }

    public void setId(ESanPhamKiem id) {
        this.id = id;
    }

	public List<HoaDonHaoHut> getHoaDonHaoHuts() {
		return hoaDonHaoHuts;
	}

	public void setHoaDonHaoHuts(List<HoaDonHaoHut> hoaDonHaoHuts) {
		this.hoaDonHaoHuts = hoaDonHaoHuts;
	}

	public BienThe getBienThe() {
		return bienThe;
	}

	public void setBienThe(BienThe bienThe) {
		this.id.setBtId(bienThe.getId());
		this.bienThe = bienThe;
	}

	public PhieuKiemHang getPhieuKiemHang() {
		return phieuKiemHang;
	}

	public void setPhieuKiemHang(PhieuKiemHang phieuKiemHang) {
		this.id.setPkhId(phieuKiemHang.getId());
		this.phieuKiemHang = phieuKiemHang;
	}

	public Integer getSoLuong() {
		return soLuong;
	}

	public void setSoLuong(Integer soLuong) {
		this.soLuong = soLuong;
	}

	public String getGhiChu() {
		return ghiChu;
	}

	public void setGhiChu(String ghiChu) {
		this.ghiChu = ghiChu;
	}

}
