package com.example.e_commerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.e_commerce.model.Subscription;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription,Long>{
	
	@Query(value = "SELECT * FROM pushnotifycation p WHERE p.endpoint=:endpoint",nativeQuery = true)
	public List<Subscription> getByEndpoint(String endpoint);
	
	
	@Query(value = "SELECT * FROM pushnotifycation p WHERE p.ND_ID=:id",nativeQuery = true)
	public List<Subscription> getAllSubByNguoiDung(long id);
	
}
