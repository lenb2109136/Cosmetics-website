package com.example.e_commerce.service;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderJobSchedule implements Job{
	
	@Autowired
	private HoaDonSerVice hoaDonSerVice;
@Override
public void execute(JobExecutionContext context) throws JobExecutionException {
	long id = context.getMergedJobDataMap().getLong("id");
	hoaDonSerVice.HuyDon2(id);
}
}
