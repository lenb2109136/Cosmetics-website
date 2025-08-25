package com.example.e_commerce.config.websocket;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;

import com.example.e_commerce.Exception.Custom.GeneralException;
import com.example.e_commerce.model.KhachHang;
import com.example.e_commerce.model.NguoiDung;
import com.example.e_commerce.service.NguoiDungService;

@Configuration
@EnableWebSocket
public class WebsocketConfigruration implements WebSocketConfigurer {
	@Autowired
	HansakeUpgrade hansakeUpgrade;
	
	 @Autowired
	    private HandleChat handleChat;
	 @Override
	    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
	        registry.addHandler(handleChat, "/ws") 
	        .addInterceptors(hansakeUpgrade)
	        .setAllowedOrigins("*"); 
	    }
    
}