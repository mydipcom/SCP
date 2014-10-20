package com.missionsky.scp.quartz;
import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.CronScheduleBuilder.weeklyOnDayAndHourAndMinute;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.JobListener;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;

import com.missionsky.scp.dataadapter.entity.AdapterTask;
public class SimpleQuartz {

	public static void runDaily(String name, String group, Date date,
			Integer separate, AdapterTask task) throws SchedulerException {
		
		
		
		
		
	}

	public static void runWeek(String rowKey, String group, Date parse,
			Integer weekday, AdapterTask adapterTask) throws SchedulerException {
		
		
	}

	public static void runOneTime(String rowKey, String group, Date parse,
			AdapterTask adapterTask) throws SchedulerException{
		SchedulerFactory schedFact = new StdSchedulerFactory();
		Scheduler sched = schedFact.getScheduler();
		
		JobDetail job = newJob(SimpleJob.class).withIdentity(rowKey, group)
				.build();
		JobDataMap dataMap = new JobDataMap();
		dataMap.put("adapterTask", adapterTask);
		SimpleTrigger trigger = (SimpleTrigger) newTrigger()
				.withIdentity("trigger", "group").startAt(parse).forJob(job)
				.usingJobData(dataMap).build();
        sched.getListenerManager().addJobListener(new MyJobListener());
		sched.scheduleJob(job, trigger);
		sched.start();
		
		
		
	}

}
