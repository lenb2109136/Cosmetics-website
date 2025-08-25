package com.example.e_commerce.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.e_commerce.model.ThongSo;
import com.example.e_commerce.model.ThongSoCuThe;
import com.example.e_commerce.model.ThuongHieu;
import com.example.e_commerce.repository.ThongSoCuTheRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ThongSoCuTheService {
	@Autowired
	private ThongSoCuTheRepository thongSoCuTheRepository;
	
	
	public ThongSoCuThe getThongCuTheById(long id) {
		return thongSoCuTheRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("Không tìm thấy thông số phù hợp"));
	}
	
	@Transactional
	public void save(List<ThongSoCuThe> thongSoCuThe, ThongSo thongSo) {
		thongSoCuThe.forEach(data->{
			data.setThongSo(thongSo);
			thongSoCuTheRepository.save(data);
		});
	}
	@Transactional
	public void save(ThongSoCuThe thongSo) {
		thongSoCuTheRepository.save(thongSo);
	}
	@Transactional
	public void delete(long id) {
		ThongSoCuThe thongSoCuThe=thongSoCuTheRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("Không tìm thấy thông số cụ thể cần xóa"));
		thongSoCuTheRepository.deleteThongSoCuThe(thongSoCuThe.getId());
		thongSoCuTheRepository.delete(thongSoCuThe);
	}
	
	@Transactional
	public void deleteAll(List<ThongSoCuThe> dstsct) {
		dstsct.forEach(data->{
			delete(data.getId());
		});
	}
	
	public List<ThongSoCuThe> getThuongHieuBySanPhamAndDanhMuc(String tenSanPham, List<Integer> idDanhMuc){
		System.out.println(idDanhMuc);
		System.out.println(tenSanPham);
		System.out.println("số lượng : "+thongSoCuTheRepository.getThuongHieuBySanPhamAndDanhMuc(tenSanPham, idDanhMuc).size());
		return thongSoCuTheRepository.getThuongHieuBySanPhamAndDanhMuc(tenSanPham, idDanhMuc);
	}
	
}
