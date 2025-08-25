package com.example.e_commerce.contoller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.e_commerce.DTO.response.APIResponse;
import com.example.e_commerce.constants.Routes;
import com.example.e_commerce.service.PhanHoiService;

@RestController
@RequestMapping(Routes.API+"/comment")
public class PhanHoiController {
	@Autowired
	private PhanHoiService phanHoiService;
	@PostMapping("/save")
	public ResponseEntity<APIResponse> save(@RequestBody Map<String, Object> data){
		Long sanPham = ((Number) data.get("sanPham")).longValue();
		phanHoiService.themPhanHoi(sanPham, (String)data.get("noiDung"), (int)data.get("soSao"));
		return new ResponseEntity<APIResponse>(new APIResponse("",null),HttpStatus.OK);
	}
}
