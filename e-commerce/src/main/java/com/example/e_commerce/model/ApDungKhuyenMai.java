package com.example.e_commerce.model;

import java.time.LocalDateTime;

import com.example.e_commerce.model.embeded.EApDungKhuyenMai;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "apdungkhuyenmai")
public class ApDungKhuyenMai {

    @EmbeddedId
    private EApDungKhuyenMai id = new EApDungKhuyenMai(); 

    @ManyToOne
    @MapsId("HD_ID")
    @JoinColumn(name = "HD_ID")
    private HoaDon hoaDon;

    @ManyToOne
    @MapsId("BT_ID")
    @JoinColumn(name = "BT_ID")
    private BienThe bienThe;

    @ManyToOne
    @MapsId("KM_ID")
    @JoinColumn(name = "KM_ID")
    private KhuyenMai khuyenMai;
    
    @Column(name = "TTHD_DEALPHU")
    private boolean dealPhu=true;

    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "HD_ID", referencedColumnName = "HD_ID", insertable = false, updatable = false),
        @JoinColumn(name = "BT_ID", referencedColumnName = "BT_ID", insertable = false, updatable = false)
    })
    private ChiTietHoaDon chiTietHoaDon;

    @Column(name = "ADKM_THOIGIANAPDUNG")
    private LocalDateTime thoiDiemApDung;

    @Column(name = "ADKM_TYLEAPDUNG")
    private float tyLeApDung;

    @Column(name = "ADKM_SOLUONGAPDUNG")
    private int soLuongApDung;
    
    
    

    public boolean isDealPhu() {
		return dealPhu;
	}

	public void setDealPhu(boolean dealPhu) {
		this.dealPhu = dealPhu;
	}

	public void setHoaDon(HoaDon hoaDon) {
        this.hoaDon = hoaDon;
        if (hoaDon != null) {
            this.id.setHdId(hoaDon.getId());
        }
    }

    public void setBienThe(BienThe bienThe) {
        this.bienThe = bienThe;
        if (bienThe != null) {
            this.id.setBtId(bienThe.getId());
        }
    }

    public void setKhuyenMai(KhuyenMai khuyenMai) {
        this.khuyenMai = khuyenMai;
        if (khuyenMai != null) {
            this.id.setKmId(khuyenMai.getId());
        }
    }


    public EApDungKhuyenMai getId() {
        return id;
    }

    public void setId(EApDungKhuyenMai id) {
        this.id = id;
    }

    public HoaDon getHoaDon() {
        return hoaDon;
    }

    public BienThe getBienThe() {
        return bienThe;
    }

    public KhuyenMai getKhuyenMai() {
        return khuyenMai;
    }

	public ChiTietHoaDon getChiTietHoaDon() {
		return chiTietHoaDon;
	}

	public void setChiTietHoaDon(ChiTietHoaDon chiTietHoaDon) {
		this.chiTietHoaDon = chiTietHoaDon;
	}

	public LocalDateTime getThoiDiemApDung() {
		return thoiDiemApDung;
	}

	public void setThoiDiemApDung(LocalDateTime thoiDiemApDung) {
		this.thoiDiemApDung = thoiDiemApDung;
	}

	public float getTyLeApDung() {
		return tyLeApDung;
	}

	public void setTyLeApDung(float tyLeApDung) {
		this.tyLeApDung = tyLeApDung;
	}

	public int getSoLuongApDung() {
		return soLuongApDung;
	}

	public void setSoLuongApDung(int soLuongApDung) {
		this.soLuongApDung = soLuongApDung;
	}
    
    
}


