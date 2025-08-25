package com.example.e_commerce.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.e_commerce.model.NenDa;
import com.example.e_commerce.repository.NenDaREposotory;

@Service
public class NenDaService {
@Autowired
NenDaREposotory nenDaREposotory;

public List<NenDa> getAll(){
	return nenDaREposotory.findAll();
}
}
