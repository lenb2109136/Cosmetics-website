package com.example.e_commerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.e_commerce.model.BienTheDongGoiChan;
import com.example.e_commerce.model.embeded.EBienTheDongGoiChan;

import io.lettuce.core.dynamic.annotation.Param;

@Repository
public interface BienTheDongGoiRepository extends JpaRepository<BienTheDongGoiChan, EBienTheDongGoiChan>{

	
	@Query(value = "SELECT * FROM bienthequycachdonggoi WHERE MAVACH=:mv",nativeQuery = true )
	public BienTheDongGoiChan getBienTheDongGoiChanByMaVach(@Param("mv") String mv) ;
	}

