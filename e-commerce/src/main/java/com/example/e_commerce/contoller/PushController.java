package com.example.e_commerce.contoller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.e_commerce.model.NguoiDung;
import com.example.e_commerce.model.Subscription;
import com.example.e_commerce.repository.SubscriptionRepository;
import com.example.e_commerce.service.JwtService;
import com.example.e_commerce.service.NguoiDungService;
import com.example.e_commerce.service.PushNotificationService;

@RestController
@RequestMapping( "/api/pushnotifycation")
 public class PushController {
	@Autowired
    private SubscriptionRepository repo;
	
	@Autowired
	private PushNotificationService pushNotificationService;
	
	@Autowired
	private SubscriptionRepository subscriptionRepository;
	
	@Autowired
	private NguoiDungService nguoiDungService;
	
	@Autowired
	private JwtService jwtService;

    @PostMapping("/save-subscription")
    public ResponseEntity<String> saveSubscription(@RequestBody Map<String, Object> sub) {
        System.out.println("đã đi vào lưu");
    	Map<String, String> keys = (Map<String, String>) sub.get("keys");
        Subscription ps = new Subscription();
        List<com.example.e_commerce.model.Subscription> subscriptions=subscriptionRepository.getByEndpoint((String)sub.get("endpoint"));
		

        if(subscriptions!=null && subscriptions.size()!=0) {
			return ResponseEntity.ok("Saved");
		}
        System.out.println("xuống tới dưới đây: "+jwtService.getIdUser());
        try {
			NguoiDung nguoiDung= nguoiDungService.getById(jwtService.getIdUser());
			if(nguoiDung!=null) {
				ps.setNguoiDung(nguoiDung);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
       
        ps.setEndpoint((String) sub.get("endpoint"));
        ps.setP256dh(keys.get("p256dh"));
        ps.setAuth(keys.get("auth"));
        
        repo.save(ps);
        return ResponseEntity.ok("Saved");
    }
    
    @PostMapping("/send")
    public ResponseEntity<String> sendNotification(@RequestBody Map<String, String> body) {
        try {
            pushNotificationService.sendPush(
                    body.get("endpoint"),
                    body.get("p256dh"),
                    body.get("auth"),
                    body.get("message")
            );
            return ResponseEntity.ok("Push notification sent!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}
