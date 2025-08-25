package com.example.e_commerce.model.chat;


public class UserAcTivity extends chatRoot{
	int activity;
	//0=> ngắt kết nối
	//1=> đã kết nối
	//2=> đang chat
//	=> user tác dộng
	long idUser; 
	
//	=> user bị tác động
	long userBiTacDong;
	
	
	
	public long getUserBiTacDong() {
		return userBiTacDong;
	}
	public void setUserBiTacDong(long userBiTacDong) {
		this.userBiTacDong = userBiTacDong;
	}
	public int getActivity() {
		return activity;
	}
	public void setActivity(int activity) {
		this.activity = activity;
	}
	public long getIdUser() {
		return idUser;
	}
	public void setIdUser(long idUser) {
		this.idUser = idUser;
	}
	
	
}
