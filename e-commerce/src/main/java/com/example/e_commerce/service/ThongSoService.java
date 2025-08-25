package com.example.e_commerce.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.e_commerce.DTO.response.DanhMucDTO;
import com.example.e_commerce.DTO.response.ThongSoMangerDTO;
import com.example.e_commerce.Exception.Custom.GeneralException;
import com.example.e_commerce.mapper.DanhMucMapper;
import com.example.e_commerce.model.DanhMuc;
import com.example.e_commerce.model.SanPham;
import com.example.e_commerce.model.ThongSo;
import com.example.e_commerce.model.ThongSoCuThe;
import com.example.e_commerce.repository.ThongSoRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.websocket.server.PathParam;

@Service
public class ThongSoService {
	@Autowired
	private DanhMucSerVice danhMucSerVice;
	
	@Autowired
	private ThongSoRepository thongSoRepository;
	
	@Autowired
	private ThongSoCuTheService thongSoCuTheService;
	
	@Autowired
	private SanPhamService sanPhamService;
	
	public Set<ThongSo> getThongSoByDanhMuc(int idDanhMuc){
		DanhMuc dm= danhMucSerVice.getDanhMucById(idDanhMuc);
		if(dm.getDanhSach().size()!=0){
			throw new GeneralException("Vui lòng chọn danh mục phù hợp", HttpStatus.BAD_REQUEST);
		}
		return dm.getThongSo();
	}
	
	public ThongSo getThongSoById(long thongso) {
		return thongSoRepository.findById(thongso).orElseThrow(()-> new GeneralException("Không tìm thấy thông số", HttpStatus.OK));
	}
	
	@Transactional
	public void save(ThongSo thongSo) {
		if(thongSo.getThongSoCuThe()==null||thongSo.getThongSoCuThe().size()<1){
			throw new GeneralException("Vui lòng cung cấp hơn một thông số cụ thể", HttpStatus.BAD_REQUEST);
		}
				long sl=thongSo.getThongSoCuThe().stream().map(data-> data.getTen().trim()).distinct().count();
		if(sl<thongSo.getThongSoCuThe().size()) {
			throw new GeneralException("Tên các thông số cụ thể không được rỗng và trùng nhau", HttpStatus.BAD_REQUEST);
		}
		ThongSo ts= thongSoRepository.findByTen(thongSo.getTen());
		if(ts!=null) {
			throw new GeneralException("Tên các thông số đã được sử dụng", HttpStatus.BAD_REQUEST);
		}
		
		thongSoRepository.save(thongSo);
		thongSoCuTheService.save(thongSo.getThongSoCuThe(),thongSo);
	}
	
	public Page<ThongSoMangerDTO> getThongSoPhanTrang(int page,String thamso){
		if(page<0) {
			throw new GeneralException("Yêu cầu không hợp lệ",HttpStatus.BAD_REQUEST);
		}
		Pageable p= PageRequest.of(page, 10);
		
		
		Page<ThongSo> pp= thongSoRepository.findByTenContainingIgnoreCase(thamso, p);
		Page<ThongSoMangerDTO> dtoPage = pp.map(thongSo -> {
		    int soLuongPhanTu = thongSo.getThongSoCuThe().size();
		    long soLuongSanPham = thongSo.getThongSoCuThe().stream()
		        .flatMap(data -> data.getSanPham().stream())
		        .map(d -> d.getId())
		        .distinct()
		        .count();

		    return new ThongSoMangerDTO(
		    	thongSo.getId(),
		        thongSo.getTen(),
		        thongSo.getDanhMuc().size(),
		        soLuongPhanTu,
		        soLuongSanPham
		    );
		});
		
		 return dtoPage;
	}
	
	public List<ThongSo> getAll(){
		return thongSoRepository.findAll();
	}
	
	public List<ThongSo> findInList(List<Long> ds) {
		return thongSoRepository.findAllById(ds);
	}
	public List<DanhMucDTO> getDanhMucOfThongSo(long id){
		ThongSo thongSo= thongSoRepository.findById(id).orElseThrow(()->new EntityNotFoundException("Thông số không tồn tại"));
		return DanhMucMapper.createToDanhMucDTO(thongSo.getDanhMuc());
	}
	public List<ThongSoCuThe> getThongSoCuTheOfThongSo(long id){
		ThongSo thongSo= thongSoRepository.findById(id).orElseThrow(()->new EntityNotFoundException("Thông số không tồn tại"));
		return thongSo.getThongSoCuThe();
	}
	@Transactional
	public void deleteDanhMucThongSoByDanhMuc(Integer id, Long idts) {
		ThongSo thongSo=getThongSoById(idts);
		DanhMuc dm=danhMucSerVice.getDanhMucById(id);
		List<Long> tsct=getThongSoCuTheOfThongSo(idts).stream().map(data-> data.getId()).collect(Collectors.toList());
		List<DanhMuc> dmm= new ArrayList<DanhMuc>();
		List<SanPham> sp= new ArrayList<SanPham>();
		if(dm.getDanhSach().size()==0) {
			sp.addAll(dm.getSanPham());
			dmm.add(dm);
		}
		else {
			danhMucSerVice.getAllChildDanhMuc(dmm, dm);
			sp=dmm.stream().flatMap((data)-> data.getSanPham().stream()).collect(Collectors.toList());
		}
		System.out.println("số lượng phần tử danh sách: "+tsct.size());
		// xóa danh mục thông số
		danhMucSerVice.deleteDanhMucThongSoByDanhMuc(dmm.stream().map(data-> data.getId()).collect(Collectors.toList()),idts);
		//xóa sản phẩm thông số cụ thể
		sanPhamService.deleteSanPhamThongSoBySanPham(sp.stream().map(data-> data.getId()).collect(Collectors.toList()), tsct);
	}
	
