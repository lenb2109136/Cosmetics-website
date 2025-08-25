package com.example.e_commerce.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.e_commerce.Exception.Custom.GeneralException;
import com.example.e_commerce.model.DanhMuc;
import com.example.e_commerce.model.QuocGia;
import com.example.e_commerce.model.ThuongHieu;
import com.example.e_commerce.repository.QuocGiaRepository;
import com.example.e_commerce.repository.ThuongHieuRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ThuongHieuService {
	@Autowired
	private ThuongHieuRepository thuongHieuRepository;
	
	@Autowired
	private CloudinaryService cloudinaryService;
	
	@Autowired
	private QuocGiaRepository quocGiaRepository;
	
	public List<ThuongHieu> getThuongHieu(){
		return thuongHieuRepository.findAll();
	}
	
	
	
	public ThuongHieu getById(long id) {
		return thuongHieuRepository.findById(id).orElseThrow(()-> new GeneralException("Không tìm thấy thương hiệu",HttpStatus.BAD_REQUEST));
	}
	
	public List<ThuongHieu> getThuongHieuBySanPhamAndDanhMuc(String tenSanPham, int idDanhMuc){
		DanhMucSerVice danhMucSerVice= ServiceLocator.getBean(DanhMucSerVice.class);
		List<DanhMuc> danhMucs= new ArrayList<DanhMuc>();
		if(idDanhMuc!=0) {
			DanhMuc dd= danhMucSerVice.getDanhMucById(idDanhMuc);
			danhMucSerVice.getAllChildDanhMuc(danhMucs, dd);
		}
		else {
			danhMucs=danhMucSerVice.getAll();
		}
		
		
		return thuongHieuRepository.getThuongHieuBySanPhamAndDanhMuc(tenSanPham, danhMucs.stream().map(f->f.getId())
				.distinct().collect(Collectors.toList()));
	}
	
	public Page<Map<String, Object>> getManager(String tenThuongHieu, int page) {
	    // Create a Pageable object for pagination with fixed size of 7
	    Pageable pageable = PageRequest.of(page, 10);

	    // Fetch paginated results
	    Page<ThuongHieu> thuongHieuPage = thuongHieuRepository.findByTenContainingIgnoreCase(tenThuongHieu, pageable);

	    // Map ThuongHieu to Map<String, Object>
	    List<Map<String, Object>> content = thuongHieuPage.getContent().stream().map(d -> {
	        Map<String, Object> r = new HashMap<>();
	        r.put("id", d.getId());
	        r.put("ten", d.getTen());
	        r.put("anhBia", d.getAnhBia());
	        r.put("anhDaiDien", d.getAnhDaiDien());
	        r.put("anhNen", d.getAnhNen());
	        r.put("tongSanPha", d.getSanPham().size());
	        return r;
	    }).collect(Collectors.toList());

	    // Return a Page object with the mapped content
	    return new PageImpl<>(content, pageable, thuongHieuPage.getTotalElements());
	}
	public List<ThuongHieu> getAll(String ten){
		System.out.println(ten);
		if(ten.length()==0) {
			List<ThuongHieu> l=thuongHieuRepository.findAll();
			System.out.println(l.size());
			return l;
		}
		else {
			List<ThuongHieu> g=thuongHieuRepository.getByChar(ten);
			System.out.println(g.size());
			return g;
		}
		
	}
	
	@Transactional
	public void save(String ten, MultipartFile anhNen, MultipartFile anhDaiDien, MultipartFile anhBia) {
		ThuongHieu thuongHieu = new ThuongHieu();
	    if (ten == null && ten.trim().isEmpty()) {
	        thuongHieu.setTen(ten.trim());
	        throw new GeneralException("Vui lòng cung cấp tên cho thương hiệu",HttpStatus.BAD_REQUEST);
	    }
	    thuongHieu.setTen(ten);
	    
	    if ((anhBia == null || anhBia.isEmpty()) 
	    	    || (anhNen == null || anhNen.isEmpty()) 
	    	    || (anhDaiDien == null || anhDaiDien.isEmpty())) {
	    	    throw new GeneralException("Vui lòng cung cấp đầy đủ ảnh cho thương hiệu", HttpStatus.BAD_REQUEST);
	    	}

	    try {
	        // Xử lý ảnh bìa
	        if (anhBia != null && !anhBia.isEmpty()) {
	            validateImageFile(anhBia); // Kiểm tra định dạng file
	            String anhBiaUrl = cloudinaryService.uploadImage(anhBia);
	            thuongHieu.setAnhBia(anhBiaUrl);
	        }

	        // Xử lý ảnh nền
	        if (anhNen != null && !anhNen.isEmpty()) {
	            validateImageFile(anhNen); // Kiểm tra định dạng file
	            String anhNenUrl = cloudinaryService.uploadImage(anhNen);
	            thuongHieu.setAnhNen(anhNenUrl);
	        }

	        // Xử lý ảnh đại diện
	        if (anhDaiDien != null && !anhDaiDien.isEmpty()) {
	            validateImageFile(anhDaiDien); // Kiểm tra định dạng file
	            String anhDaiDienUrl = cloudinaryService.uploadImage(anhDaiDien);
	            thuongHieu.setAnhDaiDien(anhDaiDienUrl);
	        }
	        QuocGia q= quocGiaRepository.findById((long)1).orElseThrow(()-> new GeneralException("Có lỗi xảy ra", HttpStatus.INTERNAL_SERVER_ERROR));
	        // Lưu đối tượng ThuongHieu
	        thuongHieu.setQuocGia(q);
	        thuongHieuRepository.save(thuongHieu);
	    } catch (Exception e) {
	        throw new RuntimeException("Lỗi khi cập nhật Thương Hiệu: " + e.getMessage(), e);
	    }
	}
	
	@Transactional
	public void update(String ten, MultipartFile anhNen, MultipartFile anhDaiDien, MultipartFile anhBia, long id) {
	    // Kiểm tra xem ThuongHieu có tồn tại không
	    ThuongHieu thuongHieu = getById(id);
	    if (thuongHieu == null) {
	        throw new IllegalArgumentException("Không tìm thấy Thương Hiệu với id: " + id);
	    }

	    // Cập nhật tên nếu hợp lệ
	    if (ten != null && !ten.trim().isEmpty()) {
	        thuongHieu.setTen(ten.trim());
	    }

	    try {
	        // Xử lý ảnh bìa
	        if (anhBia != null && !anhBia.isEmpty()) {
	            validateImageFile(anhBia); // Kiểm tra định dạng file
	            String anhBiaUrl = cloudinaryService.uploadImage(anhBia);
	            thuongHieu.setAnhBia(anhBiaUrl);
	        }

	        // Xử lý ảnh nền
	        if (anhNen != null && !anhNen.isEmpty()) {
	            validateImageFile(anhNen); // Kiểm tra định dạng file
	            String anhNenUrl = cloudinaryService.uploadImage(anhNen);
	            thuongHieu.setAnhNen(anhNenUrl);
	        }

	        // Xử lý ảnh đại diện
	        if (anhDaiDien != null && !anhDaiDien.isEmpty()) {
	            validateImageFile(anhDaiDien); // Kiểm tra định dạng file
	            String anhDaiDienUrl = cloudinaryService.uploadImage(anhDaiDien);
	            thuongHieu.setAnhDaiDien(anhDaiDienUrl);
	        }
	        QuocGia q= new QuocGia();
	        q.setId((long)1);
	        thuongHieu.setQuocGia(q);
	        // Lưu đối tượng ThuongHieu
	        thuongHieuRepository.save(thuongHieu);
	    } catch (Exception e) {
	        throw new RuntimeException("Lỗi khi cập nhật Thương Hiệu: " + e.getMessage(), e);
	    }
	}

	// Hàm kiểm tra định dạng file ảnh
	private void validateImageFile(MultipartFile file) {
	    String contentType = file.getContentType();
	    if (contentType == null || !contentType.startsWith("image/")) {
	        throw new IllegalArgumentException("File không phải là ảnh: " + file.getOriginalFilename());
	    }
	}
	
}
