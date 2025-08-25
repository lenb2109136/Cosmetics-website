package com.example.e_commerce.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.e_commerce.model.AnhChat;
import com.example.e_commerce.repository.AnhChatRepository;

@Service
public class AnhChatService {
	@Autowired
	private AnhChatRepository anhChatRepository;
	@Transactional
	public void save (AnhChat c) {
		anhChatRepository.save(c);
	}
}
