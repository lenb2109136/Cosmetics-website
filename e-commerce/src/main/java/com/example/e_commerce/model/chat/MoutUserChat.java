package com.example.e_commerce.model.chat;

import java.util.ArrayList;
import java.util.List;

public class MoutUserChat extends chatRoot{
	long idUser;
	boolean mount;
	List<String> danhSachDangChat= new ArrayList<String>();
	
	
	public List<String> getDanhSachDangChat() {
		return danhSachDangChat;
	}
	public void setDanhSachDangChat(List<String> danhSachDangChat) {
		this.danhSachDangChat = danhSachDangChat;
	}
	public void setIdUser(long idUser) {
		this.idUser = idUser;
	}
	public long getIdUser() {
		return idUser;
	}
	public boolean isMount() {
		return mount;
	}
	public void setMount(boolean mount) {
		this.mount = mount;
	}
	
	
}
