package com.example.e_commerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
@SpringBootApplication
public class ECommerceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ECommerceApplication.class, args);
		Security.addProvider(new BouncyCastleProvider());
	}

}
