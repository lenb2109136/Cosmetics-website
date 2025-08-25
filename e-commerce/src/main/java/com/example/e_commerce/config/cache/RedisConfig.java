package com.example.e_commerce.config.cache;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;



@Configuration
public class RedisConfig {
	@Bean
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory){
		RedisTemplate<String, Object> redistemplate= new RedisTemplate<String, Object>();
		redistemplate.setConnectionFactory(redisConnectionFactory);
		redistemplate.setKeySerializer(new StringRedisSerializer());
		Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
		redistemplate.setValueSerializer(serializer);
		redistemplate.setHashKeySerializer(new StringRedisSerializer());
		redistemplate.setHashValueSerializer(serializer);
		redistemplate.afterPropertiesSet();
		return redistemplate;
	}
}
