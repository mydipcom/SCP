package com.missionsky.scp.quartz;

import java.io.IOException;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;


import com.missionsky.scp.dataadapter.dao.AdapterstatusDao;



public class MyJobListener implements JobListener{
	
	
	public static final int jobToBeExecuted = 1;
	
	public static final int jobExecutionVetoed = 2;
	
	public static final int jobWasExecuted = 3;

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		 return "job_listener";
	}

	@Override
	public void jobToBeExecuted(JobExecutionContext context) {
		// TODO Auto-generated method stub
		System.out.println("jobToBeExecuted"+context.getJobDetail().getKey().getName());
		try {
			AdapterstatusDao.getInstance().updateScheduleRecord(context.getJobDetail().getKey().getName(), jobToBeExecuted);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void jobExecutionVetoed(JobExecutionContext context) {
		// TODO Auto-generated method stub
		System.out.println("jobExecutionVetoed"+context.getJobDetail().getKey().getName());
		try {
			AdapterstatusDao.getInstance().updateScheduleRecord(context.getJobDetail().getKey().getName(), jobExecutionVetoed);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void jobWasExecuted(JobExecutionContext context,
			JobExecutionException jobException) {
		// TODO Auto-generated method stub
		System.out.println("jobWasExecuted"+context.getJobDetail().getKey().getName());
		try {
			AdapterstatusDao.getInstance().updateScheduleRecord(context.getJobDetail().getKey().getName(),jobToBeExecuted);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
