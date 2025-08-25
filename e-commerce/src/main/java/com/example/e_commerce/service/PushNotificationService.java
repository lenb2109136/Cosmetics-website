package com.example.e_commerce.service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.jose4j.lang.JoseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.example.e_commerce.model.KhachHang;
import com.example.e_commerce.model.NguoiDung;
import com.example.e_commerce.repository.SubscriptionRepository;

import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import nl.martijndwars.webpush.Subscription;

@Service
public class PushNotificationService {
	
	@Value("${vapid.public.key}")
    private String PUBLIC_KEY;
	
	@Value("${vapid.private.key}")
    private String PRIVATE_KEY;
	
	@Autowired
	private SubscriptionRepository subscriptionRepository;
	
	public void sendPush(String endpoint, String p256dh, String auth, String message) 
            throws GeneralSecurityException, IOException, JoseException, ExecutionException, InterruptedException {
       
		Subscription.Keys keys = new Subscription.Keys(p256dh, auth);
        Subscription subscription = new Subscription(endpoint, keys);

        String payload = "{\"title\": \"Thông báo mới\", \"body\": \"" + message + "\"}";
        Notification notification = new Notification(subscription, payload);

        PushService pushService = new PushService()
                .setPublicKey(PUBLIC_KEY)
                .setPrivateKey(PRIVATE_KEY);

        pushService.send(notification);
    }
	
	@Async
	public void sendSubScription(String noiDungThongBao, KhachHang khachHang) {
	    List<com.example.e_commerce.model.Subscription> ds = 
	        subscriptionRepository.getAllSubByNguoiDung(khachHang.getId());

	    ds.forEach(d -> {
	        try {
	            sendPush(d.getEndpoint(), d.getP256dh(), d.getAuth(), noiDungThongBao);
	        } catch (GeneralSecurityException | IOException | JoseException |
	                 ExecutionException | InterruptedException e) {
	            
	            // In log cho dễ debug
	            System.err.println("Push failed, xóa subscription: " + d.getEndpoint());
	            e.printStackTrace();

	            // Xóa khỏi DB khi bị lỗi
	            try {
	                subscriptionRepository.delete(d);
	            } catch (Exception ex) {
	                ex.printStackTrace();
	            }
	        }
	    });
	}

}
