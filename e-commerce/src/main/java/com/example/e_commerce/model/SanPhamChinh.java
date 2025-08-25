package com.example.e_commerce.model;

import com.example.e_commerce.model.embeded.EBienTheDealPhu;
import com.example.e_commerce.model.embeded.ESanPhamChinh;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "sanphamchinh")
public class SanPhamChinh {
	@EmbeddedId
    private ESanPhamChinh id;

    @MapsId("btId")
    @ManyToOne
    @JoinColumn(name = "BT_ID")
    private BienThe bienThe;

    @MapsId("kmId")
    @ManyToOne
    @JoinColumn(name = "KM_ID")
    private KhuyenMaiTangKem khuyenMaiTangKem;
    
    @Column(name = "SPC_SOLUONGTU")
	private long  soLuongTu;
    
    @Column(name = "SPC_CONSUDUNG")
	private boolean conSuDung;

	

	public ESanPhamChinh getId() {
		return id;
	}

	public void setId(ESanPhamChinh id) {
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

	public long getSoLuongTu() {
		return soLuongTu;
	}

	public void setSoLuongTu(long soLuongTu) {
		this.soLuongTu = soLuongTu;
	}

	public boolean isConSuDung() {
		return conSuDung;
	}

	public void setConSuDung(boolean conSuDung) {
		this.conSuDung = conSuDung;
	}
    
    
}
