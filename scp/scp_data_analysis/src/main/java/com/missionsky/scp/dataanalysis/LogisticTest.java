package com.missionsky.scp.dataanalysis;
import java.io.IOException;
import java.util.Arrays;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.mahout.classifier.df.mapreduce.BuildForest;
import org.apache.mahout.classifier.df.mapreduce.TestForest;
import org.apache.mahout.classifier.df.tools.Describe;
import org.apache.mahout.classifier.sgd.RunLogistic;
import org.apache.mahout.classifier.sgd.TrainLogistic;
import org.apache.mahout.common.HadoopUtil;

import com.missionsky.scp.dataanalysis.utils.RemoteHadoopUtil;


public class  LogisticTest{
	public static void main(String args[]){
		RemoteHadoopUtil.setConf(LogisticTest.class, Thread.currentThread(), "/hadoop");
		String[] arg=new String[]{"--input", "donut.csv","--output","logisticmodel","--target","color","--predictors","x","y","--features", "20","--passes","100" ,"--rate","50","--categories","2","--types" ,"numeric"};
		
		String[] arg1=new String[]{"--input","donut.csv", "--model","logisticmodel"};
    try {
			//HadoopUtil.delete(new Configuration(), new Path("/Forest/out-testforest0"));
			
    	    TrainLogistic.main(arg);
    	    RunLogistic.main(arg1);
		} catch (IllegalArgumentException e) {
			
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
       
	}

}
