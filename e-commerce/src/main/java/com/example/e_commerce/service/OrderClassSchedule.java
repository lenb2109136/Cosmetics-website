package com.example.e_commerce.service;

import java.sql.Date;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderClassSchedule {
	@Autowired
	private Scheduler scheduler;

	public void scheduleJob(Long id, int delayInSeconds) throws SchedulerException {
        String jobName = "removeaftertime_" + id;
        String triggerName = "trigger_" + id;

        JobKey jobKey = new JobKey(jobName, "group1");
        TriggerKey triggerKey = new TriggerKey(triggerName, "group1");

        if (scheduler.checkExists(jobKey)) {
            scheduler.deleteJob(jobKey);
        }

        JobDetail jobDetail = JobBuilder.newJob(OrderJobSchedule.class)
            .withIdentity(jobKey)
            .usingJobData("id", id) 
            .build();

        Date startTime = new Date(System.currentTimeMillis() + delayInSeconds * 1000L);

        Trigger trigger = TriggerBuilder.newTrigger()
            .withIdentity(triggerKey)
            .startAt(startTime)
            .build();

        scheduler.scheduleJob(jobDetail, trigger);
        System.out.println("thành công tạo ");
    }
	
	public void callBackIpn(Long id, int delayInSeconds) throws SchedulerException {
		System.out.println("đã vào đây tạo");
        String jobName = "callbackipn_" + id;
        String triggerName = "triggercallbackipn__" + id;

        JobKey jobKey = new JobKey(jobName, "group2");
        TriggerKey triggerKey = new TriggerKey(triggerName, "group2");

        if (scheduler.checkExists(jobKey)) {
            scheduler.deleteJob(jobKey);
        }

        JobDetail jobDetail = JobBuilder.newJob(CallBackIpnService.class)
            .withIdentity(jobKey)
            .usingJobData("id", id) 
            .build();

        Date startTime = new Date(System.currentTimeMillis() + delayInSeconds * 1000L);

        Trigger trigger = TriggerBuilder.newTrigger()
            .withIdentity(triggerKey)
            .startAt(startTime)
            .build();

        scheduler.scheduleJob(jobDetail, trigger);
        System.out.println("thành công tạo ");
    }


    public boolean deleteJobById(Long id) throws SchedulerException {
        JobKey jobKey = new JobKey("removeaftertime_" + id, "group1");
        System.out.println("thành công xóa");
        return scheduler.deleteJob(jobKey);
        
    }
}
