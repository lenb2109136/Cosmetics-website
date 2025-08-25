package com.example.e_commerce.mapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.e_commerce.DTO.request.DanhMucCreate;
import com.example.e_commerce.DTO.request.TrungGian;
import com.example.e_commerce.DTO.response.DanhMucDTO;
import com.example.e_commerce.model.DanhMuc;
import com.example.e_commerce.service.DanhMucSerVice;
import com.example.e_commerce.service.ServiceLocator;

public class DanhMucMapper {
	
	public static List<DanhMucDTO> createToDanhMucDTO(Set<DanhMuc> danhmuc){
		return danhmuc.stream().map((data)-> new DanhMucDTO(data.getId(),data.getTen())).collect(Collectors.toList());
	}
	
	
	public static DanhMuc getNewDanhMucFromDanhMucCreate(DanhMucCreate danhMucCreate) {
		DanhMuc danhMuc = new DanhMuc();
		danhMuc.setTen(danhMucCreate.getTen());
		return danhMuc;
	}
	
	public static DanhMucCreate danhMucMapperToDanhMucCreate(DanhMuc danhMuc) {
	
		DanhMucSerVice danhMucSerVice = ServiceLocator.getBean(DanhMucSerVice.class);
		DanhMucCreate danhMucCreate= new DanhMucCreate();
		danhMucCreate.setDanhMucCon(danhMuc.layTatCaIdDanhMucConKhongLayNo(danhMuc));
		danhMucCreate.setTen(danhMuc.getTen());
		danhMucCreate.setThongSoChon(danhMuc.getThongSo().stream().map(data->data.getId()).collect(Collectors.toList()));
		TrungGian trangGiun = new TrungGian();
		trangGiun.setTen("");
		trangGiun.setId(0);
		danhMucCreate.setDonDanhMuc(trangGiun);
		danhMucCreate.setTrunggian(trangGiun);
		danhMucCreate.setId(danhMuc.getId());
		List<DanhMuc> dm= new ArrayList<DanhMuc>();
		danhMucSerVice.getDanhMucCha(danhMuc, dm);
		if(danhMuc.getDanhMucCon().size()!=0) {
			danhMucCreate.setCoCon(true);
		}
		if(danhMuc.getSanPham().size()!=0) {
			danhMucCreate.setCoSanPhamCon(true);
		}
		Collections.reverse(dm);
		dm.remove(dm.size()-1);
		danhMucCreate.setPickCategory(dm);
		return danhMucCreate;
	}
}
