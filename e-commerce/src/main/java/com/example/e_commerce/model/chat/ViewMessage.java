package com.example.e_commerce.model.chat;

import java.util.List;

public class ViewMessage extends chatRoot{
	int typeView;
	//0 đã gửi ;
	//1 đã đã nhận;
	//2 đã xem;
	List<Long> tinNhan;
	
	public List<Long> getTinNhan() {
		return tinNhan;
	}
	public void setTinNhan(List<Long> tinNhan) {
		this.tinNhan = tinNhan;
	}
	public int getTypeView() {
		return typeView;
	}
	public void setTypeView(int typeView) {
		this.typeView = typeView;
	}
	
}
