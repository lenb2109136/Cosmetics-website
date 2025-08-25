package com.example.e_commerce.model.embeded;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class EThueVATSanPham implements Serializable {

    @Column(name = "SP_ID")
    private long idSanPham;

    @Column(name = "TSP_THOIDIEM")
    private LocalDateTime thoiDiem;

    public long getIdSanPham() {
        return idSanPham;
    }

    public void setIdSanPham(long idSanPham) {
        this.idSanPham = idSanPham;
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
        if (!(o instanceof EThueVATSanPham)) return false;
        EThueVATSanPham that = (EThueVATSanPham) o;
        return idSanPham == that.idSanPham &&
                 Objects.equals(thoiDiem, that.thoiDiem);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idSanPham, thoiDiem);
    }
}
