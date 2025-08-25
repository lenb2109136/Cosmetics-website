package com.example.e_commerce.contoller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cloudinary.Api;
import com.example.e_commerce.DTO.request.PhieuKiemDTO;
import com.example.e_commerce.DTO.response.APIResponse;
import com.example.e_commerce.constants.Routes;
import com.example.e_commerce.model.PhieuKiemHang;
import com.example.e_commerce.service.PhieuKiemService;

@RestController
@RequestMapping(Routes.API+"/phieukiemhang")
public class PhieuKiemHangController {
	
	@Autowired 
	PhieuKiemService phieuKiemService;
	@PostMapping
	public ResponseEntity<APIResponse> save(@RequestBody List<PhieuKiemDTO> p) {
		phieuKiemService.save(p,false);
		return new ResponseEntity<APIResponse>(new APIResponse(null, null),HttpStatus.OK);
	}
	

	@GetMapping("/get-info-haohut")
	public ResponseEntity<APIResponse> getÌnoHaoHut(
	        @RequestParam int trang,
	        @RequestParam String ten,
	        @RequestParam   LocalDateTime bd,
	        @RequestParam  LocalDateTime kt,
	        @RequestParam  int active,
	        @RequestParam  String maVach) {
	    return new ResponseEntity<>(new APIResponse(null, phieuKiemService.getThongTinHaoHut(trang, ten, bd, kt,active,maVach)), HttpStatus.OK);
	}
	@GetMapping("/get-thongke")
	public ResponseEntity<APIResponse> getAllPage(
			 @RequestParam String ten,
		        @RequestParam  LocalDateTime bd,
		        @RequestParam  LocalDateTime kt,
		        @RequestParam  int active,
		        @RequestParam  String maVach) {
	    return new ResponseEntity<>(new APIResponse(null, phieuKiemService.getHoaHutHangHoa(ten, bd, kt, active, maVach)), HttpStatus.OK);
	}
	
	@PutMapping("/duyet/{id}")
	public ResponseEntity<APIResponse> duyet(
			 @PathVariable("id") long id) {
		phieuKiemService.DuyetPhieuKiem(id);
	    return new ResponseEntity<>(new APIResponse(null, null), HttpStatus.OK);
	}
	
	@GetMapping("/getupdate/{id}")
	public ResponseEntity<APIResponse> getAllPage(
	        @PathVariable("id") long id) {
	    return new ResponseEntity<>(new APIResponse(null, phieuKiemService.getPhieuKiem(id)), HttpStatus.OK);
	}
	
	@PostMapping("/update/{id}")
	public ResponseEntity<APIResponse> update(
	        @PathVariable("id") long id, @RequestBody List<PhieuKiemDTO> pp) {
	    return new ResponseEntity<>(new APIResponse(null, phieuKiemService.update(id,pp)), HttpStatus.OK);
	}
	
	@GetMapping("/get-all-page")
	public ResponseEntity<APIResponse> getAllPage(
	        @RequestParam int trang,
	        @RequestParam String ten,
	        @RequestParam  LocalDateTime bd,
	        @RequestParam  LocalDateTime kt,
	        @RequestParam int active,
	        @RequestParam String maVach) {
	    return new ResponseEntity<>(new APIResponse("Success", phieuKiemService.getPhieuKiemForUppdate(trang, ten, bd, kt, active, maVach)), HttpStatus.OK);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<APIResponse> huyDietPhieuNhap(@PathVariable("id") long id) {
		 phieuKiemService.deleteKhauHao(id);
	    return new ResponseEntity<>(new APIResponse("",null), HttpStatus.OK);
	}

	
}

