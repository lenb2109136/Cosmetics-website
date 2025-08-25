package com.example.e_commerce.mapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.example.e_commerce.DTO.request.FlashSaledto.FlashSaleDTO;
import com.example.e_commerce.DTO.request.FlashSaledto.ItemFlash;
import com.example.e_commerce.DTO.request.FlashSaledto.SanPhamFlashSale;
import com.example.e_commerce.Exception.Custom.GeneralException;
import com.example.e_commerce.model.BienThe;
import com.example.e_commerce.model.BienTheDealChinh;
import com.example.e_commerce.model.BienTheDealPhu;
import com.example.e_commerce.model.BienTheFlashSale;
import com.example.e_commerce.model.Deal;
import com.example.e_commerce.model.FlashSale;
import com.example.e_commerce.model.KhuyenMaiTangKem;
import com.example.e_commerce.model.SanPham;
import com.example.e_commerce.model.SanPhamChinh;
import com.example.e_commerce.model.SanPhamPhu;
import com.example.e_commerce.model.embeded.EBienTheDealChinh;
import com.example.e_commerce.model.embeded.EBienTheDealPhu;
import com.example.e_commerce.model.embeded.EBienTheFlashSale;
import com.example.e_commerce.model.embeded.ESanPhamChinh;
import com.example.e_commerce.model.embeded.ESanPhamPhu;
import com.example.e_commerce.service.BienTheFlashSaleService;
import com.example.e_commerce.service.DealService;
import com.example.e_commerce.service.FlashSaleService;
import com.example.e_commerce.service.KhuyenMaiTangKemSerivce;
import com.example.e_commerce.service.SanPhamService;
import com.example.e_commerce.service.ServiceLocator;

public class FlashSaleMapper {
	public static FlashSale getFlashFromDTO(FlashSaleDTO falFlashSale) {
		FlashSale f = new FlashSale();
		f.setConSuDung(true);
		f.setGioBatDau(falFlashSale.getThoiGianBatDau());
		f.setGioKetThuc(falFlashSale.getThoiGianKetThuc());
		f.setThoiGianApDung(falFlashSale.getNgayBatDau());
		f.setThoiGianNgung(falFlashSale.getNgayKetThuc());
		return f;
	}
	
