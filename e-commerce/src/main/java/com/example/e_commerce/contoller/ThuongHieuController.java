package com.example.e_commerce.contoller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.e_commerce.DTO.response.APIResponse;
import com.example.e_commerce.constants.Routes;
import com.example.e_commerce.model.ThuongHieu;
import com.example.e_commerce.service.ThuongHieuService;

@RestController
@RequestMapping(Routes.API+"/thuonghieu")
public class ThuongHieuController {
	@Autowired
	private ThuongHieuService thuongHieuService;
	
	@GetMapping("/get")
	public ResponseEntity<APIResponse> getThuuongHieu(){
		return new ResponseEntity<APIResponse>( new APIResponse("", thuongHieuService.getThuongHieu()),HttpStatus.OK);
	}
	
	@GetMapping("/getmanager")
	public ResponseEntity<APIResponse> getThuuongHieuManager(@RequestParam("ten") String ten,
			@RequestParam(value = "trang", required = false, defaultValue = "0") int trang
			){
		return new ResponseEntity<APIResponse>( new APIResponse("", thuongHieuService.getManager(ten,trang)),HttpStatus.OK);
	}
	
	@PutMapping("update")
	public void update(
			@RequestParam(value = "anhBia", required = false) MultipartFile anhBia,
			@RequestParam(value = "anhDaiDien", required = false) MultipartFile anhDaiDien,
			@RequestParam(value = "anhNen", required = false) MultipartFile anhNen,
			@RequestParam(value = "ten", required = false) String ten,
			@RequestParam(value = "id") long id
			) {
		thuongHieuService.update(ten, anhNen, anhDaiDien, anhBia, id);
	}
	@PostMapping("save")
	public void save(
			@RequestParam(value = "anhBia", required = false) MultipartFile anhBia,
			@RequestParam(value = "anhDaiDien", required = false) MultipartFile anhDaiDien,
			@RequestParam(value = "anhNen", required = false) MultipartFile anhNen,
			@RequestParam(value = "ten", required = false) String ten
			) {
		thuongHieuService.save(ten, anhNen, anhDaiDien, anhBia);
	}
	
	@GetMapping("/getCondition")
	
	public ResponseEntity<APIResponse> getThuongHieuBySanPhamAndDanhMuc(
			@RequestParam(value="ten", required = false, defaultValue = "") String ten,
			@RequestParam(value="id", required = false, defaultValue = "0") int id
			){
		return new ResponseEntity<APIResponse>(
				new APIResponse("", thuongHieuService.getThuongHieuBySanPhamAndDanhMuc(ten, id)),HttpStatus.OK);
	}
	
	@GetMapping("/getinfo/{id}")
	public ResponseEntity<APIResponse> getInfo(@PathVariable("id") long id){
		return new ResponseEntity<APIResponse>(
				new APIResponse("", thuongHieuService.getById( id)),HttpStatus.OK);
	}
	
	@GetMapping("/getall")
	public ResponseEntity<APIResponse> getAll(@RequestParam(value = "ten",required = false,defaultValue = "") String ten){
		return new ResponseEntity<APIResponse>(
				new APIResponse("", thuongHieuService.getAll(ten)),HttpStatus.OK);
	}
}
