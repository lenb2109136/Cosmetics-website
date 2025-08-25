package com.example.e_commerce.contoller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.e_commerce.DTO.request.FlashSaledto.FlashSaleDTO;
import com.example.e_commerce.DTO.response.APIResponse;
import com.example.e_commerce.Exception.Custom.GeneralException;
import com.example.e_commerce.constants.Routes;
import com.example.e_commerce.service.KhuyenMaiTangKemSerivce;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping(Routes.API+"/bonus")
public class KhuyenMaiTangKemController {
	@Autowired
	private KhuyenMaiTangKemSerivce khuyenMaiTangKemSerivce;
	@PostMapping
	public ResponseEntity<APIResponse> save(@RequestBody FlashSaleDTO flashSaleDTO) {
		flashSaleDTO.validatedBonus();
		khuyenMaiTangKemSerivce.save(flashSaleDTO);
		return new ResponseEntity<APIResponse>(new APIResponse(null, null),HttpStatus.OK);
	}
	@PostMapping("/update/{id}")
	public ResponseEntity<APIResponse> update(@RequestBody @Valid FlashSaleDTO khuyenMaiTangKem,
			@PathVariable("id") @NotNull(message = "Khuyến mãi không tồn tại") @Min(value = 1, message = "khuyến mãi không tồn tại") Long id
			) {
		khuyenMaiTangKem.validatedBonusForUpdate(id);
		khuyenMaiTangKemSerivce.update(khuyenMaiTangKem,id);
		return new ResponseEntity<APIResponse>(new APIResponse("ok", null),HttpStatus.OK);
		
	}
	@GetMapping("/getbonus/{id}")
	public ResponseEntity<APIResponse> getDeal(
			@PathVariable("id") @NotNull(message = "Deal không tồn tại") @Min(value = 1, message = "Deal không tồn tại") Long id){
		return new ResponseEntity<APIResponse>(new APIResponse(null, khuyenMaiTangKemSerivce.mapToBonusDTOFromBonus(id)),HttpStatus.OK);
	}
	@GetMapping("/getbystatus/{status}")
	public ResponseEntity<APIResponse> getByStatus(
			@PathVariable @NotNull(message = "Vui lòng cung cấp trạng thái Flashsale" )@Min(value = 0, message = "Thông tin trạng thái cung cấp không hợp lệ") Integer status,
			@RequestParam @NotNull(message = "Vui lòng cung cấp thời gian bắt đầu lọc") LocalDateTime bd,
			@RequestParam @NotNull(message = "Vui lòng cung cấp thời gian kết thúc lọc") LocalDateTime kt,
			@RequestParam @NotNull(message = "Vui lòng cung cấp trang cần lọc") @Min(value = 0, message = "Thông tin trang cung cấp không hợp lệ") Integer trang
			) {
		if(!bd.isBefore(kt)) {
			throw new GeneralException("Thời gian kết thúc phải sau thười gian bắt đầu",HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<APIResponse>(new APIResponse(null,khuyenMaiTangKemSerivce.getByStatus(trang, bd, kt, status)),HttpStatus.OK);
	}
	@GetMapping("active/{id}")
	public ResponseEntity<APIResponse> setActiveBonus(
			@PathVariable("id")@NotNull(message = "Thông tin flash sale cung cấp không hợp lệ")
			@Min(value = 1,message = "Thông tin flash sale cung cấp không hợp lệ") Long id,
			@RequestParam("active") @NotNull(message = "Vui lòng cung cấp hoạt động của flashsale") Boolean active){
		khuyenMaiTangKemSerivce.setActiveBonus(id, active);
		return new ResponseEntity<APIResponse>(new APIResponse(null, null),HttpStatus.OK);
	}
}
