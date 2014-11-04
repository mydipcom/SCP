package com.missionsky.scp.dataanalysis.facadeinterface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.mahout.cf.taste.common.TasteException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.missionsky.scp.dao.ScheduleDao;
import com.missionsky.scp.dao.SourceDao;
import com.missionsky.scp.dataanalysis.Constants;
import com.missionsky.scp.dataanalysis.algorithm.BasicAlgorithm;
import com.missionsky.scp.dataanalysis.algorithm.dataaggregate.StoreToHBaseAlgorithm;
import com.missionsky.scp.dataanalysis.entity.StandardFile;
import com.missionsky.scp.dataanalysis.entity.StandardTask;
import com.missionsky.scp.dataanalysis.utils.HbaseUtil;
import com.missionsky.scp.dataanalysis.utils.PropertiesUtil;
import com.missionsky.scp.dataanalysis.utils.RemoteHadoopUtil;
import com.missionsky.scp.entity.Action;

/**
 * Task Assembly Line
 * @author ellis.xie 
 * @version 1.0
 */

public class BasicTaskAssemblyLine extends Assembly{
	
	private Logger logger = LoggerFactory.getLogger(BasicTaskAssemblyLine.class);
	
	public void stream(StandardTask task){
		
		
		RemoteHadoopUtil.setConf(BasicTaskAssemblyLine.class, Thread.currentThread(), "/hadoop");
		
		Boolean firstStep = true;
		Configuration conf = new Configuration();
		ArrayList<String> tempIntput = new ArrayList<>();
		BasicAlgorithm algorithm = null;
		String algorithmTaskOutput = null;
		int algorithmResult = Constants.ALGORITHM_RESULT_SUCCESS;
		String taskName = task.getName();
		
		try {
			
			StandardFile standardFile = new StandardFile(task.getStandardFile());
			
			for (Action action : task.getAcions()) {
				
				algorithm = (BasicAlgorithm) Class.forName(action.getPathName()).newInstance();
				algorithmTaskOutput = "/Task_Out/" + taskName+"_"+task.getRowKey() + "/" + algorithm.getClass().getSimpleName();
				if (firstStep) {
					conf.set("mapreduce.jobtracker.address", getJobTracker());
					algorithmResult = algorithm.run(conf,algorithmTaskOutput, task,action.getInputpaths());
					
					if (algorithmResult == Constants.ALGORITHM_RESULT_SUCCESS) {
						firstStep=false;
					    tempIntput.add(algorithmTaskOutput+"/part-r-00000");
						} else {
						
						throw new InterruptedException("the algorithm "+ action.getName() +" in the task " + taskName + " fail");
					}
				} else {
				    algorithmResult = algorithm.run(conf, tempIntput, algorithmTaskOutput, task, taskName);
					
					if (algorithmResult == Constants.ALGORITHM_RESULT_SUCCESS) {
						tempIntput.clear();
						tempIntput.add(algorithmTaskOutput+"/part-r-00000");
						} else {
						throw new InterruptedException("the algorithm "+ action.getName() +" in the task " + taskName + " fail.");
					}
				}
			}
			
			// store parsed data to HBase.
			HbaseUtil.CreateHBaseTable(taskName+"_"+task.getRowKey(), standardFile.getColumnNames());
			algorithm = new StoreToHBaseAlgorithm();
			conf = HBaseConfiguration.create();
			conf.set("TABLE_NAME", taskName+"_"+task.getRowKey());
			conf.setStrings("COLUMN_NAMES",  (String[]) standardFile.getColumnNames().toArray(new String[standardFile.getColumnNames().size()]));
			
			conf.set("mapreduce.jobtracker.address", getJobTracker());
			
			algorithmResult = algorithm.run(conf, tempIntput, null, task,  taskName+"_"+task.getRowKey());
			if(algorithmResult == Constants.ALGORITHM_RESULT_SUCCESS){
				try {
					SourceDao.saveSourceFile(taskName+"_"+task.getRowKey(), "Preprocess Data");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else if (algorithmResult == Constants.ALGORITHM_RESULT_FAIL) {
				throw new InterruptedException("the algorithm StoreToHBaseAlgorithm in the task " + taskName + " fail.");
			}
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			logger.error("Unexpected interrupted:{}",e.getMessage());
		} catch (InstantiationException e) {
			logger.error("Unexpected interrupted:{}",e.getMessage());
		} catch (IllegalAccessException e) {
			logger.error("Unexpected interrupted:{}",e.getMessage());
		} catch (InterruptedException e) {
			logger.error("Unexpected interrupted:{}",e.getMessage());
		}
		
		
	}
	
	public String getJobTracker(){
		Properties properties = PropertiesUtil.loadPropertiesFile(BasicTaskAssemblyLine.class.getResource("/").getPath() + "/hadoop.properties");
		return (String) properties.get("mapreduce.jobtracker.address");
	}
	

}
