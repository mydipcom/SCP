package com.missionsky.scp.quartz;

import java.io.IOException;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.missionsky.scp.dao.ScheduleDao;
import com.missionsky.scp.dataanalysis.Constants;
import com.missionsky.scp.dataanalysis.entity.StandardTask;
import com.missionsky.scp.dataanalysis.facadeinterface.BasicTaskAssemblyLine;

public class SimpleJob implements Job {
	private StandardTask task;

	@Override
	public void execute(JobExecutionContext context) {
		JobDataMap dataMap = context.getTrigger().getJobDataMap();
		task = (StandardTask) dataMap.get("task");
		if (task != null) {
			System.out.println("taskName:" + task.getName());

			try {

				if (task.getAssembly() != null && !task.getAssembly().equals("")) {
					if (task.getAssembly().equals("BasicTaskAssemblyLine")) {
						
						BasicTaskAssemblyLine basicTaskAssemblyLine = new BasicTaskAssemblyLine();
						basicTaskAssemblyLine.stream(task);
						
					} else {

					}
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block

				e.printStackTrace();
			}

		}
	}

	public StandardTask getTask() {
		return task;
	}

	public void setTask(StandardTask task) {
		this.task = task;
	}
}
