package com.example.e_commerce.DTO.response;

import java.util.List;

import jakarta.validation.constraints.NotBlank;

public class AdressDTO {
	@NotBlank(message = "Vui lòng chọn địa chỉ phù hợp")
	private String name;
	private int code;
	List<AdressDTO> wards;
	List<AdressDTO> districts;
	
	
	public List<AdressDTO> getWards() {
		return wards;
	}
	public void setWards(List<AdressDTO> wards) {
		this.wards = wards;
	}
	public List<AdressDTO> getDistricts() {
		return districts;
	}
	public void setDistricts(List<AdressDTO> districts) {
		this.districts = districts;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	
	
}
