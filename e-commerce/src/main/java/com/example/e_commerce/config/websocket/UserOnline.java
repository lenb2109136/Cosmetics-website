package com.example.e_commerce.config.websocket;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import com.example.e_commerce.model.NhanVien;

import jakarta.websocket.Session;

@Component
public class UserOnline {
	public static Map<Long,Set<Long>> phanCongChat= new ConcurrentHashMap<Long, Set<Long>>();
	public static Map<Long,WebSocketSession> danhSachNhanVien= new ConcurrentHashMap<Long, WebSocketSession>();
	public static Map<Long,WebSocketSession> danhSachKhachHang= new ConcurrentHashMap<Long, WebSocketSession>();
}
