package com.example.e_commerce.model.embeded;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class ETrangThaiHoaDon implements Serializable {

    @Column(name = "TTHD_THOIDIEM")
    private LocalDateTime thoiDiem;

    @Column(name = "HD_ID")
    private Long hoaDonId;

    @Column(name = "TT_ID")
    private Long trangThaiId;
    
    

    public LocalDateTime getThoiDiem() {
		return thoiDiem;
	}

	public void setThoiDiem(LocalDateTime thoiDiem) {
		this.thoiDiem = thoiDiem;
	}

	public Long getHoaDonId() {
		return hoaDonId;
	}

	public void setHoaDonId(Long hoaDonId) {
		this.hoaDonId = hoaDonId;
	}

	public Long getTrangThaiId() {
		return trangThaiId;
	}

	public void setTrangThaiId(Long trangThaiId) {
		this.trangThaiId = trangThaiId;
	}

	// equals and hashCode (bắt buộc)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ETrangThaiHoaDon)) return false;
        ETrangThaiHoaDon that = (ETrangThaiHoaDon) o;
        return Objects.equals(thoiDiem, that.thoiDiem) &&
               Objects.equals(hoaDonId, that.hoaDonId) &&
               Objects.equals(trangThaiId, that.trangThaiId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(thoiDiem, hoaDonId, trangThaiId);
    }

    // getters & setters
}
