package com.example.e_commerce.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class CacheService {
	
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	
	public Object getKey(String key) {
		return redisTemplate.opsForValue().get(key);
	}
}
