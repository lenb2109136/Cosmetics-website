package com.example.e_commerce.contoller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.e_commerce.DTO.response.APIResponse;
import com.example.e_commerce.constants.Routes;
import com.example.e_commerce.model.KhachHang;
import com.example.e_commerce.model.NguoiDung;
import com.example.e_commerce.model.NhanVien;
import com.example.e_commerce.service.JwtService;
import com.example.e_commerce.service.NguoiDungService;

@RestController
@RequestMapping(Routes.API+"/nguoidung")
public class NguoiDungController {
	@Autowired
	private NguoiDungService nguoiDungService;
	
	@Autowired 
	private JwtService jwtService;
	
	@GetMapping("getkhbysdt")
	public ResponseEntity<APIResponse> getKhachHangBySoDienThoai(@RequestParam(value = "sdt",required = false,defaultValue = "") String sdt){
		System.out.println("");
		KhachHang khachHang= nguoiDungService.getKhachHangBySDT(sdt);
		if(khachHang==null) {
			return new ResponseEntity<APIResponse>(new APIResponse("", null), HttpStatus.OK);
			
		}
		else {
			Map<String, Object> map= new HashMap<String, Object>();
			map.put("id",khachHang.getId());
			map.put("ten", khachHang.getTen());
			map.put("sdt",khachHang.getSodienthoai());
			return new ResponseEntity<APIResponse>(new APIResponse("", map), HttpStatus.OK);
		}
	}
	
	@GetMapping
	public ResponseEntity<APIResponse> getInfo(){
		NguoiDung nd= nguoiDungService.getById(jwtService.getIdUser());
			Map<String, Object> m= new HashMap<String, Object>();
			m.put("ten", nd.getTen());
			m.put("soDienThoai",nd.getSodienthoai());
			m.put("diaChi", nd.getDiachi().substring(0, nd.getDiachi().lastIndexOf(".")));
			m.put("email", nd.getEmail());
			m.put("matKhau", nd.getMatkhau());
			String diaChi=nd.getDiachi();
			diaChi = diaChi.substring(diaChi.lastIndexOf(".") + 1); // Lấy phần sau dấu chấm cuối

			String[] codeStr = diaChi.split(" "); // Tách bằng dấu cách

			// Chuyển sang mảng int
			int[] codeInt = Arrays.stream(codeStr)
			                      .mapToInt(Integer::parseInt)
			                      .toArray();

			m.put("code", codeInt);

			return new ResponseEntity<APIResponse>(new APIResponse("", m), HttpStatus.OK);
		
	}
	
	@PutMapping
	public ResponseEntity<APIResponse> update(
			@RequestBody NguoiDung nguoiDung) {
		nguoiDungService.update(nguoiDung);
		return new ResponseEntity<APIResponse>(new APIResponse("success", null),HttpStatus.OK);
	}
	
	
}
