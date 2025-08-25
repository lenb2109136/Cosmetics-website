package com.example.e_commerce.mapper;

import com.example.e_commerce.DTO.request.NhaCungCapDTO;
import com.example.e_commerce.model.DonViCungCap;

public class DonViCungCapMapper {
	public static DonViCungCap mapToDonViCungCap(NhaCungCapDTO nhaCungCapDTO) {
		DonViCungCap donViCungCap= new DonViCungCap();
		donViCungCap.setTen(nhaCungCapDTO.getName());
		donViCungCap.setSoTaiKhoan(nhaCungCapDTO.getSoTaiKhoan());
		donViCungCap.setSoDienThoai(nhaCungCapDTO.getSoDienThoai());
		donViCungCap.setMaSoThue(nhaCungCapDTO.getMaSoThue());
		if(nhaCungCapDTO.getCode()==0) {
			
		}
		else {
			donViCungCap.setId(nhaCungCapDTO.getCode());
		}
		System.out.println("đo vô địa chỉ ");
		System.out.println(nhaCungCapDTO.getHuyen().getName());
		String diaChi=nhaCungCapDTO.getTinh().getName()+", "+nhaCungCapDTO.getHuyen().getName()+", "+nhaCungCapDTO.getXa().getName()
				+", "+nhaCungCapDTO.getDiaChi()+"."+nhaCungCapDTO.getTinh().getCode()+" "+nhaCungCapDTO.getHuyen().getCode()+" "+nhaCungCapDTO.getXa().getCode();
		donViCungCap.setDiaChi(diaChi);
		return donViCungCap;
		
	}
}
