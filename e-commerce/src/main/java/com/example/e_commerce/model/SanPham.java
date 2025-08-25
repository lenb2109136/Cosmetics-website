package com.example.e_commerce.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "sanpham")
public class SanPham {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SP_ID")
	private long id;
	
	@Column(name = "SP_TEN")
	private String ten;
	
	@Column(name = "SP_MOTA")
	private String moTa;
	
	@Column(name = "SP_THANHPHAN")
	private String thanhPhan;
	
	@Column(name = "SP_CACHDUNG")
	private String cachDung;
	
	@Column(name = "SP_CONDUNG")
	private boolean conDung=true;
	
	@Column(name = "SP_GIAMACDINH")
	private float giaMacDinh;
	
	@ManyToOne
	@JoinColumn(name = "DMM_ID")
	private DanhMuc danhMuc;
	
	@ManyToOne
	@JoinColumn(name = "TH_ID")
	private ThuongHieu thuongHieu;
	
	@Column(name = "SP_THUEVAT")
	private float thuevat;
	
	
	@ManyToMany
	@JoinTable(
	name = "sanpham_thongsocuthe",
	joinColumns = @JoinColumn(name ="SP_ID"),
	inverseJoinColumns = @JoinColumn(name="TSCT_ID")
			)
	private List<ThongSoCuThe> thongSoCuThe;
	
	
	@OneToMany(mappedBy = "sanPham")
	private List<BienThe> bienThe;
	
	
	@Column(name = "SP_ANHBIA")
	private String anhBia;
	
	@JsonIgnore
	@OneToMany(mappedBy = "sanPham")
	private List<TruyCap> truyCap;
	
	@OneToMany(mappedBy = "sanPham")
	private List<AnhGioiThieu> anhGioiThieus;
	
	@OneToMany(mappedBy = "sanPham")
	private List<PhanHoi> phanHois;
	

	// Những routine mà khách hàng đã like
	
	
	public List<BienThe> getBienThe() {
		return bienThe;
	}

	public void setBienThe(List<BienThe> bienThe) {
		this.bienThe = bienThe;
	}

	public List<PhanHoi> getPhanHois() {
		return phanHois;
	}

	public void setPhanHois(List<PhanHoi> phanHois) {
		this.phanHois = phanHois;
	}

	public List<AnhGioiThieu> getAnhGioiThieus() {
		return anhGioiThieus;
	}

	public void setAnhGioiThieus(List<AnhGioiThieu> anhGioiThieus) {
		this.anhGioiThieus = anhGioiThieus;
	}

	public float getThuevat() {
		return thuevat;
	}

	public void setThuevat(float thuevat) {
		this.thuevat = thuevat;
	}

	public List<TruyCap> getTruyCap() {
		return truyCap;
	}

	public void setTruyCap(List<TruyCap> truyCap) {
		this.truyCap = truyCap;
	}

	public String getAnhBia() {
		return anhBia;
	}

	public void setAnhBia(String anhBia) {
		this.anhBia = anhBia;
	}

	public ThuongHieu getThuongHieu() {
		return thuongHieu;
	}

	public void setThuongHieu(ThuongHieu thuongHieu) {
		this.thuongHieu = thuongHieu;
	}

	
	

	public List<ThongSoCuThe> getThongSoCuThe() {
		return thongSoCuThe;
	}

	public void setThongSoCuThe(List<ThongSoCuThe> thongSoCuThe) {
		this.thongSoCuThe = thongSoCuThe;
	}

	public boolean isConDung() {
		return conDung;
	}

	public void setConDung(boolean conDung) {
		this.conDung = conDung;
	}

	
	
	public List<BienThe> getBienTheOfConditionNotCheckActive(boolean isFormat) {

	    Optional<BienThe> optionalDefault = bienThe.stream()
	        .filter(d -> "DEFAULT_SYSTEM_NAME_CLASSIFY".equals(d.getTen()))
	        .findFirst();

	    List<BienThe> filtered = bienThe.stream()
	        .filter(d -> !"DEFAULT_SYSTEM_NAME_CLASSIFY".equals(d.getTen()) && d.isConSuDung())
	        .collect(Collectors.toList());

	    if (filtered.isEmpty()) {
	        if (optionalDefault.isPresent()) {
	            BienThe macDinh = optionalDefault.get();
	            if (isFormat) {
	                macDinh.setTen("Mặc định");
	            }
	            return Collections.singletonList(macDinh);
	        } else {
	            return Collections.emptyList();
	        }
	    } else {
	        return bienThe.stream()
	            .filter(d -> !"DEFAULT_SYSTEM_NAME_CLASSIFY".equals(d.getTen()))
	            .collect(Collectors.toList());
	    }
	}

