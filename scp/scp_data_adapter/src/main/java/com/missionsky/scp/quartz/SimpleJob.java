package com.missionsky.scp.quartz;

import java.io.IOException;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;


import com.missionsky.scp.dataadapter.dao.StandardFileDao;
import com.missionsky.scp.dataadapter.datafetcher.DataFetcherDispenser;
import com.missionsky.scp.dataadapter.entity.AdapterTask;
import com.missionsky.scp.dataadapter.entity.StandardFile;


public  class SimpleJob implements Job {

	private AdapterTask task;


	
	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		JobDataMap dataMap = context.getTrigger().getJobDataMap();
		task = (AdapterTask) dataMap.get("adapterTask");
		/*
		if (task != null) {
			Taskline taskline=new Taskline();
			taskline.gainTask(task);
		}
		*/
		StandardFile file = new StandardFile();
		Document doc=null;
		StandardFileDao standardfiledao=new StandardFileDao();
		if (task != null) {
			System.out.println("taskName:" + task.getTaskName());

				
				try {
					file=standardfiledao.getFileByRowKey(task.getFileName());
				} catch (IOException e1) {
					e1.printStackTrace();
				}		
			try {
					doc = DocumentHelper.parseText(file.getContent());
				} catch (DocumentException e) {	
					e.printStackTrace();
				}
			DataFetcherDispenser dataFetcherDispenser = new DataFetcherDispenser();
			dataFetcherDispenser.gainParsedData(doc);
	}
	}
	public static void main(String arg[]){
		SimpleJob job=new SimpleJob();
		try {
			job.execute(null);
		} catch (JobExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}


