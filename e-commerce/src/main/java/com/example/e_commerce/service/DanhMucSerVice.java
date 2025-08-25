package com.example.e_commerce.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.e_commerce.DTO.request.DanhMucCreate;
import com.example.e_commerce.DTO.request.DanhMucManager;
import com.example.e_commerce.DTO.response.ThongSoMangerDTO;
import com.example.e_commerce.Exception.Custom.GeneralException;
import com.example.e_commerce.mapper.DanhMucMapper;
import com.example.e_commerce.model.DanhMuc;
import com.example.e_commerce.model.SanPham;
import com.example.e_commerce.model.ThongSo;
import com.example.e_commerce.model.ThongSoCuThe;
import com.example.e_commerce.repository.DanhMucRepository;
import com.example.e_commerce.repository.SanPhamRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class DanhMucSerVice {
	@Autowired
	private DanhMucRepository danhMucRepository;

	@Autowired
	private ServiceLocator serviceLocator;

	@Autowired
	private SanPhamRepository sanPhamRepository;

	public List<DanhMuc> findAll() {
		return danhMucRepository.findAll();
	}

	public DanhMuc getDanhMucById(int id) {
		return danhMucRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Không tìm thấy danh mục cần tìm"));
	}

	public void getAllChildDanhMuc(List<DanhMuc> danhmuc, DanhMuc d) {
		danhmuc.add(d);
		for (int i = 0; i < d.getDanhSach().size(); i++) {
			getAllChildDanhMuc(danhmuc, d.getDanhSach().get(i));
		}
	}

	public long countProduct(DanhMuc danhmuc) {
		long tong = danhmuc.getSanPham().size();
		for (DanhMuc child : danhmuc.getDanhSach()) {
			tong += countProduct(child);
		}
		return tong;
	}

	public void CountProduct(DanhMuc danhmuc, List<SanPham> sanPhams) {

		sanPhams.addAll(danhmuc.getSanPham());
		for (int i = 0; i < danhmuc.getDanhSach().size(); i++) {
			CountProduct(danhmuc.getDanhSach().get(i), sanPhams);

		}
	}

	public List<DanhMuc> getAll() {
		return danhMucRepository.findAll();
	}

	@Transactional
	public void deleteDanhMucThongSoByDanhMuc(List<Integer> dm, long id) {
		ArrayList<Long> idd = new ArrayList<Long>();
		idd.add(id);
		if (dm.size() != 0) {
			danhMucRepository.deleteDanhMucThongSoByDanhMuc(dm, idd);
		}
	}

	@Transactional
	public void save(DanhMucCreate cap) {
		ThongSoService thongSoService = serviceLocator.getBean(ThongSoService.class);
		SanPhamService sanPhamService = serviceLocator.getBean(SanPhamService.class);
		// lấy danh mục từ danh mục c
		DanhMuc danhMuc = DanhMucMapper.getNewDanhMucFromDanhMucCreate(cap);
		DanhMuc danhMucParent;
		danhMucParent = cap.getPickCategory().size() != 0
				? getDanhMucById(cap.getPickCategory().get(cap.getPickCategory().size() - 1).getId())
				: null;
		Set<Long> thongsocha = danhMucParent.getThongSo().stream().map(d -> d.getId()).collect(Collectors.toSet());
		if (cap.getPickCategory().size() != 0 && danhMucParent.getSanPham().size() != 0) {
			List<Long> kiemtra = cap.getThongSoChon().stream().filter(d -> thongsocha.contains(d)).distinct()
					.collect(Collectors.toList());
			if (kiemtra.size() != cap.getThongSoChon().size()) {
				throw new GeneralException("Thông tin thông số không hợp lệ", HttpStatus.BAD_REQUEST);
			}
			Set<ThongSo> thongSoChon = danhMucParent.getThongSo().stream()
					.filter(d -> cap.getThongSoChon().contains(d.getId())).collect(Collectors.toSet());

			// thông tin thông số
			danhMuc.setThongSo(thongSoChon);
			// thông tin danh mục cha
//			danhMuc.setDanhMucCha(danhMucParent);
			danhMucRepository.save(danhMuc);
			// set các sản phẩm
			if (cap.getTrunggian() == null || cap.getTrunggian().getTen().equals("")) {
				// lấy danh sách các id thông số mà cái mới có cái cũ không có
				List<Long> dsloaibo = thongsocha.stream().filter(d -> !cap.getThongSoChon().contains(d))
						.collect(Collectors.toList());
				// loại bỏ đi các thông số đó trước
				sanPhamRepository.deleteByThongSoIdsAndDanhMucIds(dsloaibo, Arrays.asList(danhMucParent.getId()));
				danhMucParent.getSanPham().forEach(data -> {
					data.setDanhMuc(danhMuc);
					sanPhamService.update(data);
				});
			} else {
				DanhMuc danhMucTrungGian = new DanhMuc();
				danhMucTrungGian.setTen(cap.getTrunggian().getTen());
				danhMucTrungGian.setDanhMucCha(danhMucParent);
				danhMucRepository.save(danhMucTrungGian);
				danhMucTrungGian.setThongSo(danhMucParent.getThongSo());
				danhMucParent.getSanPham().forEach(data -> {
					data.setDanhMuc(danhMucTrungGian);
					sanPhamService.update(data);
				});
				danhMucRepository.save(danhMucTrungGian);
			}
			// hoàn thành :
			danhMucRepository.save(danhMuc);
			danhMucRepository.save(danhMucParent);
		} else {
			List<ThongSo> dsthonso = thongSoService.findInList(cap.getThongSoChon());
			danhMuc.setThongSo(dsthonso.stream().collect(Collectors.toSet()));
			danhMuc.setDanhMucCha(danhMucParent);
			danhMucRepository.save(danhMuc);
		}
	}

	@Transactional
	public void update(DanhMucCreate c) {
		SanPhamService sanPhamService = serviceLocator.getBean(SanPhamService.class);
		ThongSoService thongSoService = serviceLocator.getBean(ThongSoService.class);
		DanhMuc dm = danhMucRepository.findById(c.getId())
				.orElseThrow(() -> new GeneralException("Không tìm thấy danh mục", HttpStatus.BAD_REQUEST));

		if (c.getPickCategory().size() == 0 && (dm.getDanhMucCha() != null && dm.getDanhMucCha().getId() != 0)) {
			if (dm.getTen().equals(c.getTen()) == false) {
				dm.setTen(c.getTen());
			}
			dm.setDanhMucCha(null);
			// kiểm tra danh sách thông số
			List<Long> thongSoCu = dm.getThongSo().stream().map(d -> d.getId()).collect(Collectors.toList());
			// lấy danh sách thêm
			List<Long> danhSachThem = c.getThongSoChon().stream().filter(d -> !thongSoCu.contains(d))
					.collect(Collectors.toList());
			// lưu những thông số cũ đó
			danhSachThem.forEach(d -> {
				ThongSo t = thongSoService.getThongSoById(d);
				dm.getThongSo().add(t);
			});
			// lấy danh sách xóa đi
			List<Long> danhSachXoa = dm.getThongSo().stream().filter(d -> !c.getThongSoChon().contains(d.getId()))
					.map(d -> d.getId()).collect(Collectors.toList());
			// lấy toàn bộ con và id của nó
			List<Integer> idConChau = dm.layTatCaIdDanhMucCon(dm);

			// xóa toàn bộ liên kết giữa danh mục và thông số
			danhMucRepository.deleteDanhMucThongSoByDanhMuc(idConChau, danhSachXoa);

			// xóa toàn bộ nhưngx liên kết sản phẩm liên kết với thông số cụ thể luôn
			sanPhamRepository.deleteByThongSoIdsAndDanhMucIds(danhSachXoa, idConChau);
			danhMucRepository.save(dm);
			return;
		}
		
//		if (c.getPickCategory().size() != 0 && (dm.getDanhMucCha() == null || dm.getDanhMucCha().getId() == 0)) {
//			System.out.println("đi vào 5");
//			DanhMuc dk = danhMucRepository.findById(c.getPickCategory().get(c.getPickCategory().size() - 1).getId())
//					.orElseThrow(() -> new EntityNotFoundException("Không tìm thấy danh mục"));
//			if (dm.getTen().equals(c.getTen()) == false) {
//				dm.setTen(c.getTen());
//			}
//			// kiểm tra xem danh mục mà nó chuyển đến có con là sản phẩm ko => có phải đích ko 
//			if(dk.getSanPham().size()!=0) {
//				if(c.getDonDanhMuc().getTen()==null||c.getDonDanhMuc().getTen().equals("")) {
//					throw new GeneralException("Vui lòng tên danh mục được chuyển đến",HttpStatus.BAD_REQUEST);
//				}
//				DanhMuc danhMucChuyenDen= new DanhMuc();
//				danhMucChuyenDen.setTen(c.getDonDanhMuc().getTen());
//				danhMucChuyenDen.setDanhMucCha(dk);
//				danhMucRepository.save(danhMucChuyenDen);
//				Set<ThongSo> thongSoMoi = new HashSet<>();
//				for (ThongSo ts : dk.getThongSo()) {
//				    thongSoMoi.add(ts);
//				}
//				danhMucChuyenDen.setThongSo(thongSoMoi);
//
//				dk.getSanPham().forEach(data -> {
//					data.setDanhMuc(danhMucChuyenDen);
//					sanPhamService.update(data);
//				});
//				danhMucRepository.save(danhMucChuyenDen);
//			}
//			
//			//kiểm tra danh sách thông số
//			List<Long> thongSoCu = dm.getThongSo().stream().map(d -> d.getId()).collect(Collectors.toList());
//			// lấy danh sách thêm
//			List<Long> danhSachThem = c.getThongSoChon().stream().filter(d -> !thongSoCu.contains(d))
//					.collect(Collectors.toList());
//			// lưu những thông số cũ đó
//			danhSachThem.forEach(d -> {
//				ThongSo t = thongSoService.getThongSoById(d);
//				dm.getThongSo().add(t);
//			});
//			// lấy danh sách xóa đi
//			List<Long> danhSachXoa = dm.getThongSo().stream().filter(d -> !c.getThongSoChon().contains(d.getId()))
//					.map(d -> d.getId()).collect(Collectors.toList());
//			// lấy toàn bộ con và id của nó
//			List<Integer> idConChau = dm.layTatCaIdDanhMucCon(dm);
//
//			// xóa toàn bộ liên kết giữa danh mục và thông số
//			danhMucRepository.deleteDanhMucThongSoByDanhMuc(idConChau, danhSachXoa);
//
//			// xóa toàn bộ nhưngx liên kết sản phẩm liên kết với thông số cụ thể luôn
//			sanPhamRepository.deleteByThongSoIdsAndDanhMucIds(danhSachXoa, idConChau);
//			if (idcon.contains(dk.getId())) {
//				System.out.println("đi đên 5");
//				if (c.getTrunggian().getTen() == null || c.getTrunggian().getTen().equals("")) {
//					throw new GeneralException("Vui lòng cung cấp tên cho danh mục thay thế", HttpStatus.BAD_REQUEST);
//				}
//				// lấy danh sách thông số mới
//				DanhMuc news = new DanhMuc();
//				news.setTen(c.getTrunggian().getTen());
//				news.setDanhMucCha(dm.getDanhMucCha());
//				news.setThongSo(new HashSet<>(dm.getThongSo()));
//				danhMucRepository.save(news);
//				dk.setDanhMucCha(news);
//				dm.setDanhMucCha(dk);
//				// set đến nhưunxg thông số bị loại bỏ 
//				List<Long> loaibo= dm.getThongSo().stream().filter(d->!c.getThongSoChon().contains(d.getId())).map(d->d.getId()).collect(Collectors.toList());
//				// loại bỏ đi thông tin danh mục thông số nếu có 
//				danhMucRepository.deleteDanhMucThongSoByDanhMuc(idcon,loaibo);
//				// loại bỏ đi thông tin sản phẩm của nó với các thông số trên 
//				sanPhamRepository.deleteByThongSoIdsAndDanhMucIds(loaibo, idcon);
//				
//				danhMucRepository.save(dk);
//				danhMucRepository.save(news);
//				danhMucRepository.save(dm);
//				return;
//			} 
//			
//			danhMucRepository.save(dm);
//			return;
//
//		}
		if (c.getPickCategory().size() != 0) {
			if (dm.getTen().equals(c.getTen()) == false) {
				dm.setTen(c.getTen());
			}
		    List<Integer> idcon = dm.layTatCaIdDanhMucCon(dm);
		    DanhMuc dk = danhMucRepository.findById(c.getPickCategory().get(c.getPickCategory().size() - 1).getId())
		            .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy danh mục"));
		    // Check if the target category has products (is a leaf node)
		    if((dm.getDanhMucCha()==null||dm.getDanhMucCha().getId()==0) || (dm.getDanhMucCha().getId()!=dk.getId() )) {
		    	if (dk.getSanPham().size() != 0) {
			        if (c.getDonDanhMuc().getTen() == null || c.getDonDanhMuc().getTen().equals("")) {
			            throw new GeneralException("Vui lòng nhập tên danh mục được chuyển đến", HttpStatus.BAD_REQUEST);
			        }
			        DanhMuc danhMucChuyenDen = new DanhMuc();
			        danhMucChuyenDen.setTen(c.getDonDanhMuc().getTen());
			        danhMucChuyenDen.setDanhMucCha(dk);
			        danhMucRepository.save(danhMucChuyenDen);

			        // Create a new HashSet for thongSo to avoid shared references
			        danhMucChuyenDen.setThongSo(new HashSet<>());
			        dk.getThongSo().forEach(thongSo -> {
			            // Ensure each ThongSo is either a new instance or properly managed
			            ThongSo newThongSo = thongSoService.getThongSoById(thongSo.getId()); // Fetch fresh instance if needed
			            danhMucChuyenDen.getThongSo().add(newThongSo);
			        });

			        dk.getSanPham().forEach(data -> {
			            data.setDanhMuc(danhMucChuyenDen);
			            sanPhamService.update(data);
			        });
			        danhMucRepository.save(danhMucChuyenDen);
			    }

			    if (idcon.contains(dk.getId())) {
			        if (c.getTrunggian().getTen() == null || c.getTrunggian().getTen().equals("")) {
			            throw new GeneralException("Vui lòng cung cấp tên cho danh mục thay thế", HttpStatus.BAD_REQUEST);
			        }

			        // Create a new DanhMuc with a new HashSet for thongSo
			        DanhMuc news = new DanhMuc();
			        news.setTen(c.getTrunggian().getTen());
			        news.setDanhMucCha(dm.getDanhMucCha());
			        news.setThongSo(new HashSet<>());
			        dm.getThongSo().forEach(thongSo -> {
			            // Fetch a fresh ThongSo instance to avoid shared references
			            ThongSo newThongSo = thongSoService.getThongSoById(thongSo.getId());
			            news.getThongSo().add(newThongSo);
			        });
			        dm.getDanhMucCon().forEach(d->{
			        	d.setDanhMucCha(news);
			        	danhMucRepository.save(d);
			        });
			        danhMucRepository.save(news);

			       
			        dm.setDanhMucCha(dk);

			        // Identify thongSo to remove
			        List<Long> loaibo = dm.getThongSo().stream()
			                .filter(d -> !c.getThongSoChon().contains(d.getId()))
			                .map(ThongSo::getId)
			                .collect(Collectors.toList());

			        // Remove DanhMuc-ThongSo and SanPham-ThongSo associations
			        danhMucRepository.deleteDanhMucThongSoByDanhMuc(idcon, loaibo);
			        sanPhamRepository.deleteByThongSoIdsAndDanhMucIds(loaibo, idcon);

			        danhMucRepository.save(dk);
			        danhMucRepository.save(news);
			        danhMucRepository.save(dm);
			        return;
			    } else {
			        dm.setDanhMucCha(dk);

			        // Get current thongSo IDs
			        List<Long> thongSoCu = dm.getThongSo().stream()
			                .map(ThongSo::getId)
			                .collect(Collectors.toList());

			        // Add new thongSo
			        List<Long> danhSachThem = c.getThongSoChon().stream()
			                .filter(d -> !thongSoCu.contains(d))
			                .collect(Collectors.toList());
			        danhSachThem.forEach(d -> {
			            ThongSo t = thongSoService.getThongSoById(d);
			            dm.getThongSo().add(t);
			        });

			        // Identify thongSo to remove
			        List<Long> danhSachXoa = dm.getThongSo().stream()
			                .filter(d -> !c.getThongSoChon().contains(d.getId()))
			                .map(ThongSo::getId)
			                .collect(Collectors.toList());

			        // Get all descendant category IDs
			        List<Integer> idConChau = dm.layTatCaIdDanhMucCon(dm);

			        // Remove DanhMuc-ThongSo and SanPham-ThongSo associations
			        danhMucRepository.deleteDanhMucThongSoByDanhMuc(idConChau, danhSachXoa);
			        sanPhamRepository.deleteByThongSoIdsAndDanhMucIds(danhSachXoa, idConChau);

			        danhMucRepository.save(dk);
			        danhMucRepository.save(dm);
			        return;
			    }
		    }
		    else {
		    	if (dm.getTen().equals(c.getTen()) == false) {
					dm.setTen(c.getTen());
				}
				// kiểm tra danh sách thông số
				List<Long> thongSoCu = dm.getThongSo().stream().map(d -> d.getId()).collect(Collectors.toList());
				// lấy danh sách thêm
				List<Long> danhSachThem = c.getThongSoChon().stream().filter(d -> !thongSoCu.contains(d))
						.collect(Collectors.toList());
				// lưu những thông số cũ đó
				danhSachThem.forEach(d -> {
					ThongSo t = thongSoService.getThongSoById(d);
					dm.getThongSo().add(t);
				});
				// lấy danh sách xóa đi
				List<Long> danhSachXoa = dm.getThongSo().stream().filter(d -> !c.getThongSoChon().contains(d.getId()))
						.map(d -> d.getId()).collect(Collectors.toList());
				// lấy toàn bộ con và id của nó
				List<Integer> idConChau = dm.layTatCaIdDanhMucCon(dm);

				// xóa toàn bộ liên kết giữa danh mục và thông số
				danhMucRepository.deleteDanhMucThongSoByDanhMuc(idConChau, danhSachXoa);

				// xóa toàn bộ nhưngx liên kết sản phẩm liên kết với thông số cụ thể luôn
				sanPhamRepository.deleteByThongSoIdsAndDanhMucIds(danhSachXoa, idConChau);
				danhMucRepository.save(dm);
		    }
		}
		if (dm.getDanhMucCha() != null && dm.getDanhMucCha().getId() != 0) {
			System.out.println("KAKA");
			if (dm.getTen().equals(c.getTen()) == false) {
				dm.setTen(c.getTen());
			}
			if (c.getPickCategory() != null && c.getPickCategory().size() != 0
					&& c.getPickCategory().get(c.getPickCategory().size() - 1).getId() == dm.getDanhMucCha().getId()) {

				if (dm.getTen().equals(c.getTen()) == false) {
					dm.setTen(c.getTen());
				}
				// kiểm tra danh sách thông số
				List<Long> thongSoCu = dm.getThongSo().stream().map(d -> d.getId()).collect(Collectors.toList());
				// lấy danh sách thêm
				List<Long> danhSachThem = c.getThongSoChon().stream().filter(d -> !thongSoCu.contains(d))
						.collect(Collectors.toList());
				// lưu những thông số cũ đó
				danhSachThem.forEach(d -> {
					ThongSo t = thongSoService.getThongSoById(d);
					dm.getThongSo().add(t);
				});
				// lấy danh sách xóa đi
				List<Long> danhSachXoa = dm.getThongSo().stream().filter(d -> !c.getThongSoChon().contains(d.getId()))
						.map(d -> d.getId()).collect(Collectors.toList());
				// lấy toàn bộ con và id của nó
				List<Integer> idConChau = dm.layTatCaIdDanhMucCon(dm);

				// xóa toàn bộ liên kết giữa danh mục và thông số
				danhMucRepository.deleteDanhMucThongSoByDanhMuc(idConChau, danhSachXoa);

				// xóa toàn bộ nhưngx liên kết sản phẩm liên kết với thông số cụ thể luôn
				sanPhamRepository.deleteByThongSoIdsAndDanhMucIds(danhSachXoa, idConChau);
				danhMucRepository.save(dm);
			}
		} else {
			if (c.getPickCategory() == null || c.getPickCategory().size() == 0) {
				System.out.println("đi vào 2");
				// lưu thông tin cơ bản của nó
				if (dm.getTen().equals(c.getTen()) == false) {
					dm.setTen(c.getTen());
				}
				// kiểm tra danh sách thông số
				List<Long> thongSoCu = dm.getThongSo().stream().map(d -> d.getId()).collect(Collectors.toList());
				// lấy danh sách thêm
				List<Long> danhSachThem = c.getThongSoChon().stream().filter(d -> !thongSoCu.contains(d))
						.collect(Collectors.toList());
				// lưu những thông số cũ đó
				danhSachThem.forEach(d -> {
					ThongSo t = thongSoService.getThongSoById(d);
					dm.getThongSo().add(t);
				});
				// lấy danh sách xóa đi
				List<Long> danhSachXoa = dm.getThongSo().stream().filter(d -> !c.getThongSoChon().contains(d.getId()))
						.map(d -> d.getId()).collect(Collectors.toList());
				// lấy toàn bộ con và id của nó
				List<Integer> idConChau = dm.layTatCaIdDanhMucCon(dm);

				// xóa toàn bộ liên kết giữa danh mục và thông số
				danhMucRepository.deleteDanhMucThongSoByDanhMuc(idConChau, danhSachXoa);

				// xóa toàn bộ nhưngx liên kết sản phẩm liên kết với thông số cụ thể luôn
				sanPhamRepository.deleteByThongSoIdsAndDanhMucIds(danhSachXoa, idConChau);
				danhMucRepository.save(dm);
			}
		}
	}

	@Transactional
	public Boolean test(int id) {
		DanhMuc dm = danhMucRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Không tìm thấy danh mục"));
		if (dm.getDanhMucCha() != null && dm.getSanPham().size() != 0) {
			return true;
		} else {
			return false;
		}
	}

	public Page<DanhMucManager> getDanhMucManager(int page, String thamso) {
		Pageable pageable = PageRequest.of(page, 5);
		Page<DanhMuc> danhMucPage = danhMucRepository.findByTenContainingIgnoreCase(thamso, pageable);

		Page<DanhMucManager> result = danhMucPage.map(data -> {
			long tong = countProduct(data);
			DanhMucManager m = new DanhMucManager();
			m.setId(data.getId());
			m.setTen(data.getTen());
			m.setTongDanhMucCon(data.getDanhSach().size());
			m.setTongSanPham(tong);
			m.setTongThongSo(data.getThongSo().size());
			return m;
		});

		return result;
	}

	public HashMap<String, String> getAllSanPhamInDanhMuc(int id) {
		DanhMuc danhMuc = danhMucRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Không tìm thấy danh mục cần tìm"));
		List<SanPham> sanPhams = new ArrayList<SanPham>();
		HashMap<String, String> has = new HashMap<String, String>();
		CountProduct(danhMuc, sanPhams);
		sanPhams.forEach(data -> {
			has.put(data.getAnhBia(), data.getTen());
		});

		return has;
	}

	public Set<ThongSo> getThongSoCuTheOfThongSo(int id) {
		DanhMuc danhMuc = danhMucRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Danh mục không tồn tại"));
		return danhMuc.getThongSo();
	}

	public void getDanhMucCha(DanhMuc danhMuc, List<DanhMuc> danhsach) {
		danhsach.add(danhMuc);
		if (danhMuc.getDanhMucCha() == null) {
			return;
		} else {
			getDanhMucCha(danhMuc.getDanhMucCha(), danhsach);
		}
	}

	public DanhMucCreate getUpdate(int id) {
		DanhMuc d = danhMucRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Không tìm thấy danh mục cần tìm"));
		return DanhMucMapper.danhMucMapperToDanhMucCreate(d);

	}
	
	public void Huy(int id, int danhMucThayThe) {
		DanhMuc d = danhMucRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Không tìm thấy danh mục cần tìm"));
		
		if(d.getSanPham().size()!=0) {
			DanhMuc da = danhMucRepository.findById(danhMucThayThe)
					.orElseThrow(() -> new EntityNotFoundException("Không tìm thấy danh mục thay thế"));
			
			List<ThongSoCuThe> thongSoCuTheCha = da.getThongSo().stream()
				    .flatMap(f -> f.getThongSoCuThe().stream())
				    .collect(Collectors.toList());
			d.getSanPham().forEach(m->{
				m.setDanhMuc(da);
				
				// SSLấy danh sách thông số cũ
				List<ThongSoCuThe> thongSoSanPham= m.getThongSoCuThe();
				m.setThongSoCuThe(thongSoSanPham.stream().filter(dg->thongSoCuTheCha.contains(dg)).collect(Collectors.toList()));
				// lấy những cái trùng 
				
				sanPhamRepository.save(m);
			});
			danhMucRepository.delete(d);
		}
		else {
			if(d.getDanhMucCha()!=null) {
				d.getDanhMucCon().forEach(m->{
					m.setDanhMucCha(d.getDanhMucCha());
					danhMucRepository.save(m);
				});
			}
			else {
				d.getDanhMucCon().forEach(m->{
					m.setDanhMucCha(null);
					danhMucRepository.save(m);
				});
			}
			danhMucRepository.delete(d);
		}
	}

}