	public static KhuyenMaiTangKem getBonusFromDTO(FlashSaleDTO falFlashSale) {
		KhuyenMaiTangKem f = new KhuyenMaiTangKem();
		f.setConSuDung(true);
		f.setThoiGianApDung(falFlashSale.getNgayBatDau());
		f.setThoiGianNgung(falFlashSale.getNgayKetThuc());
		f.setSoLuongGioiHan(falFlashSale.getSoLuongGioiHan());
		return f;
	}
	public static List<SanPhamChinh> getSanPhamChinhFromBonus(FlashSaleDTO falFlashSale) {
		return falFlashSale.getData().stream().flatMap(m -> m.getBienThe().
				stream()).filter(l -> l.getSoLuongKhuyenMai() >0).map((d) -> {
			SanPhamChinh bienTheFlashSale = new SanPhamChinh();
			bienTheFlashSale.setConSuDung(true);
			bienTheFlashSale.setSoLuongTu(d.getSoLuongKhuyenMai());
			ESanPhamChinh e = new ESanPhamChinh();
			e.setBtId(d.getId());
			bienTheFlashSale.setId(e);
			return bienTheFlashSale;
		}).collect(Collectors.toList());
	}
	public static List<SanPhamPhu> getSanPhamPhuFromBonus(FlashSaleDTO falFlashSale) {
		return falFlashSale.getDataPhu().stream().flatMap(m -> m.getBienThe().stream()).filter(l -> l.getSoLuongKhuyenMai() >0).map((d) -> {
			SanPhamPhu bienTheFlashSale = new SanPhamPhu();
			bienTheFlashSale.setConSuDung(true);
			bienTheFlashSale.setSoLuongTang(d.getSoLuongKhuyenMai());
			ESanPhamPhu e = new ESanPhamPhu();
			e.setBtId(d.getId());
			bienTheFlashSale.setId(e);
			return bienTheFlashSale;
		}).collect(Collectors.toList());
	}
	public static List<SanPhamChinh> getSanPhamChinhFromBonusForUpdate(FlashSaleDTO falFlashSale) {
		return falFlashSale.getData().stream().flatMap(m -> m.getBienThe().
				stream()).map((d) -> {
			SanPhamChinh bienTheFlashSale = new SanPhamChinh();
			bienTheFlashSale.setConSuDung(true);
			bienTheFlashSale.setSoLuongTu(d.getSoLuongKhuyenMai());
			ESanPhamChinh e = new ESanPhamChinh();
			e.setBtId(d.getId());
			bienTheFlashSale.setId(e);
			return bienTheFlashSale;
		}).collect(Collectors.toList());
	}
	public static List<SanPhamPhu> getSanPhamPhuFromBonusForUpdate(FlashSaleDTO falFlashSale) {
		return falFlashSale.getDataPhu().stream().flatMap(m -> m.getBienThe().stream()).map((d) -> {
			SanPhamPhu bienTheFlashSale = new SanPhamPhu();
			bienTheFlashSale.setConSuDung(true);
			bienTheFlashSale.setSoLuongTang(d.getSoLuongKhuyenMai());
			ESanPhamPhu e = new ESanPhamPhu();
			e.setBtId(d.getId());
			bienTheFlashSale.setId(e);
			return bienTheFlashSale;
		}).collect(Collectors.toList());
	}
	public static List<BienTheFlashSale> getBienTheFlashFromDTO(FlashSaleDTO falFlashSale) {
		return falFlashSale.getData().stream().flatMap(m -> m.getBienThe().stream()).map((d) -> {
			BienTheFlashSale bienTheFlashSale = new BienTheFlashSale();
			bienTheFlashSale.setConSuDung(true);
			bienTheFlashSale.setGiaTriGiam(d.getGiaGiam());
			bienTheFlashSale.setLaPhanTram(true);
			bienTheFlashSale.setSoLuongGioiHan(d.getSoLuongKhuyenMai());
			bienTheFlashSale.setConSuDung(d.isConSuDung());
			EBienTheFlashSale e = new EBienTheFlashSale();
			e.setBtId(d.getId());
			bienTheFlashSale.setId(e);
			return bienTheFlashSale;
		}).collect(Collectors.toList());
	}

