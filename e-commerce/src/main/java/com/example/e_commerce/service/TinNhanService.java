package com.example.e_commerce.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.e_commerce.Exception.Custom.GeneralException;
import com.example.e_commerce.Validator.TimeValidator;
import com.example.e_commerce.config.websocket.UserOnline;
import com.example.e_commerce.model.KhachHang;
import com.example.e_commerce.model.NguoiDung;
import com.example.e_commerce.model.TinNhan;
import com.example.e_commerce.model.chat.Message;
import com.example.e_commerce.repository.TinNhanRepository;

@Service
public class TinNhanService {

	@Autowired
	private TinNhanRepository tinNhanRepository;

	@Autowired
	private NguoiDungService nguoiDungService;

	@Autowired
	private UserOnline online;
	
	@Autowired
	private JwtService jwtService;

	public List<Map<String, Object>> getTinNhanNew() {
		List<Object[]> danhSach = tinNhanRepository.getKhachHangVaTinNhanMoiNhat();
		TimeValidator time = new TimeValidator();
		return danhSach.stream().map(d -> {
			Map<String, Object> result = new HashMap<String, Object>();
			KhachHang k = (KhachHang) d[0];
			TinNhan t = (TinNhan) d[1];
			result.put("id", k.getId());
			result.put("tenKhach", k.getTen());
			System.out.println("khách hàng được lấy: "+k.getId());
			System.out.println("số lượng phần tử: "+online.phanCongChat.size());
			if (online.phanCongChat.containsKey(k.getId())) {
				result.put("dangHoatDong", true);
				
			} else {
				result.put("dangHoatDong", false);
			}

			if (t != null) {
				result.put("noiDungTinNhan", t.getNoiDungTinNhan());
				String thoiHan = time.getThoiGianTuNgay(t.getNgayGioNhan());
				result.put("thoiHanNhan", thoiHan);
				result.put("khach", t.getNhanVien() == null);
				result.put("statusKhachHang", t.getStatusKhachHang());
				result.put("statusNhanVien", t.getStatusNhanVien());
				if (t.getNhanVien() != null) {
					result.put("nhanVien", t.getNhanVien().getId());
					result.put("tenNhanVien", t.getNhanVien().getTen());
				} else {
					result.put("nhanVien", 0);
				}
			}

			return result;
		}).collect(Collectors.toList());
	}

	public List<TinNhan> getTinNhanChuaNhanNhanVien() {
		return tinNhanRepository.getTinNhanChuaNhanNhanVien();
	}

	@Transactional
	public void setDanhanTinNhan(TinNhan t) {
		t.setStatusNhanVien(1);
		tinNhanRepository.save(t);
	}

	@Transactional
	public void save(TinNhan t) {
		tinNhanRepository.save(t);
	}
	
	public TinNhan getById(long id) {
		return tinNhanRepository.findById(id).orElseThrow(()-> new GeneralException("Không tồn tại tin nhắn",HttpStatus.BAD_REQUEST));
	}

	public List<Message> getTinNhanOfKhachhang(int trang, long id,long idTinNhan) {
		NguoiDung n= nguoiDungService.getById(jwtService.getIdUser());
		if(n instanceof KhachHang) {
			System.out.println("đây là khách hàng");
			id=n.getId();
		}
		Pageable page = PageRequest.of(trang, 20);
		return tinNhanRepository.getTinNhanOfKhachHang(page,id,idTinNhan).stream().map(d->{
			return Message.getMessgaeFromTinNhan(d);
		}).collect(Collectors.toList());
	}
	
	public List<TinNhan> getTinNhanByKhachHangAndStatus(
			long id,
			List<Integer> status,
			boolean isNhanVien
			){
		return tinNhanRepository.getTinNhanChuaCheckOfNguoiDung(id, status, isNhanVien);
	}
	public List<TinNhan> getTinNhanByKhachHangAndStatusKhach(
			long id,
			List<Integer> status
			){
		return tinNhanRepository.getTinNhanChuaCheckOfNguoiDungKhach(id, status);
	}
}
