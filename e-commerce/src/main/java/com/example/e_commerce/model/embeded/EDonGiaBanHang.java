package com.example.e_commerce.model.embeded;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class EDonGiaBanHang implements Serializable {
	 @Column(name = "BT_ID")
	 private int bienTheId;

	 @Column(name = "DGB_THOIDIEM")
	 private LocalDateTime thoiDiem;

	 public long getIdSanPham() {
	        return bienTheId;
	 }

	 public void setIdSanPham(int bienTheId) {
	        this.bienTheId = bienTheId;
	  }

	    public LocalDateTime getThoiDiem() {
	        return thoiDiem;
	    }

	    public void setThoiDiem(LocalDateTime thoiDiem) {
	        this.thoiDiem = thoiDiem;
	    }

	    @Override
	    public boolean equals(Object o) {
	        if (this == o) return true;
	        if (!(o instanceof EDonGiaBanHang)) return false;
	        EDonGiaBanHang that = (EDonGiaBanHang) o;
	        return bienTheId == that.bienTheId &&
	               Objects.equals(thoiDiem, that.thoiDiem);
	    }

	    @Override
	    public int hashCode() {
	        return Objects.hash(bienTheId, thoiDiem);
	    }
}