	public static FlashSaleDTO MapToFlashDTOFromFlash(FlashSale flash) {
		List<Long> idd= new ArrayList<Long>();
		idd.add(flash.getId());
		BienTheFlashSaleService bienTheFlashSaleService = ServiceLocator.getBean(BienTheFlashSaleService.class);
		FlashSaleDTO d = new FlashSaleDTO();
		d.setNgayBatDau(flash.getThoiGianApDung());
		d.setNgayKetThuc(flash.getThoiGianNgung());
		d.setThoiGianBatDau(flash.getGioBatDau());
		d.setThoiGianKetThuc(flash.getGioKetThuc());
		List<SanPhamFlashSale> data = new ArrayList<SanPhamFlashSale>();
		SanPhamService sanPhamService = ServiceLocator.getBean(SanPhamService.class);
		FlashSaleService flashSaleService = ServiceLocator.getBean(FlashSaleService.class);
		BienThe b;
		SanPham ss;
		List<SanPham> sanphamApDung = flash.getBienTheFlashSale().stream()
				.map(dy -> dy.getBienThe().getSanPham().getId()).distinct().map(id -> sanPhamService.getSanPhamById(id))
				.collect(Collectors.toList());
		System.out.println("SỐ LƯỢNG SẢN PHẨM FLASSHSALE: "+sanphamApDung.size());
		data= sanphamApDung.stream().map(de -> {
			SanPhamFlashSale ff = new SanPhamFlashSale();
			ff.setHinhAnh(de.getAnhBia());
			ff.setId(de.getId());
			ff.setTen(de.getTen());
			List<BienThe> udo=de.getAllBienTheNotCheckActive(true);
			List<BienThe> udo2=de.getOnlyNotDefault(1);
			List<ItemFlash>rt= udo.stream().map(dp -> {
				ItemFlash i = new ItemFlash();
				i.setTen(dp.getTen());
				i.setHinhAnh(dp.getAnhBia());
				i.setSoLuongKho(dp.getSoLuongKho());
				i.setId(dp.getId());
				i.setGia(dp.getGia());
				i.setSoLuongKhuyenMai(0);
				i.setGiaGiam(0);
				i.setNotUpdate(false);
				if(dp.isConSuDung()==false&&!dp.getTen().equals("Mặc định")) {
					i.setNotUpdate(true);
				}
				if(dp.getTen().equals("Mặc định")&&udo2.size()!=0){
					i.setNotUpdate(true);
				}
				BienTheFlashSale bf = flash.getBienTheFlashSale().stream()
						.filter(k -> dp.getId() == k.getId().getBtId()).findFirst().orElse(null);
				if (bf != null) {
					i.setGiaGiam(bf.getGiaTriGiam());
					i.setSoLuongKhuyenMai(bf.getSoLuongGioiHan());
					i.setSoLuongConLai(bf.getSoLuongDaDung());
					i.setConSuDung(bf.isConSuDung());
				}
				System.out.println(bf);
				if(bf!=null) {
					i.setDanhSachFlasSale(flashSaleService.getFlashActiveOfBienThe(bf.getBienThe(),idd));
				}
				else {
					i.setDanhSachFlasSale(new ArrayList<Map<String,Object>>());
				}
				
				
				return i;

			}).collect(Collectors.toList());
			ff.setBienThe(rt);
			return ff;
		}).collect(Collectors.toList());
		d.setData(data);
		return d;
	}
	public static Deal getDeal(FlashSaleDTO deal) {
		Deal d= new Deal();
		d.setConSuDung(true);
		d.setSoLuongDaDung(0);
		d.setSoLuongGioiHan(deal.getSoLuongGioiHan());
		d.setThoiGianApDung(deal.getNgayBatDau());
		d.setThoiGianNgung(deal.getNgayKetThuc());
		return d;
	}
	
	public static List<BienTheDealChinh> getDealChinh(FlashSaleDTO dealChinh) {
	    return dealChinh.getData().stream()
	        .flatMap(m -> m.getBienThe().stream()) 
	        .filter(l -> l.getSoLuongKhuyenMai() >0)
	        .map(l -> {
	            BienTheDealChinh bb = new BienTheDealChinh();
	            EBienTheDealChinh e = new EBienTheDealChinh();
	            e.setBtId(l.getId());
	            bb.setId(e);
	            bb.setConSuDung(true);
	            bb.setSoLuongTu(l.getSoLuongKhuyenMai());
	            return bb;
	        })
	        .collect(Collectors.toList()); 
	}
	public static List<BienTheDealChinh> getDealChinhForupdate(FlashSaleDTO dealChinh) {
	    return dealChinh.getData().stream()
	        .flatMap(m -> m.getBienThe().stream()) 
	        .map(l -> {
	            BienTheDealChinh bb = new BienTheDealChinh();
	            EBienTheDealChinh e = new EBienTheDealChinh();
	            e.setBtId(l.getId());
	            bb.setId(e);
	            bb.setConSuDung(l.isConSuDung());
	            bb.setSoLuongTu(l.getSoLuongKhuyenMai());
	            return bb;
	        })
	        .collect(Collectors.toList()); 
	}
	public static List<BienTheDealPhu> getDealPhu(FlashSaleDTO dealPhu) {
	    return dealPhu.getDataPhu().stream()
	        .flatMap(m -> m.getBienThe().stream()) 
	        .filter(l -> {
	        	if(l.getSoLuongKhuyenMai()>0&&l.getGiaGiam()>0) {
	        		System.out.println("đã true phần tử");
	        		return true;
	        	}
	        	else {
	        		return false;
	        	}
	        })
	        .map(l -> {
	            BienTheDealPhu bb = new BienTheDealPhu();
	            EBienTheDealPhu e = new EBienTheDealPhu();
	            e.setBtId(l.getId());
	            bb.setId(e);
	            bb.setConSuDung(true);
	            System.out.println("gía trị giảm: "+l.getGiaGiam());
	            bb.setGiaTriGiam(l.getGiaGiam());
	            bb.setLaPhanTram(true);
	            bb.setToiDaTrenDonVi(l.getSoLuongKhuyenMai());
	            return bb;
	        })
	        .collect(Collectors.toList()); 
	}
	public static List<BienTheDealPhu> getDealPhuforupdate(FlashSaleDTO dealPhu) {
	    return dealPhu.getDataPhu().stream()
	        .flatMap(m -> m.getBienThe().stream())
	        .map(l -> {
	            BienTheDealPhu bb = new BienTheDealPhu();
	            EBienTheDealPhu e = new EBienTheDealPhu();
	            e.setBtId(l.getId());
	            bb.setId(e);
	            bb.setConSuDung(l.isConSuDung());
	            bb.setGiaTriGiam(l.getGiaGiam());
	            bb.setLaPhanTram(l.isConSuDung());
	            bb.setToiDaTrenDonVi(l.getSoLuongKhuyenMai());
	            return bb;
	        })
	        .collect(Collectors.toList()); 
	}
	
