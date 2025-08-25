package com.example.e_commerce.service;

import java.util.Map;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.e_commerce.model.HoaDon;

public class CallBackIpnService implements Job{
	@Autowired
	MomoService momoService;
	
	@Autowired
	HoaDonSerVice hoaDonSerVice;
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		long id = context.getMergedJobDataMap().getLong("id");
		try {
			Map<String, String> m= momoService.queryTransactionStatus(id+"");
			int resultCode = Integer.parseInt(m.getOrDefault("resultCode", "0"));
	        long transId = Long.parseLong(m.getOrDefault("transId", "0"));
	        long orderId = Long.parseLong(m.getOrDefault("orderId", "0"));
//	        hoaDonSerVice.listenMomo(orderId, transId, resultCode);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
