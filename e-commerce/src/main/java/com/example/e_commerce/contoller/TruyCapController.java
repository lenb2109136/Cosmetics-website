package com.example.e_commerce.contoller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.e_commerce.constants.Routes;
import com.example.e_commerce.model.DongGoiChan;
import com.example.e_commerce.model.DongHop;
import com.example.e_commerce.model.NenDa;
import com.example.e_commerce.model.Step;
import com.example.e_commerce.repository.DongGoiChanRepository;
import com.example.e_commerce.repository.DongHopRepository;
import com.example.e_commerce.repository.StepRepository;
import com.example.e_commerce.service.DongHopService;
import com.example.e_commerce.service.MomoService;
import com.example.e_commerce.service.NenDaService;
import com.example.e_commerce.service.OrderClassSchedule;
import com.example.e_commerce.service.ServiceLocator;
import com.example.e_commerce.service.TruyCapService;

import io.lettuce.core.dynamic.annotation.Param;

@RestController
@RequestMapping(Routes.API+ "/truycap")
public class TruyCapController {
	
	@Autowired
	private TruyCapService truyCapService;
	
	@Autowired
	private NenDaService nenDaService;
	
	@Autowired
	private StepRepository stepRepository;
	
	@Autowired
	private DongHopRepository dongHopService;
	
	@Autowired
	private DongGoiChanRepository dongGoiChanRepository;
	
	
	@GetMapping("truycap/{id}")
	public void addTruyCap(@PathVariable("id") long id) {
		truyCapService.add(id);
	}
	@GetMapping("getall")
	public List<NenDa> getAllNenDa() {
		return nenDaService.getAll();
	}
	
	@GetMapping("getallstep")
	public List<Step> getAllStep() {
		return stepRepository.findAll();
	}
	@GetMapping("getall-dong-hop")
	public List<DongHop> getAllDongHop() {
		return dongHopService.findAll();
	}
	
	@GetMapping("getall-donggoi")
	public List<DongGoiChan> getAllDongGoiNhap() {
		return dongGoiChanRepository.findAll().stream().filter(d->d.getId()!=4).collect(Collectors.toList());
}
	
	
	
	
	
	
}
