package com.example.e_commerce.config.websocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.poi.sl.draw.geom.GuideIf.Op;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.example.e_commerce.Exception.Custom.GeneralException;
import com.example.e_commerce.constants.MessageType;
import com.example.e_commerce.model.AnhChat;
import com.example.e_commerce.model.KhachHang;
import com.example.e_commerce.model.NguoiDung;
import com.example.e_commerce.model.NhanVien;
import com.example.e_commerce.model.TinNhan;
import com.example.e_commerce.model.chat.DropHeart;
import com.example.e_commerce.model.chat.Message;
import com.example.e_commerce.model.chat.MoutUserChat;
import com.example.e_commerce.model.chat.UserAcTivity;
import com.example.e_commerce.model.chat.ViewMessage;
import com.example.e_commerce.model.chat.chatRoot;
import com.example.e_commerce.service.AnhChatService;
import com.example.e_commerce.service.NguoiDungService;
import com.example.e_commerce.service.TinNhanService;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class HandleChat extends TextWebSocketHandler {

	@Autowired
	private UserOnline userOnline;
	
	@Autowired
	private AnhChatService anhChatService;

	@Autowired
	private TinNhanService tinhNhanService;
	
	@Autowired
	private NguoiDungService nguoiDungService;

	private ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {

		Long userId = (Long) session.getAttributes().get("id");
		String role = (String) session.getAttributes().get("role");
		if (role.equals("CUSTOMER")) {
			Set<Long> danhSachPhanCong= new HashSet<Long>();
			UserOnline.phanCongChat.put(userId, danhSachPhanCong);
			UserOnline.danhSachKhachHang.put(userId, session);
			UserAcTivity u= new UserAcTivity();
			u.setActivity(1);
			u.setIdUser((long)session.getAttributes().get("id"));
			u.setType(MessageType.USERACTIVITY);
			senToKhachNhanVien(u);
			List<TinNhan> th= tinhNhanService.getTinNhanByKhachHangAndStatusKhach(
	        		userId,Arrays.asList(0,1));
	        List<Long> gh= th.stream().map(d->{
	        	d.setStatusKhachHang(1);
	        	tinhNhanService.save(d);
	        	return d.getId();
	        }).collect(Collectors.toList());
	        ViewMessage vi= new ViewMessage();
	        vi.setTinNhan(gh);
	        vi.setType(MessageType.VIEWMESSGE);
	        vi.setTypeView(1);
	        if(gh.size()!=0) {
	        	Set<Long> ds= userOnline.phanCongChat.get(userId);
				if(ds!=null) {
					senToKhachNhanVienFilter(vi, ds);
				}
	        }
		}
		if (role.equals("EMPLOYEE")) {
			UserOnline.danhSachNhanVien.put(userId, session);
		}
		
		
	}
	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message) {
		chatRoot me = null;
		String role = (String) session.getAttributes().get("role");
		System.out.println(message.getPayload());
		Long idd = (Long) session.getAttributes().get("id");
		NguoiDung n= nguoiDungService.getById(idd);
		try {
			me = objectMapper.readValue(message.getPayload().getBytes(), chatRoot.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (me instanceof Message && role.equals("CUSTOMER")) {
			Message m = (Message) me;
			if (m.getNoiDungTinNhan() == null || m.getNoiDungTinNhan().equals("")) {
			}
			TinNhan t = new TinNhan();
			t.setNoiDungTinNhan(m.getNoiDungTinNhan());
			Set<Long> ll= userOnline.phanCongChat.get(idd);
			int g=0;
			if(ll.size()==0 && userOnline.danhSachNhanVien.size()!=0) {
				g=1;
			}
		
			if (ll.size() !=0) {
				g=2;
			}
			if(m.getRe()!=0) {
				TinNhan h= tinhNhanService.getById(m.getRe());
				t.setReply(h);
			}
			t.setStatusNhanVien(g);
			t.setKhachHang((KhachHang)n);
			t.setStatusKhachHang(2);
			
			tinhNhanService.save(t);
			if(m.getListImage()!=null) {
				List<AnhChat> ag= new ArrayList<AnhChat>();
				m.getListImage().forEach(d->{
					AnhChat a= new AnhChat();
					a.setDuongDan(d);
					a.setTinNhan(t);
					anhChatService.save(a);
					ag.add(a);
				});
				t.setAnhChat(ag);
			}
			Message mess= Message.getMessgaeFromTinNhan(t);
			senToKhachHang(mess, idd);
			senToKhachNhanVien(mess);
		}
		if (me instanceof Message && role.equals("EMPLOYEE")) {
			Message m = (Message) me;
			if (m.getNoiDungTinNhan() == null || m.getNoiDungTinNhan().equals("")) {
			}
			TinNhan t = new TinNhan();
			t.setNoiDungTinNhan(m.getNoiDungTinNhan());
			Long id = (Long) session.getAttributes().get("id");
			if(m.getRe()!=0) {
				TinNhan h= tinhNhanService.getById(m.getRe());
				t.setReply(h);
			}
			NhanVien nn=(NhanVien)nguoiDungService.getById(id);
			KhachHang kk=(KhachHang)nguoiDungService.getById(m.getKhachHang());
			t.setKhachHang(kk);
			boolean tn=userOnline.danhSachKhachHang.containsKey(kk.getId());
			if(tn==true) {
				t.setStatusKhachHang(1);
			}
			else {
				t.setStatusKhachHang(0);
			}
			t.setNhanVien(nn);
			t.setStatusNhanVien(2);
			tinhNhanService.save(t);
			if(m.getListImage()!=null) {
				List<AnhChat> ag= new ArrayList<AnhChat>();
				m.getListImage().forEach(d->{
					AnhChat a= new AnhChat();
					a.setDuongDan(d);
					a.setTinNhan(t);
					anhChatService.save(a);
					ag.add(a);
				});
				t.setAnhChat(ag);
			}
			Message mess= Message.getMessgaeFromTinNhan(t);
			senToKhachHang(mess, kk.getId());
			senToKhachNhanVien(mess);
		}
		if(me instanceof DropHeart) {
			
			DropHeart d= (DropHeart)me;
			TinNhan t = tinhNhanService.getById(d.getTinNhanDuocTim());
			t.setDaTim(!t.isDaTim());
			tinhNhanService.save(t);
			
			
			Set<Long> danhSachGui = userOnline.phanCongChat.get(t.getKhachHang().getId());
			if(danhSachGui==null) {
				danhSachGui= new HashSet<Long>();
			}
			d.setActive(!t.isDaTim());
			d.setType(MessageType.DROPHEART);
			senToKhachHang(d,t.getKhachHang().getId());
			senToKhachNhanVienFilter(me, danhSachGui);
		}
		if (me instanceof MoutUserChat) {
		    MoutUserChat m = (MoutUserChat) me;
		   if(userOnline.phanCongChat.containsKey((long)m.getIdUser())) {
			   Set<Long> l = userOnline.phanCongChat.get((long)m.getIdUser());
			    if (m.isMount()) {
			        if (l == null) {
			            l = new HashSet<>();
			            userOnline.phanCongChat.put((long)m.getIdUser(), l); 
			        }
			        // check các tin nhắn chưa xem
			        List<TinNhan> th= tinhNhanService.getTinNhanByKhachHangAndStatus(
			        		(long)m.getIdUser(),Arrays.asList(0,1), true);
			        List<Long> gh= th.stream().map(d->{
			        	d.setStatusNhanVien(2);
			        	tinhNhanService.save(d);
			        	return d.getId();
			        }).collect(Collectors.toList());
			        ViewMessage vi= new ViewMessage();
			        vi.setTinNhan(gh);
			        vi.setType(MessageType.VIEWMESSGE);
			        vi.setTypeView(2);
			        if(gh.size()!=0) {
			         senToKhachHang(vi,(long)m.getIdUser());
			        }
			        l.add(idd);
			        
			        // mơis thêm ở đay 
			        MoutUserChat mm= new MoutUserChat();
		            mm.setMount(true);
		            mm.setType(MessageType.MOUNTUSER);
		            
		            mm.setDanhSachDangChat(l.stream().map(d->{
		            	NguoiDung nn= nguoiDungService.getById(d);
		            	return nn.getTen();
		            }).collect(Collectors.toList()));
		            senToKhachNhanVienFilter(mm, l);
			    } else {
			    	System.out.println("đi vvoo đay");
			        if (l != null) {
			            l.remove(idd);
			            
			            MoutUserChat mm = new MoutUserChat();
			            mm.setMount(true);
			            mm.setType(MessageType.MOUNTUSER);

			            // Thay vì set vào m, phải set vào mm
			            mm.setDanhSachDangChat(
			                l.stream().map(d -> {
			                    NguoiDung nn = nguoiDungService.getById(d);
			                    return nn.getTen();
			                }).collect(Collectors.toList())
			            );

			            senToKhachNhanVienFilter(mm, l);
			        }
			        
			        
			    }
		   }
		}

		if(me instanceof ViewMessage) {
			
			ViewMessage v= (ViewMessage)me;
			
			if((v.getTinNhan()==null|| v.getTinNhan().size()==0) && n instanceof KhachHang) {
				List<TinNhan> th= tinhNhanService.getTinNhanByKhachHangAndStatusKhach(
		        		n.getId(),Arrays.asList(0,1));
		        List<Long> gh= th.stream().map(d->{
		        	d.setStatusKhachHang(2);
		        	tinhNhanService.save(d);
		        	return d.getId();
		        }).collect(Collectors.toList());
		        ViewMessage vi= new ViewMessage();
		        vi.setTinNhan(gh);
		        vi.setType(MessageType.VIEWMESSGE);
		        vi.setTypeView(2);
		        if(gh.size()!=0) {
		        	Set<Long> ds= userOnline.phanCongChat.get(n.getId());
					if(ds!=null) {
						senToKhachNhanVienFilter(vi, ds);
					}
		        }
		        return;
			}
			TinNhan t= tinhNhanService.getById(v.getTinNhan().get(0));
			t.setStatusKhachHang(2);
			tinhNhanService.save(t);
			v.setTypeView(2);
			v.getTinNhan().add(t.getId());
			v.setType(MessageType.VIEWMESSGE);
			Set<Long> ds= userOnline.phanCongChat.get(n.getId());
			if(ds!=null) {
				senToKhachNhanVienFilter(v, ds);
			}
		}
		if(me instanceof UserAcTivity) {
				UserAcTivity d= (UserAcTivity)me;
				d.setType(MessageType.USERACTIVITY);
				if(n instanceof NhanVien) {
					senToKhachHang(d, d.getUserBiTacDong());
				}
				else {
					Set<Long> ds= userOnline.phanCongChat.get(n.getId());
					if(ds!=null) {
						senToKhachNhanVienFilter(me, ds);
					}
					
				}
				
		}
		
		
		
		
		
		
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
		Long userId = (Long) session.getAttributes().get("id");
		String role = (String) session.getAttributes().get("role");
		if (role.equals("CUSTOMER")) {
			UserOnline.phanCongChat.remove(userId);
			UserOnline.danhSachKhachHang.remove(userId);
			UserAcTivity u= new UserAcTivity();
			u.setActivity(0);
			u.setIdUser((long)session.getAttributes().get("id"));
			u.setType(MessageType.USERACTIVITY);
			senToKhachNhanVien(u);
		}
		if (role.equals("EMPLOYEE")) {
			UserOnline.danhSachNhanVien.remove(userId);
		}
		
		
		
	}

	public void senToKhachHang(chatRoot c, Long idKhachHang) {
		WebSocketSession w = UserOnline.danhSachKhachHang.get(idKhachHang);
		
		if (w != null) {
			try {
				w.sendMessage(new TextMessage(objectMapper.writeValueAsString(c)));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void senToKhachNhanVien(chatRoot c) {
		for (Map.Entry<Long, WebSocketSession> entry : userOnline.danhSachNhanVien.entrySet()) {
	
		    WebSocketSession se = entry.getValue();
		    try {
				se.sendMessage(new TextMessage(objectMapper.writeValueAsString(c)));
			} catch (Exception e) {
				e.printStackTrace();
			} 

		}
		
	 }
//	public void senToKhachNhanVienFilter(chatRoot c,Set<Long> danhSach) {
//		danhSach.forEach(d->{
//			WebSocketSession w= userOnline.danhSachNhanVien.get(d);
//			if(w!=null) {
//				try {
//					w.sendMessage(new TextMessage(objectMapper.writeValueAsString(c)));
//				} catch (Exception e) {
//					System.out.println("LỖI Ở ĐÂY 2");
//					e.printStackTrace();
//				}
//			}
//		});
//		
//	 }
	public void senToKhachNhanVienFilter(chatRoot c, Set<Long> danhSach) {
		System.out.println("số lượng phần tử danh sách : "+danhSach.size());
	    danhSach.forEach(d -> {
	        WebSocketSession w = userOnline.danhSachNhanVien.get(d);
	        if (w != null && w.isOpen()) {
	            synchronized (w) {
	                try {
	                    w.sendMessage(new TextMessage(objectMapper.writeValueAsString(c)));
	                } catch (Exception e) {
	                    e.printStackTrace();
	                }
	            }
	        }
	    });
	}



}