	public static FlashSaleDTO mapFromBonusToBonusDTO(KhuyenMaiTangKem khuyenMaiTangKem) {
		KhuyenMaiTangKemSerivce khuyenMaiTangKemSerivce=ServiceLocator.getBean(KhuyenMaiTangKemSerivce.class);
		FlashSaleDTO flashSaleDTO= new FlashSaleDTO();
		flashSaleDTO.setNgayBatDau(khuyenMaiTangKem.getThoiGianApDung());
		flashSaleDTO.setNgayKetThuc(khuyenMaiTangKem.getThoiGianNgung());
		flashSaleDTO.setSoLuongGioiHan(khuyenMaiTangKem.getSoLuongGioiHan());
		flashSaleDTO.setSoLuongDaDung(khuyenMaiTangKem.getSoLuongDaDung());
		List<SanPham> spDealChinh=khuyenMaiTangKem.getSanPhamChinh().stream().map(d->{
			return d.getBienThe().getSanPham();
		}).distinct().collect(Collectors.toList());
		List<SanPham> spDealPhu=khuyenMaiTangKem.getSanPhamPhu().stream().map(d->{
			return d.getBienThe().getSanPham();
		}).distinct().collect(Collectors.toList());
		//
		
		List<SanPhamFlashSale> sanPhamFlashSalesChinh = spDealChinh.stream().map(data -> {
		    SanPhamFlashSale sanPhamFlashSale = new SanPhamFlashSale();
		    sanPhamFlashSale.setHinhAnh(data.getAnhBia());
		    sanPhamFlashSale.setId(data.getId());
		    sanPhamFlashSale.setTen(data.getTen());

		    
		    List<BienThe> udo=data.getAllBienTheNotCheckActive(true);
			List<BienThe> udo2=data.getOnlyNotDefault(1);
			
		    List<ItemFlash> itemFlashList = udo.stream().map(dat -> {
		        Optional<SanPhamChinh> optional = khuyenMaiTangKem.getSanPhamChinh().stream()
		            .filter(df -> df.getBienThe().getId() == dat.getId())
		            .findFirst();

		        ItemFlash itemFlash = new ItemFlash();
		        itemFlash.setGia(dat.getGia());
		        itemFlash.setHinhAnh(dat.getAnhBia());
		        itemFlash.setId(dat.getId());
		        itemFlash.setSoLuongKho(dat.getSoLuongKho());
		        itemFlash.setTen(dat.getTen());
		        itemFlash.setNotUpdate(false);
		        if(dat.isConSuDung()==false&&!dat.getTen().equals("Mặc định")) {
						itemFlash.setNotUpdate(true);
					}
					if(dat.getTen().equals("Mặc định")&&udo2.size()!=0){
						itemFlash.setNotUpdate(true);
					}

		        if (optional.isPresent()) {
		            SanPhamChinh sanPhamChinh = optional.get();
		            itemFlash.setConSuDung(sanPhamChinh.isConSuDung());
		            itemFlash.setSoLuongKhuyenMai((int) sanPhamChinh.getSoLuongTu());
		            itemFlash.setReady(true);
		        }
		        List<Map<String, Object>> bienTheDealChinh=khuyenMaiTangKemSerivce.getKhuyenMaiTangKemChinhOfBienThe(dat.getId()).stream()
		        		.filter(d->d.getId()!=khuyenMaiTangKem.getId())
		                .map(di -> {
		                        Map<String, Object> mi = new HashMap<>();
		                        mi.put("thoiGianChay", di.getThoiGianApDung());
		                        mi.put("thoiGianNgung", di.getThoiGianNgung());
		                        mi.put("soLuotGioiHan", di.getSoLuongGioiHan());
		                        mi.put("soLuongDaDung", di.getSoLuongDaDung());
		                        return mi;
		                })
		                .filter(Objects::nonNull)
		                .collect(Collectors.toList());
		            	List<Map<String, Object>> bienTheDealPhu=khuyenMaiTangKemSerivce.getKhuyenMaiTangKemPhuOfBienThe(dat.getId()).stream()
		            			.filter(d->d.getId()!=khuyenMaiTangKem.getId())
		    	                .map(di -> {
		    	                        Map<String, Object> mi = new HashMap<>();
		    	                        mi.put("thoiGianChay", di.getThoiGianApDung());
		    	                        mi.put("thoiGianNgung", di.getThoiGianNgung());
		    	                        mi.put("soLuotGioiHan", di.getSoLuongGioiHan());
		    	                        mi.put("soLuongDaDung", di.getSoLuongDaDung());
		    	                        return mi;
		    	                })
		    	                .filter(Objects::nonNull)
		    	                .collect(Collectors.toList());
		            	itemFlash.setDealChinh(bienTheDealChinh);
		            	itemFlash.setDealPhu(bienTheDealPhu);

		        return itemFlash;
		    }).collect(Collectors.toList());

		    sanPhamFlashSale.setBienThe(itemFlashList);
		    return sanPhamFlashSale;
		}).collect(Collectors.toList());
		List<SanPhamFlashSale> sanPhamFlashSalesPhu = spDealPhu.stream().map(data -> {
		    SanPhamFlashSale sanPhamFlashSale = new SanPhamFlashSale();
		    sanPhamFlashSale.setHinhAnh(data.getAnhBia());
		    sanPhamFlashSale.setId(data.getId());
		    sanPhamFlashSale.setTen(data.getTen());
		    
		    
		    List<BienThe> udo=data.getAllBienTheNotCheckActive(true);
			List<BienThe> udo2=data.getOnlyNotDefault(1);
		    List<ItemFlash> itemFlashList = udo.stream().map(dat -> {
		        Optional<SanPhamPhu> optional = khuyenMaiTangKem.getSanPhamPhu().stream()
		            .filter(df -> df.getBienThe().getId() == dat.getId())
		            .findFirst();

		        ItemFlash itemFlash = new ItemFlash();
		        itemFlash.setGia(dat.getGia());
		        itemFlash.setHinhAnh(dat.getAnhBia());
		        itemFlash.setId(dat.getId());
		        itemFlash.setSoLuongKho(dat.getSoLuongKho());
		        itemFlash.setTen(dat.getTen());
		        itemFlash.setNotUpdate(false);
		        if(dat.isConSuDung()==false&&!dat.getTen().equals("Mặc định")) {
						itemFlash.setNotUpdate(true);
					}
					if(dat.getTen().equals("Mặc định")&&udo2.size()!=0){
						itemFlash.setNotUpdate(true);
					}
		        if (optional.isPresent()) {
		            SanPhamPhu bienTheDealPhu = optional.get();
		            itemFlash.setConSuDung(bienTheDealPhu.isConSuDung());
		            itemFlash.setSoLuongKhuyenMai((int)bienTheDealPhu.getSoLuongTang());
		            itemFlash.setReady(true);
		        }

		        return itemFlash;
		    }).collect(Collectors.toList());

		    sanPhamFlashSale.setBienThe(itemFlashList);
		    
		    return sanPhamFlashSale;
		}).collect(Collectors.toList());
		flashSaleDTO.setData(sanPhamFlashSalesChinh);
		flashSaleDTO.setDataPhu(sanPhamFlashSalesPhu);
		flashSaleDTO.setSoLuongDaDung(khuyenMaiTangKem.getSoLuongDaDung());
		return flashSaleDTO;
	}
	
