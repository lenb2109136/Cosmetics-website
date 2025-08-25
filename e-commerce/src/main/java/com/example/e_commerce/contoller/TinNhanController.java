package com.example.e_commerce.contoller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.e_commerce.DTO.response.APIResponse;
import com.example.e_commerce.constants.Routes;
import com.example.e_commerce.service.TinNhanService;

@RestController
@RequestMapping(Routes.API+ "/tinnhan")
public class TinNhanController {
	@Autowired
	private TinNhanService tinNhanService;
	
	@GetMapping("/getinit")
	public ResponseEntity<APIResponse> getInit(){
		return  new ResponseEntity<APIResponse>(new APIResponse("", tinNhanService.getTinNhanNew()),HttpStatus.OK);
	}
	
	@GetMapping("/getby-khachhang")
	public ResponseEntity<APIResponse> getTinNhanOfKhachHang(
			@RequestParam("id") long id,
			@RequestParam("idtinnhan") int idTinNhan
			){
		return  new ResponseEntity<APIResponse>(new APIResponse("", tinNhanService.getTinNhanOfKhachhang(0, id,idTinNhan)),HttpStatus.OK);
	}
}
