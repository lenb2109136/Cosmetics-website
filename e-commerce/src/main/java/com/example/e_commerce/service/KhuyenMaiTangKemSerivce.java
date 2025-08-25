package com.example.e_commerce.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.e_commerce.DTO.request.FlashSaledto.FlashSaleDTO;
import com.example.e_commerce.Exception.Custom.GeneralException;
import com.example.e_commerce.Validator.TimeValidator;
import com.example.e_commerce.mapper.FlashSaleMapper;
import com.example.e_commerce.model.BienThe;
import com.example.e_commerce.model.BienTheDealChinh;
import com.example.e_commerce.model.BienTheDealPhu;
import com.example.e_commerce.model.BienTheFlashSale;
import com.example.e_commerce.model.Deal;
import com.example.e_commerce.model.FlashSale;
import com.example.e_commerce.model.KhuyenMai;
import com.example.e_commerce.model.KhuyenMaiTangKem;
import com.example.e_commerce.model.SanPhamChinh;
import com.example.e_commerce.model.SanPhamPhu;
import com.example.e_commerce.repository.KhuyenMaiTangKemRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class KhuyenMaiTangKemSerivce {
	@Autowired
	private KhuyenMaiTangKemRepository khuyenMaiTangKemRepository;
	
	@Autowired
	private SanPhamChinhService sanPhamChinhService;
	
	@Autowired
	private SanPhamPhuSerice sanPhamPhuSerice;
	public List<KhuyenMaiTangKem>  getKhuyenMaiTangKemChinhOfBienThe(int id){
		return khuyenMaiTangKemRepository.getKhuyenMaiTangKemChinhOfBienThe(id);
	}
	public List<KhuyenMaiTangKem>  getKhuyenMaiTangKemPhuOfBienThe(int id){
		return khuyenMaiTangKemRepository.getKhuyenMaiTangKemPhuOfBienThe(id);
	}
	
	
	public void CheckDuplicateDealOfBienThe(KhuyenMaiTangKem deal, int idBienThe, String tenBienThe) {
		List<KhuyenMaiTangKem> khuyenMai = khuyenMaiTangKemRepository.getKhuyenMaiTangKemChinhOfBienThe(idBienThe);
		khuyenMai.addAll(khuyenMaiTangKemRepository.getKhuyenMaiTangKemPhuOfBienThe(idBienThe));
		System.out.println("tổng số truy vấn ra được"+khuyenMai.size());
		khuyenMai.stream().filter(d->d.getId()!=deal.getId()).forEach((d) -> {
		System.out.println(TimeValidator.isOverlap(deal.getThoiGianApDung(), deal.getThoiGianNgung(), d.getThoiGianApDung(),
					d.getThoiGianNgung()));	
			if (TimeValidator.isOverlap(deal.getThoiGianApDung(), deal.getThoiGianNgung(), d.getThoiGianApDung(),
					d.getThoiGianNgung())) {
				

				throw new GeneralException("Deal bị trùng thời gian với deal khác của biến thể: " + tenBienThe,
						HttpStatus.BAD_REQUEST);

			}

		});

	}
	@Transactional
	public void save(KhuyenMaiTangKem khuyenMaiTangKem) {
		if(khuyenMaiTangKem.getSoLuongGioiHan()<=0) {
			throw new GeneralException("Số lượt khuyến mãi không thể nhỏ hơn 0",HttpStatus.BAD_REQUEST);
		}
		khuyenMaiTangKemRepository.save(khuyenMaiTangKem);
	}
	@Transactional
	public void save(FlashSaleDTO flashSaleDTO) {
		KhuyenMaiTangKem khuyenMaiTangKem= FlashSaleMapper.getBonusFromDTO(flashSaleDTO);
		List<SanPhamChinh> sanPhamChinhs= FlashSaleMapper.getSanPhamChinhFromBonus(flashSaleDTO);
		List<SanPhamPhu> sanPhamPhus=FlashSaleMapper.getSanPhamPhuFromBonus(flashSaleDTO);
		if(sanPhamChinhs.size()<=0) {
			throw new GeneralException("Vui lòng cung cấp sản phẩm chính",HttpStatus.BAD_REQUEST);
		}
		if(sanPhamPhus.size()<=0) {
			throw new GeneralException("Vui lòng cung cấp sản phẩm phụ",HttpStatus.BAD_REQUEST);
		}
		save(khuyenMaiTangKem);
		BienTheSerVice bienTheSerVice= ServiceLocator.getBean(BienTheSerVice.class);
		sanPhamChinhs.stream().filter(s->s.getSoLuongTu()>0).forEach(data -> {
			System.out.println("id biến thể: "+data.getId().getBtId());
			BienThe bienThe = bienTheSerVice.getById(data.getId().getBtId());
			CheckDuplicateDealOfBienThe(khuyenMaiTangKem, bienThe.getId(), bienThe.getTen());
			data.setBienThe(bienThe);
			data.setKhuyenMaiTangKem(khuyenMaiTangKem);
			sanPhamChinhService.save(data);
		});
		sanPhamPhus.stream().filter(s->s.getSoLuongTang()>0).forEach(data -> {
			System.out.println("id biến thể: "+data.getId().getBtId());
			BienThe bienThe = bienTheSerVice.getById(data.getId().getBtId());
			List<KhuyenMaiTangKem> danhSachTrung= khuyenMaiTangKemRepository.getKhuyenMaiTangKemPhuOfBienThe(data.getId().getBtId());
			CheckDuplicateDealOfBienThe(khuyenMaiTangKem, bienThe.getId(), bienThe.getTen());
			data.setBienThe(bienThe);
			data.setKhuyenMaiTangKem(khuyenMaiTangKem);
			sanPhamPhuSerice.save(data);
		});
	}
	
	
	public KhuyenMaiTangKem  getById(long id) {
		return khuyenMaiTangKemRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("Khuyến mãi tặng kèm không tồn tại"));
	}
	public FlashSaleDTO mapToBonusDTOFromBonus(long idBonus) {
		KhuyenMaiTangKem khuyenMaiTangKem=getById(idBonus);
		return FlashSaleMapper.mapFromBonusToBonusDTO(khuyenMaiTangKem);
	}
	public void update(FlashSaleDTO dealDto, long id) {
		KhuyenMaiTangKem khuyenMaiTangKem = getById(id);
		List<SanPhamChinh> bienTheDealChinh = FlashSaleMapper.getSanPhamChinhFromBonusForUpdate(dealDto);
		if(dealDto.getSoLuongGioiHan()<khuyenMaiTangKem.getSoLuongDaDung()) {
			throw new GeneralException("Số lượng khuyến mãi không thể nhỏ hơn số lượng đã dùng",HttpStatus.BAD_REQUEST);
		}
		if (dealDto.getNgayBatDau().isBefore(LocalDateTime.now())) {
		    throw new GeneralException("Không thể chỉnh thời gian bắt đầu về trước ngày hiện tại", HttpStatus.BAD_REQUEST);
		}

		if (dealDto.getNgayKetThuc().isBefore(LocalDateTime.now())) {
		    throw new GeneralException("Không thể chỉnh thời gian kết thúc về trước ngày hiện tại", HttpStatus.BAD_REQUEST);
		}

		if (dealDto.getNgayKetThuc().isBefore(dealDto.getNgayBatDau())) {
		    throw new GeneralException("Ngày kết thúc không được nhỏ hơn ngày bắt đầu", HttpStatus.BAD_REQUEST);
		}

		khuyenMaiTangKem.setSoLuongGioiHan(dealDto.getSoLuongGioiHan());

		if (!khuyenMaiTangKem.getThoiGianApDung().equals(dealDto.getNgayBatDau())) {
		    if (khuyenMaiTangKem.getSoLuongDaDung() != 0) {
		        throw new GeneralException("Không thể chỉnh thời gian bắt đầu khi đã có lượt sử dụng", HttpStatus.BAD_REQUEST);
		    }
		    khuyenMaiTangKem.setThoiGianApDung(dealDto.getNgayBatDau());
		}
		save(khuyenMaiTangKem);

		BienTheSerVice bienTheSerVice=ServiceLocator.getBean(BienTheSerVice.class);
		khuyenMaiTangKem.setThoiGianNgung(dealDto.getNgayKetThuc());
		
		List<SanPhamPhu> bienTheDealPhu = FlashSaleMapper.getSanPhamPhuFromBonusForUpdate(dealDto);
		List<SanPhamChinh> bienTheDealChinhs = khuyenMaiTangKem.getSanPhamChinh();
		List<SanPhamPhu> bienTheDealPhus = khuyenMaiTangKem.getSanPhamPhu();
		List<Integer> idchinh = bienTheDealChinhs.stream().map(data -> {
			return data.getBienThe().getId();
		}).collect(Collectors.toList());
		List<Integer> idphu = bienTheDealPhus.stream().map(data -> {
			return data.getBienThe().getId();
		}).collect(Collectors.toList());
		// sử lý deal chính mới
		bienTheDealChinh.stream().filter(d->{
			if(d.getSoLuongTu()>0) {
				return  true;
			}
			else {
				return false;
			}
		}).filter(data -> {
			return !idchinh.contains(data.getId().getBtId());
		}).forEach(dd -> {
			
			BienThe bienThe = bienTheSerVice.getById(dd.getId().getBtId());
			CheckDuplicateDealOfBienThe(khuyenMaiTangKem, bienThe.getId(),bienThe.getTen());
			dd.setBienThe(bienThe);
			dd.setKhuyenMaiTangKem(khuyenMaiTangKem);
			sanPhamChinhService.save(dd);
		});
		// sử lý deal phụ mới
		bienTheDealPhu.stream().filter(d->{
			if(d.getSoLuongTang()>0) {
				return  true;
			}
			else {
				return false;
			}
		}).filter(data -> {
			return !idphu.contains(data.getId().getBtId());
		}). forEach(dd -> {

			BienThe bienThe = bienTheSerVice.getById(dd.getId().getBtId());
			CheckDuplicateDealOfBienThe(khuyenMaiTangKem,dd.getId().getBtId(), bienThe.getTen());
			dd.setBienThe(bienThe);
			dd.setKhuyenMaiTangKem(khuyenMaiTangKem);
			sanPhamPhuSerice.save(dd);
		});
		bienTheDealChinhs.forEach(data -> {
			Optional<SanPhamChinh> optionalDd = bienTheDealChinh.stream()
					.filter(d -> d.getId().getBtId() == data.getBienThe().getId()).findFirst();
			optionalDd.ifPresent(dd -> {
				if (dd.getSoLuongTu() <= 0) {
					data.setConSuDung(false);
					data.setSoLuongTu(0);
					sanPhamChinhService.save(data);
				} else {
					if(dd.isConSuDung()) {
		        		CheckDuplicateDealOfBienThe(khuyenMaiTangKem,data.getBienThe().getId(), data.getBienThe().getTen());
		        	}
					data.setSoLuongTu(dd.getSoLuongTu());
					data.setConSuDung(dd.isConSuDung());
					sanPhamChinhService.save(data);
				}
			});
		});

		// Xử lý biến thể deal phụ cũ
		bienTheDealPhus.forEach(data -> {

			
		    Optional<SanPhamPhu> optionalDd = bienTheDealPhu.stream()
		            .filter(d -> d.getId().getBtId() == data.getBienThe().getId())
		            .findFirst();
		    
		    optionalDd.ifPresent(dd -> {
		    	
		        if (dd.getSoLuongTang()<=0) {
		            data.setConSuDung(false);
		            data.setSoLuongTang(0);
		        } else {
		        	if(dd.isConSuDung()) {
		        		CheckDuplicateDealOfBienThe(khuyenMaiTangKem,data.getBienThe().getId(), data.getBienThe().getTen());
		        	}
		        	
		            data.setConSuDung(dd.isConSuDung());
		            data.setSoLuongTang(dd.getSoLuongTang());
		        }
		        sanPhamPhuSerice.save(data);
		    });
		});


	}
	public Page<Map<String, Object>> getByStatus(
			int trang, LocalDateTime bd, LocalDateTime kt, int status
			){
		Page<KhuyenMaiTangKem> flashSales;
		
		if(status==2) {
		flashSales=	khuyenMaiTangKemRepository.getFlashSaleBeforNow(bd, kt, PageRequest.of(trang, 5));
		}
		else if(status==1) {
			System.out.println("đi vào trong đây");
			flashSales=	khuyenMaiTangKemRepository.getFlashSaleInNow(bd, kt, PageRequest.of(trang, 5));
			}
		else if (status==3) {
			flashSales=	khuyenMaiTangKemRepository.getFlashSaleAfterNow(bd, kt, PageRequest.of(trang, 5));
			}
		else{
			flashSales=	khuyenMaiTangKemRepository.findAll(PageRequest.of(trang, 5));
			}
		System.out.println("số lượng phần tử trong danh sách :"+flashSales.getSize());
		return flashSales.map(data -> {
	        Map<String, Object> map = new HashMap<>();
	        map.put("thoiGianApDung", data.getThoiGianApDung());
	        map.put("thoiGianNgung", data.getThoiGianNgung());
	        map.put("conSuDung", data.isConSuDung());
	        map.put("id", data.getId());

	        long soLuongSanPhamChinh = data.getSanPhamChinh().stream()
	            .map(dat -> dat.getBienThe().getSanPham().getId())
	            .distinct()
	            .count();
	        long soLuongSanPhamPhu = data.getSanPhamPhu().stream()
		            .map(dat -> dat.getBienThe().getSanPham().getId())
		            .distinct()
		            .count();
	        map.put("soLuongDaDung", data.getSoLuongDaDung());
	        map.put("tongLuotToiDa", data.getSoLuongGioiHan());
	        map.put("sanPhamChinh", soLuongSanPhamChinh);
	        map.put("sanPhamPhu", soLuongSanPhamPhu);

	        return map;
	    });
		
	}
	public void setActiveBonus(long id,boolean active) {
		KhuyenMaiTangKem f=getById(id);
		if (f.getThoiGianNgung().isBefore(LocalDateTime.now())) {
		    throw new GeneralException("Không thể điều chỉnh do khuyến mãi đã hết hạn",HttpStatus.BAD_REQUEST);
		}
		if(active==true) {
			List<SanPhamChinh> sanPhamChinhs=f.getSanPhamChinh();
			List<SanPhamPhu> sanPhamPhus=f.getSanPhamPhu();
			
			sanPhamPhus.forEach(d->{
				CheckDuplicateDealOfBienThe(f, d.getBienThe().getId(),d.getBienThe().getTen());
			});
			sanPhamChinhs.forEach(d->{
				CheckDuplicateDealOfBienThe(f, d.getBienThe().getId(),d.getBienThe().getTen());
			});
		}
		f.setConSuDung(active);
		save(f);
	}
}
