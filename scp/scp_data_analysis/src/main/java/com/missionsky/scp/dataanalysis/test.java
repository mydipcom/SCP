package com.missionsky.scp.dataanalysis;

import java.util.ArrayList;
import java.util.List;

import org.apache.cassandra.cli.CliParser.newColumnFamily_return;
import org.apache.hadoop.mapred.YARNRunner;

import com.missionsky.scp.dataanalysis.algorithm.dataaggregate.DuplicateRemovalSimpleAlgorithm;
import com.missionsky.scp.dataanalysis.entity.StandardTask;
import com.missionsky.scp.dataanalysis.facadeinterface.BasicTaskAssemblyLine;
import com.missionsky.scp.entity.Action;

public class test {
	
	public static void main(String [] args){
		
		StandardTask task = new StandardTask();
		ArrayList<String> algorithms = new ArrayList<String>();
		algorithms.add(DuplicateRemovalSimpleAlgorithm.class.getName());
		List <Action> actions=new ArrayList<Action>();
		Action action=new Action();
		List <String> inputpath=new ArrayList<String>();
		inputpath.add("/Fire");
		action.setName("DuplicateRemovalSimpleAlgorithm");
		action.setInputpaths(inputpath);
		actions.add(action);
		task.setAcions(actions);
		//task.setAligorithms(algorithms);
		task.setStandardFile("standard01.xml");
		
		task.setName("test_X");
		
		
		BasicTaskAssemblyLine basicTaskAssemblyLine = new BasicTaskAssemblyLine();
		basicTaskAssemblyLine.stream(task);
	}
}
