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

public class SpecialTaskAssemblyLine extends Assembly{
	private Logger logger=LoggerFactory.getLogger(SpecialTaskAssemblyLine.class);
	
	public void stream(StandardTask task) {
		RemoteHadoopUtil.setConf(SpecialTaskAssemblyLine.class, Thread.currentThread(), "/hadoop");
		
		Boolean firstStep=true;
		Configuration conf=new Configuration();
		ArrayList<String> tempInput = new ArrayList<String>();
		BasicAlgorithm algorithm = null;
		String algorithmTaskOut = null;
		int algorithmResult=Constants.ALGORITHM_RESULT_SUCCESS;
		int i=0;
		try{
			StandardFile standardFile=new StandardFile(task.getStandardFile());
			for(Action action:task.getAcions()){
				algorithm=(BasicAlgorithm) Class.forName(action.getPathName()).newInstance();
				algorithmTaskOut="/Task_Out/"+task.getName()+"_"+task.getRowKey()+"/"+algorithm.getClass().getSimpleName();
				conf.set("mapreduce.jobtracker.address", getJobTracker());
				if(i<task.getAcions().size()-1){
					algorithmResult=algorithm.run(conf, algorithmTaskOut, task, action.getInputpaths());
					if(algorithmResult == Constants.ALGORITHM_RESULT_SUCCESS){
						tempInput.add(algorithmTaskOut+"/part-r-00000");
					}else{
						throw new InterruptedException("the algorithm "+ action.getName() +" in the task " + task.getName() + " fail");
					}
				}else{
					algorithmResult=algorithm.run(conf, tempInput, algorithmTaskOut, task, task.getName());
					if(algorithmResult == Constants.ALGORITHM_RESULT_SUCCESS){
						algorithmTaskOut=algorithmTaskOut+"/part-r-00000";
						tempInput.clear();
						tempInput.add(algorithmTaskOut);
					}else{
						throw new InterruptedException("the algorithm "+ action.getName() +" in the task " + task.getName() + " fail");
					}
				}
				
				i++;
			}
			
			//store to hbase
			HbaseUtil.CreateHBaseTable(task.getName()+"_"+task.getRowKey(), standardFile.getColumnNames());
			algorithm=new StoreToHBaseAlgorithm();
			conf = HBaseConfiguration.create();
			conf.set("TABLE_NAME",task.getName()+"_"+task.getRowKey());
			conf.setStrings("COLUMN_NAMES", standardFile.getColumnNames().toArray(new String[standardFile.getColumnNames().size()]));
			conf.set("mapreduce.jobtracker.address", getJobTracker());
			algorithmResult=algorithm.run(conf,tempInput , null, task, task.getName()+"_"+task.getRowKey());
			if(algorithmResult == Constants.ALGORITHM_RESULT_SUCCESS){
				try {
					SourceDao.saveSourceFile(task.getName()+"_"+task.getRowKey(), "Preprocess Data");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else if (algorithmResult == Constants.ALGORITHM_RESULT_FAIL) {
				throw new InterruptedException("the algorithm StoreToHBaseAlgorithm in the task " + task.getName() + " fail.");
			}
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public String getJobTracker(){
		Properties properties = PropertiesUtil.loadPropertiesFile(BasicTaskAssemblyLine.class.getResource("/").getPath() + "/hadoop.properties");
		return (String) properties.get("mapreduce.jobtracker.address");
	}

}