	public List<BienThe> getBienTheOfConditionCheckActive(boolean isFormat) {
	    Optional<BienThe> optionalDefault = bienThe.stream()
	        .filter(d -> "DEFAULT_SYSTEM_NAME_CLASSIFY".equals(d.getTen()))
	        .findFirst();

	    List<BienThe> filtered = bienThe.stream()
	        .filter(d -> !"DEFAULT_SYSTEM_NAME_CLASSIFY".equals(d.getTen()) && d.isConSuDung())
	        .collect(Collectors.toList());

	    if (filtered.isEmpty()) {
	        if (optionalDefault.isPresent()) {
	            BienThe macDinh = optionalDefault.get();
	            if (isFormat) {
	                macDinh.setTen("Mặc định");
	            }
	            return Collections.singletonList(macDinh);
	        } else {
	            return Collections.emptyList();
	        }
	    } else {
	        return bienThe.stream()
	            .filter(d -> !"DEFAULT_SYSTEM_NAME_CLASSIFY".equals(d.getTen()))
	            .collect(Collectors.toList());
	    }
	}

	
	public List<BienThe> getAllBienTheNotCheckActive(boolean isFormat){
		if(isFormat) {
			bienThe.forEach(d->{
				if(d.getTen().equals("DEFAULT_SYSTEM_NAME_CLASSIFY")) {
					d.setTen("Mặc định");
				}
			});
		}
		return bienThe;
	}
	public List<BienThe> getAllBienTheCheckActive(boolean isFormat){
		if(isFormat) {
			bienThe.forEach(d->{
				if(d.getTen().equals("DEFAULT_SYSTEM_NAME_CLASSIFY")) {
					d.setTen("Mặc định");
				}
			});
		}
		return bienThe.stream().filter(d->d.isConSuDung()).collect(Collectors.toList());
	}
	
	public List<BienThe> getOnlyNotDefault(Integer consuDung) {
	    return bienThe.stream()
	        .filter(d -> !"DEFAULT_SYSTEM_NAME_CLASSIFY".equals(d.getTen()))
	        .filter(d ->
	            consuDung == null || consuDung == 0 ||
	            (consuDung == 1 && d.isConSuDung()) ||
	            (consuDung == -1 && !d.isConSuDung())
	        )
	        .collect(Collectors.toList());
	}
	public BienThe getbienTheDefault() {
	    return bienThe.stream()
	        .filter(d -> "DEFAULT_SYSTEM_NAME_CLASSIFY".equals(d.getTen()))
	        .findFirst()
	        .orElse(null); 
	}




	

//	public List<BienThe> getBienThe() {
//		return bienThe;
//	}
//
//	public void setBienThe(List<BienThe> bienThe) {
//		this.bienThe = bienThe;
//	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTen() {
		return ten;
	}

	public void setTen(String ten) {
		this.ten = ten;
	}

	public String getMoTa() {
		return moTa;
	}

	public void setMoTa(String moTa) {
		this.moTa = moTa;
	}

	public String getThanhPhan() {
		return thanhPhan;
	}

	public void setThanhPhan(String thanhPhan) {
		this.thanhPhan = thanhPhan;
	}

	public String getCachDung() {
		return cachDung;
	}

	public void setCachDung(String cachDung) {
		this.cachDung = cachDung;
	}

	public float getGiaMacDinh() {
		return giaMacDinh;
	}

	public void setGiaMacDinh(float giaMacDinh) {
		this.giaMacDinh = giaMacDinh;
	}

	public DanhMuc getDanhMuc() {
		return danhMuc;
	}

	public void setDanhMuc(DanhMuc danhMuc) {
		this.danhMuc = danhMuc;
	}
	
	@Override
	public boolean equals(Object o) {
	    if (this == o) return true;
	    if (o == null || getClass() != o.getClass()) return false;
	    SanPham sanPham = (SanPham) o;
	    return Objects.equals(this.id, sanPham.id);
	}

	@Override
	public int hashCode() {
	    return Objects.hash(id);
	}

}
