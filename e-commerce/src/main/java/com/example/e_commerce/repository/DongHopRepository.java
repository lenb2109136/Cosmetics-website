package com.example.e_commerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.e_commerce.model.DongHop;

@Repository
public interface DongHopRepository extends JpaRepository<DongHop, Integer>{

}
