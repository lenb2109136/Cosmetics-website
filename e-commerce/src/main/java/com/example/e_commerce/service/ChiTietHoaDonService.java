package com.example.e_commerce.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.e_commerce.model.ChiTietHoaDon;
import com.example.e_commerce.model.HoaDon;
import com.example.e_commerce.model.SanPham;
import com.example.e_commerce.model.embeded.EChiTietHoaDon;
import com.example.e_commerce.repository.ChiTietHoaDonRepository;

@Service
public class ChiTietHoaDonService {
@Autowired
ChiTietHoaDonRepository chiTietHoaDonRepository;
public List<ChiTietHoaDon> getHoaDonOfSanPham(
		List<Integer> sp,
		LocalDateTime bd,
		LocalDateTime kt,
		List<Integer> dsTrangThai
		) {
	return chiTietHoaDonRepository.getChiTietHoaDonOfSanPham(sp,bd,kt,dsTrangThai);
}
@Transactional
public void save(ChiTietHoaDon c) {
	chiTietHoaDonRepository.save(c);
}

@Transactional 
public void delete(EChiTietHoaDon e) {
	chiTietHoaDonRepository.deleteById(e);
}
public List<ChiTietHoaDon> getHoaDonOfSanPhamInTime(
        long idkh,
        LocalDateTime bd,
        LocalDateTime kt,
        long idsp
    ) {
        return chiTietHoaDonRepository.getChiTietHoaDonOfSanPhamInTime(idkh, bd, kt,idsp);
    }

public Long getTongSoLuotBan(int id, LocalDateTime bd, LocalDateTime kt, List<Integer> ds) {
	return chiTietHoaDonRepository.soluongBanRa(id,bd,kt,ds);
}

public Long getTongSoLuotBan(List<Integer> id, LocalDateTime bd, LocalDateTime kt, List<Integer> ds) {
	return chiTietHoaDonRepository.soluongBanRa(id,bd,kt,ds);
}
public List<ChiTietHoaDon> getTongSoLuotBanOfFlahSale(List<Integer> id, List<Integer> ds,long idFlashsale) {
	return chiTietHoaDonRepository.getChiTietHoaDonOfBienTheWhereIdBienTheIn(id,ds,idFlashsale);
}
public List<ChiTietHoaDon> getTongSoLuotBanOfDeal(List<Integer> id, List<Integer> ds,long idFlashsale) {
	return chiTietHoaDonRepository.getChiTietHoaDonOfBienTheWhereIdBienTheInOfDeal(id,ds,idFlashsale);
}

public Long getTongSoLuotSuDungTruocDo(int id, LocalDateTime bd, List<Integer> ds, long idkh) {
	return chiTietHoaDonRepository.getChiTietHoaDonOfBienTheWhereIdBienTheInNguoiDungUse(id,bd,ds,idkh);
}


public List<ChiTietHoaDon> getChiTietHoaDonOfBienThe(int id, LocalDateTime bd, LocalDateTime kt, List<Integer> ds) {
	return chiTietHoaDonRepository.getChiTietHoaDonOfBienThe(id,bd,kt,ds);
}

public boolean kiemTraKhachHangDaMuaThanhConChua(SanPham sp, long idkh) {
	long l= chiTietHoaDonRepository.kiemTraKhachHangDaMuaThanhConChua(sp.getAllBienTheNotCheckActive(false)
			.stream().map(d->d.getId()).collect(Collectors.toList()),idkh);
	if(l==0) {
		return false;
	}
	else {
		return true;
	}
}

}