	public static FlashSaleDTO mapDealFromDealToDto(Deal deal) {
		SanPhamService sanPhamService=ServiceLocator.getBean(SanPhamService.class);
		DealService dealService= ServiceLocator.getBean(DealService.class);
		FlashSaleDTO flashSaleDTO= new FlashSaleDTO();
		// thông tin cơ bản trước 
		flashSaleDTO.setNgayBatDau(deal.getThoiGianApDung());
		flashSaleDTO.setNgayKetThuc(deal.getThoiGianNgung());
		flashSaleDTO.setSoLuongGioiHan(deal.getSoLuongGioiHan());
		flashSaleDTO.setSoLuongGioiHan(deal.getSoLuongGioiHan());
		flashSaleDTO.setTenChuongTrinh(deal.getTenChuongTrinh());
		if(deal.getThoiGianNgung().isBefore(LocalDateTime.now())) {
			flashSaleDTO.setCanUpdate(false);
		}
		List<SanPham> spDealChinh=deal.getBienTheDealChinh().stream().map(d->{
			return d.getBienThe().getSanPham();
		}).distinct().collect(Collectors.toList());
		List<SanPham> spDealPhu=deal.getBienTheDealPhu().stream().map(d->{
			return d.getBienThe().getSanPham();
		}).distinct().collect(Collectors.toList());
		List<SanPhamFlashSale> sanPhamFlashSalesChinh = spDealChinh.stream().map(data -> {
		    SanPhamFlashSale sanPhamFlashSale = new SanPhamFlashSale();
		    sanPhamFlashSale.setHinhAnh(data.getAnhBia());
		    sanPhamFlashSale.setId(data.getId());
		    sanPhamFlashSale.setTen(data.getTen());
		    List<BienThe> udo=data.getAllBienTheNotCheckActive(true);
			List<BienThe> udo2=data.getOnlyNotDefault(1);
		    List<ItemFlash> itemFlashList = udo.stream().map(dat -> {
		        Optional<BienTheDealChinh> optional = deal.getBienTheDealChinh().stream()
		            .filter(df -> df.getBienThe().getId() == dat.getId())
		            .findFirst();

		        ItemFlash itemFlash = new ItemFlash();
		        itemFlash.setGia(dat.getGia());
		        itemFlash.setHinhAnh(dat.getAnhBia());
		        itemFlash.setId(dat.getId());
		        itemFlash.setSoLuongKho(dat.getSoLuongKho());
		        itemFlash.setTen(dat.getTen());
		        itemFlash.setNotUpdate(false);
		        if(dat.isConSuDung()==false&&!dat.getTen().equals("Mặc định")) {
						itemFlash.setNotUpdate(true);
					}
					if(dat.getTen().equals("Mặc định")&&udo2.size()!=0){
						itemFlash.setNotUpdate(true);
					}
		        if (optional.isPresent()) {
		            BienTheDealChinh bienTheDealChinh = optional.get();
		            itemFlash.setConSuDung(bienTheDealChinh.isConSuDung());
		            itemFlash.setSoLuongKhuyenMai(bienTheDealChinh.getSoLuongTu());
		            itemFlash.setReady(true);
		            System.out.println("có trong danh sách deal");
		        }
		        else {
		        	System.out.println("Không có trong danh sách deal");
		        }
		        List<Map<String, Object>> bienTheDealChinh=dealService.getDealChinhOfBienThe(dat.getId()).stream()
		        		.filter(d->d.getId()!=deal.getId())
		                .map(di -> {
		                        Map<String, Object> mi = new HashMap<>();
		                        mi.put("thoiGianChay", di.getThoiGianApDung());
		                        mi.put("thoiGianNgung", di.getThoiGianNgung());
		                        mi.put("soLuotGioiHan", di.getSoLuongGioiHan());
		                        mi.put("soLuongDaDung", di.getSoLuongDaDung());
		                        return mi;
		                })
		                .filter(Objects::nonNull)
		                .collect(Collectors.toList());
		            	List<Map<String, Object>> bienTheDealPhu=dealService.getDealPhuOfBienThe(dat.getId()).stream()
		            			.filter(d->d.getId()!=deal.getId())
		    	                .map(di -> {
		    	                        Map<String, Object> mi = new HashMap<>();
		    	                        mi.put("thoiGianChay", di.getThoiGianApDung());
		    	                        mi.put("thoiGianNgung", di.getThoiGianNgung());
		    	                        mi.put("soLuotGioiHan", di.getSoLuongGioiHan());
		    	                        mi.put("soLuongDaDung", di.getSoLuongDaDung());
		    	                        return mi;
		    	                })
		    	                .filter(Objects::nonNull)
		    	                .collect(Collectors.toList());
		            	itemFlash.setDealChinh(bienTheDealChinh);
		            	itemFlash.setDealPhu(bienTheDealPhu);

		        return itemFlash;
		    }).collect(Collectors.toList());

		    sanPhamFlashSale.setBienThe(itemFlashList);
		    return sanPhamFlashSale;
		}).collect(Collectors.toList());
		List<Integer> bienTheDealChins= deal.getBienTheDealChinh().stream().map(d->d.getBienThe().getId()).collect(Collectors.toList());
		List<Integer> bienTheDealPhus= deal.getBienTheDealPhu().stream().map(d->d.getBienThe().getId()).collect(Collectors.toList());
		List<SanPhamFlashSale> sanPhamFlashSalesPhu = spDealPhu.stream().map(data -> {
		    SanPhamFlashSale sanPhamFlashSale = new SanPhamFlashSale();
		    sanPhamFlashSale.setHinhAnh(data.getAnhBia());
		    sanPhamFlashSale.setId(data.getId());
		    sanPhamFlashSale.setTen(data.getTen());
		    List<BienThe> udo=data.getBienTheOfConditionCheckActive(true);
		    List<ItemFlash> itemFlashList = udo.stream().map(dat -> {
		        Optional<BienTheDealPhu> optional = deal.getBienTheDealPhu().stream()
		            .filter(df -> df.getBienThe().getId() == dat.getId())
		            .findFirst();

		        ItemFlash itemFlash = new ItemFlash();
		        itemFlash.setGia(dat.getGia());
		        itemFlash.setHinhAnh(dat.getAnhBia());
		        itemFlash.setId(dat.getId());
		        itemFlash.setSoLuongKho(dat.getSoLuongKho());
		        itemFlash.setTen(dat.getTen());
		        itemFlash.setNotUpdate(false);
		        
		        if (optional.isPresent()) {
		            BienTheDealPhu bienTheDealPhu = optional.get();
		            itemFlash.setConSuDung(bienTheDealPhu.isConSuDung());
		            itemFlash.setSoLuongKhuyenMai(bienTheDealPhu.getToiDaTrenDonVi());
		            itemFlash.setGiaGiam(bienTheDealPhu.getGiaTriGiam());
		            itemFlash.setReady(true);
		        }

		        return itemFlash;
		    }).collect(Collectors.toList());

		    sanPhamFlashSale.setBienThe(itemFlashList);
		    
		    return sanPhamFlashSale;
		}).collect(Collectors.toList());
		flashSaleDTO.setData(sanPhamFlashSalesChinh);
		flashSaleDTO.setDataPhu(sanPhamFlashSalesPhu);
		flashSaleDTO.setSoLuongDaDung(deal.getSoLuongDaDung());
		return flashSaleDTO;

	}

}
