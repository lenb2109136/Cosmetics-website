package com.example.e_commerce.repository;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.e_commerce.model.AnhChat;

@Repository
public interface AnhChatRepository extends JpaRepository<AnhChat, Long>{

}
