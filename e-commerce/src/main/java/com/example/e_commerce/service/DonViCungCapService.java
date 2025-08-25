package com.example.e_commerce.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.e_commerce.DTO.request.NhaCungCapDTO;
import com.example.e_commerce.DTO.response.SanPhamPhieuNhap;
import com.example.e_commerce.mapper.DonViCungCapMapper;
import com.example.e_commerce.model.BienThe;
import com.example.e_commerce.model.DonViCungCap;
import com.example.e_commerce.model.SanPham;
import com.example.e_commerce.repository.DonViCungCapRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class DonViCungCapService {
	@Autowired
	DonViCungCapRepository donViCungCapRepository;


	@Transactional
	public void save(NhaCungCapDTO nhaCungCapDTO) {
//		if (nhaCungCapDTO.getCode() != 0) {
//			throw new IllegalArgumentException("ID phải để trống khi tạo mới!");
//		}
		DonViCungCap donViCungCap = DonViCungCapMapper.mapToDonViCungCap(nhaCungCapDTO);
//		System.out.println(donViCungCap.getId());
		if(donViCungCap.getId()==null || donViCungCap.getId()==0) {
			donViCungCapRepository.save(donViCungCap);
		}
		else {
			DonViCungCap d= getById(donViCungCap.getId());
			d.setDiaChi(donViCungCap.getDiaChi());
			d.setMaSoThue(donViCungCap.getMaSoThue());
			d.setSoDienThoai(donViCungCap.getSoDienThoai());
			d.setSoTaiKhoan(donViCungCap.getSoTaiKhoan());
			d.setTen(donViCungCap.getTen());
			donViCungCapRepository.save(d);
		}
	}
	
	public List<DonViCungCap> donViCungCaps(){
		return donViCungCapRepository.findAll();
	}
	
	public DonViCungCap getById(long id) {
		return donViCungCapRepository.findById(id).orElseThrow(()->new EntityNotFoundException("Không tìm thấy đơn vị cung cấp"));
	}

	public Page<Map<Object, Object>> getManagerSupplier(String ten, int p, LocalDate ngaybd, LocalDate ngaykt) {

		Pageable pageRequest = PageRequest.of(p, 10);
		Page<DonViCungCap> donViCungCapPage = donViCungCapRepository.findByTenContainingIgnoreCase(ten, pageRequest);

		List<Map<Object, Object>> mappedContent = donViCungCapPage.getContent().stream().map(donVi -> {
			Map<Object, Object> map = new HashMap<>();
			map.put("id", donVi.getId());
			map.put("ten", donVi.getTen());
			map.put("tongPhieuNhap", donViCungCapRepository.getSumPhieuNhap(ngaybd, ngaykt, donVi.getId()));
			map.put("tongSanPham", donViCungCapRepository.getSumProduct(ngaybd, ngaykt, donVi.getId()));
			return map;
		}).collect(Collectors.toList());

		return new PageImpl<>(mappedContent, pageRequest, donViCungCapPage.getTotalElements());
	}

	public Page<SanPhamPhieuNhap> getSanPhamCungCap(LocalDateTime nbd, LocalDateTime nkt, long id, int trang) {
	    ChiTietPhieuNhapService chiTietPhieuNhapService = ServiceLocator.getBean(ChiTietPhieuNhapService.class);
	    BienTheSerVice bienTheSerVice = ServiceLocator.getBean(BienTheSerVice.class);
	    Page<Object[]> danhSac = chiTietPhieuNhapService.getBienTheInChiTietPhieuNhap(nbd, nkt, id, PageRequest.of(trang, 10));
	    List<Object[]> danhSach=danhSac.getContent();
	    Map<Long, SanPhamPhieuNhap> sanPhamPhieuNhap = new HashMap<>();
	    SanPham s;
	    BienThe b;
	    SanPhamPhieuNhap sp;

	    for (int i = 0; i < danhSach.size(); i++) {
	        b = (BienThe) danhSach.get(i)[0];
	        s = b.getSanPham();
	        if (sanPhamPhieuNhap.containsKey(s.getId())) {
	            sp = sanPhamPhieuNhap.get(s.getId());
	        } else {
	            sp = new SanPhamPhieuNhap();
	            sp.setAnhSanPham(s.getAnhBia());
	            sp.setIdSanPham(s.getId());
	            sp.setTenSanPham(s.getTen());
	            sanPhamPhieuNhap.put(s.getId(), sp);
	        }
	        sp.addBienThe(b.getId(), b.getTen(), b.getAnhBia());
	        sp.addLanNhap(b.getId(), (Integer) danhSach.get(i)[2], (Float) danhSach.get(i)[3],
	                (LocalDateTime) danhSach.get(i)[1], (Long) danhSach.get(i)[4], "");
	    }

	    return new PageImpl<>(sanPhamPhieuNhap.values().stream().toList(), PageRequest.of(trang, 10), sanPhamPhieuNhap.size());
	}
	public Map<String, Object> getInfo(long id) {
	    DonViCungCap donViCungCap = getById(id);
	    Map<String, Object> map = new HashMap<>();

	    // Điền các thông tin cơ bản
	    map.put("name", donViCungCap.getTen());
	    map.put("soDienThoai", donViCungCap.getSoDienThoai());
	    map.put("maSoThue", donViCungCap.getMaSoThue());
	    map.put("soTaiKhoan", donViCungCap.getSoTaiKhoan());
	    map.put("code",donViCungCap.getId());
	    System.out.println(donViCungCap.getDiaChi());
	    // Xử lý địa chỉ chi tiết
	    String fullAddress = donViCungCap.getDiaChi();
	    String detailedAddress = "";
	    
	    if (fullAddress != null && !fullAddress.isEmpty()) {
	        // Tách phần sau dấu phẩy cuối cùng
	        int lastCommaIndex = fullAddress.lastIndexOf(",");
	        if (lastCommaIndex != -1) {
	            detailedAddress = fullAddress.substring(lastCommaIndex + 1);
	        } else {
	            detailedAddress = fullAddress;
	        }
	        
	        // Loại bỏ phần từ dấu chấm trở về sau
	        int dotIndex = detailedAddress.indexOf(".");
	        if (dotIndex != -1) {
	            detailedAddress = detailedAddress.substring(0, dotIndex).trim();
	        } else {
	            detailedAddress = detailedAddress.trim();
	        }
	        
	        map.put("diaChi", detailedAddress);
	    } else {
	        map.put("diaChi", "");
	    }

	    // Tách chuỗi mã
	    String diaChi = donViCungCap.getDiaChi();
	    int viTriChamCuoi = diaChi.lastIndexOf(".");
	    String tach = diaChi.substring(viTriChamCuoi + 1);
	    String[] mang = tach.trim().split("\\s+");


	    Map<String, Object> tinh = new HashMap<>();
	    Map<String, Object> huyen = new HashMap<>();
	    Map<String, Object> xa = new HashMap<>();

	    // Tách mã tỉnh, huyện, xã
	   
	       

	        // Gán mã và tên
	        tinh.put("code", Integer.parseInt(mang[0]));
	        tinh.put("name", "Cần Thơ");

	        huyen.put("code", Integer.parseInt(mang[1]));
	        huyen.put("name", "Phong Điền");

	        xa.put("code", Integer.parseInt(mang[2]));
	        xa.put("name", "Trường Long");
	    

	    // Thêm thông tin tỉnh, huyện, xã vào map
	    map.put("tinh", tinh);
	    map.put("huyen", huyen);
	    map.put("xa", xa);

	    return map;
	}
}
