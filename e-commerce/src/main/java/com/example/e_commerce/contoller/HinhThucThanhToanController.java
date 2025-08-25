package com.example.e_commerce.contoller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.e_commerce.DTO.response.APIResponse;
import com.example.e_commerce.constants.Routes;
import com.example.e_commerce.repository.HinhThucThanhToanRepository;

@RestController
@RequestMapping(Routes.API+"/hinhthucthanhtoan")
public class HinhThucThanhToanController {
	@Autowired
	private HinhThucThanhToanRepository hinhThucThanhToanRepository;
	
	
	@GetMapping("getall")
	public ResponseEntity<APIResponse> getAll(){
		return new ResponseEntity<APIResponse>(new APIResponse("", hinhThucThanhToanRepository.findAll()),HttpStatus.OK);
	}
}
