package com.example.e_commerce.contoller;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.e_commerce.DTO.response.APIResponse;
import com.example.e_commerce.constants.Routes;
import com.example.e_commerce.model.ThongSo;
import com.example.e_commerce.service.ThongSoService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.websocket.server.PathParam;

@RestController
@RequestMapping(Routes.API+ "/thongso")
@Validated
public class ThongSoController {
	@Autowired
	private ThongSoService thongSoService;
	
	@GetMapping("/getthongso/{iddanhmuc}")
	public ResponseEntity<APIResponse> getThongSoByDanhMuc(@PathVariable("iddanhmuc") int idDanhMuc){
		return new ResponseEntity<APIResponse>(
				new APIResponse("", thongSoService.getThongSoByDanhMuc(idDanhMuc)),HttpStatus.OK);
	}
	@PostMapping("save")
	public ResponseEntity<APIResponse> save(@Valid @RequestBody ThongSo thongSo){
		thongSoService.save(thongSo);
		return new ResponseEntity<APIResponse>(new APIResponse("", null),HttpStatus.OK);
	}
	@GetMapping("/getall")
	public ResponseEntity<APIResponse> getAll(){
		return new ResponseEntity<APIResponse>(new APIResponse("", thongSoService.getAll()),HttpStatus.OK);
	} 
	@GetMapping("/getthongsobyid/{id}")
	public ResponseEntity<APIResponse> getthongsobyid(
	@NotNull(message = "Vui lòng cung cấp thông tin thông số") 
	@Min(value = 0, message = "Thông số trên không tồn tại") @PathVariable("id") Long id){
		return new ResponseEntity<APIResponse>(new APIResponse("", thongSoService.getThongSoById(id)),HttpStatus.OK);
	} 
	
	@GetMapping("/thongsophantrang/{trang}")
	public ResponseEntity<APIResponse> getThongSoPhanTrang(@PathVariable("trang") int trang,
			@RequestParam(value = "thamso",required = false, defaultValue = "") String thamso){
		return new ResponseEntity<APIResponse>(new APIResponse(null,thongSoService.getThongSoPhanTrang(trang, thamso)),HttpStatus.OK);
		
	}
	
	@PutMapping("/updatethongso")
	public ResponseEntity<APIResponse> update(@Valid @RequestBody ThongSo thongSo){
		thongSoService.update(thongSo);
		return new ResponseEntity<APIResponse>(new APIResponse("", null),HttpStatus.OK);
	}
	@GetMapping("/getmucthongso/{id}")
	public ResponseEntity<APIResponse> getDanhMucOfThongSo(
	@NotNull(message = "Vui lòng cung cấp thông tin thông số") 
    @Min(value = 0, message = "Không tìm thấy thông số cần kiểm") 
    @PathVariable("id") Integer id){
		return new ResponseEntity<APIResponse>(new APIResponse("", thongSoService.getDanhMucOfThongSo(id)),HttpStatus.OK);
	}
	
	@GetMapping("/getthongsocuthe/{id}")
	public ResponseEntity<APIResponse> getThongSoCuTheOfThongSo(
	@NotNull(message = "Vui lòng cung cấp thông tin thông số") 
    @Min(value = 0, message = "Không tìm thấy thông số cần kiểm") 
    @PathVariable("id") Integer id){
		return new ResponseEntity<APIResponse>(new APIResponse("", thongSoService.getThongSoCuTheOfThongSo(id)),HttpStatus.OK);
	}
	
	@DeleteMapping("/deletedanhmuc/{id}/{idts}")
	public ResponseEntity<APIResponse> removeThongSoCuThe(
		@NotNull(message = "Vui lòng cung cấp thông tin thông số") 
		@Min(value = 0, message = "Thông số trên không tồn tại") @PathVariable("id") Integer id,
		@NotNull(message = "Vui lòng cung cấp thông tin thông số") 
		@Min(value = 0, message = "Thông số trên không tồn tại") @PathVariable("idts") Long idts
			) {
		thongSoService.deleteDanhMucThongSoByDanhMuc(id,idts);
		return new ResponseEntity<APIResponse>(new APIResponse("", null),HttpStatus.OK);
	}
	
	@GetMapping("/getCondition")
	public ResponseEntity<APIResponse> getThuongHieuBySanPhamAndDanhMuc(
			@RequestParam(value="ten", required = false, defaultValue = "") String ten,
			@RequestParam(value="id", required = false, defaultValue = "0") int id
			){
		return new ResponseEntity<APIResponse>(
				new APIResponse("", thongSoService.getThongSoCuTheForFilter(ten, id)),HttpStatus.OK);
	}
	
	
}

