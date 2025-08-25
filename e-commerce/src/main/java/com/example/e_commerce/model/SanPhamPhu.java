package com.example.e_commerce.model;

import com.example.e_commerce.model.embeded.EBienTheDealPhu;
import com.example.e_commerce.model.embeded.ESanPhamPhu;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
@Entity
@Table(name = "sanphamtangkem")
public class SanPhamPhu {
	@EmbeddedId
    private ESanPhamPhu id;

    @MapsId("btId")
    @ManyToOne
    @JoinColumn(name = "BT_ID")
    private BienThe bienThe;

    @MapsId("kmId")
    @ManyToOne
    @JoinColumn(name = "KM_ID")
    private KhuyenMaiTangKem khuyenMaiTangKem;
    
    @Column(name = "SPTK_SOLUONG")
	private long  soLuongTang;
    
    @Column(name = "SPTK_CONSUDUNG")
	private boolean conSuDung;

	

	public ESanPhamPhu getId() {
		return id;
	}

	public void setId(ESanPhamPhu id) {
		this.id = id;
	}

	public BienThe getBienThe() {
		return bienThe;
	}

	public void setBienThe(BienThe bienThe) {
		this.id.setBtId(bienThe.getId());
		this.bienThe = bienThe;
	}

	public KhuyenMaiTangKem getKhuyenMaiTangKem() {
		return khuyenMaiTangKem;
	}

	public void setKhuyenMaiTangKem(KhuyenMaiTangKem khuyenMaiTangKem) {
		this.id.setKmId(khuyenMaiTangKem.getId());
		this.khuyenMaiTangKem = khuyenMaiTangKem;
	}

	public long getSoLuongTang() {
		return soLuongTang;
	}

	public void setSoLuongTang(long soLuongTang) {
		this.soLuongTang = soLuongTang;
	}

	public boolean isConSuDung() {
		return conSuDung;
	}

	public void setConSuDung(boolean conSuDung) {
		this.conSuDung = conSuDung;
	}
    
    
}