	@Transactional
	public void update(ThongSo thongSo) {
	    ThongSo ts = getThongSoById(thongSo.getId());
	    if (ts == null) {
	        throw new GeneralException("Không tìm thấy thông tin thông số ", HttpStatus.NOT_FOUND);
	    }
	    ts.setTen(thongSo.getTen());
	    thongSoRepository.save(ts);

	    List<Long> newIds = thongSo.getThongSoCuThe().stream()
	        .map(ThongSoCuThe::getId)
	        .filter(Objects::nonNull)
	        .distinct()
	        .collect(Collectors.toList());

	    List<ThongSoCuThe> toRemove = ts.getThongSoCuThe().stream()
	        .filter(old -> !newIds.contains(old.getId()))
	        .toList();

	    ts.getThongSoCuThe().removeAll(toRemove);
	    thongSoCuTheService.deleteAll(toRemove);

	    long distinctNameCount = thongSo.getThongSoCuThe().stream()
	        .map(d -> d.getTen().trim())
	        .filter(name -> !name.isEmpty())
	        .distinct()
	        .count();

	    if (distinctNameCount != thongSo.getThongSoCuThe().size()) {
	        throw new GeneralException("Các thông số cụ thể không được trùng tên", HttpStatus.BAD_REQUEST);
	    }

	    List<ThongSoCuThe> cuTheCu = thongSo.getThongSoCuThe().stream()
	        .filter(d -> d.getId() !=0 && d.getId() > 0)
	        .toList();

	    List<ThongSoCuThe> cuTheMoi = thongSo.getThongSoCuThe().stream()
	        .filter(d -> d.getId() == 0 || d.getId() == 0)
	        .toList();

	    Map<Long, String> newIdToName = cuTheCu.stream()
	        .collect(Collectors.toMap(
	            ThongSoCuThe::getId,
	            ThongSoCuThe::getTen,
	            (v1, v2) -> v1
	        ));

	    ts.getThongSoCuThe().forEach(old -> {
	        if (newIdToName.containsKey(old.getId())) {
	            old.setTen(newIdToName.get(old.getId()));
	            newIdToName.remove(old.getId());
	        }
	    });

	    thongSoRepository.save(ts);

	    cuTheMoi.forEach(tsct -> {
	        ThongSoCuThe t = new ThongSoCuThe();
	        t.setTen(tsct.getTen());
	        t.setThongSo(ts);
	        thongSoCuTheService.save(t);
	    });
	}
	
	public List<Map<String, Object>> getThongSoCuTheForFilter(String ten, int id) {
		DanhMucSerVice danhMucSerVice= ServiceLocator.getBean(DanhMucSerVice.class);
		System.out.println("id cần tìm là: "+id);
		List<DanhMuc> danhMucs= new ArrayList<DanhMuc>();
		if(id!=0) {
			System.out.println("ĐI VÀO ĐÂY KIỂM");
			DanhMuc dd= danhMucSerVice.getDanhMucById(id);
			danhMucSerVice.getAllChildDanhMuc(danhMucs, dd);
		}
		else {
			System.out.println("HUHU");
			danhMucs=this.danhMucSerVice.getAll();
		}
		List<ThongSoCuThe> thongSoCuThes= thongSoCuTheService.getThuongHieuBySanPhamAndDanhMuc(ten, danhMucs.stream().map(f->f.getId())
				.distinct().collect(Collectors.toList()));
		Map<Long, Map<String, Object>> result= new HashMap<Long, Map<String,Object>>();
		thongSoCuThes.forEach(d->{
			Map<String,Object> map= result.get(d.getThongSo().getId());
			
			if(map==null) {
				map=new HashMap<String, Object>();
				result.put(d.getThongSo().getId(), map);
				map.put("ten",d.getThongSo().getTen());
				map.put("danhSachCon", new ArrayList<ThongSoCuThe>());
			}
			List<ThongSoCuThe> thongSoCuThe=(ArrayList<ThongSoCuThe>)map.get("danhSachCon");
			thongSoCuThe.add(d);
		});
		return result.values().stream().collect(Collectors.toList());
	}

	
}
