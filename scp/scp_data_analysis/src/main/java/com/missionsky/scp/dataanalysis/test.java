package com.missionsky.scp.dataanalysis;

import java.util.ArrayList;
import java.util.List;


import org.apache.hadoop.mapred.YARNRunner;

import com.missionsky.scp.dataanalysis.algorithm.dataaggregate.DuplicateRemovalSimpleAlgorithm;
import com.missionsky.scp.dataanalysis.entity.StandardTask;
import com.missionsky.scp.dataanalysis.facadeinterface.BasicTaskAssemblyLine;
import com.missionsky.scp.dataanalysis.facadeinterface.SpecialTaskAssemblyLine;
import com.missionsky.scp.entity.Action;

public class test {
	
	public static void main(String [] args){
		
		StandardTask task = new StandardTask();
		ArrayList<String> algorithms = new ArrayList<String>();
		algorithms.add(DuplicateRemovalSimpleAlgorithm.class.getName());
		List <Action> actions=new ArrayList<Action>();
		Action action=new Action();
		Action action1=new Action();
		Action action2=new Action();
		List <String> inputpath=new ArrayList<String>();
		inputpath.add("/Fire2");
		inputpath.add("/Fire1");
		
		action.setName("DuplicateRemovalSimpleAlgorithmAlgorithm");
		action.setPathName("com.missionsky.scp.dataanalysis.algorithm.dataaggregate.DuplicateRemovalSimpleAlgorithm");
		action1.setName("CopyOfCopyOfDuplicateRemovalSimpleAlgorithm");
		action1.setPathName("com.missionsky.scp.dataanalysis.algorithm.dataaggregate.CopyOfCopyOfDuplicateRemovalSimpleAlgorithm");
		action2.setName("CopyOfDuplicateRemovalSimpleAlgorithm");
		action2.setPathName("com.missionsky.scp.dataanalysis.algorithm.dataaggregate.CopyOfDuplicateRemovalSimpleAlgorithm");
		action.setInputpaths(inputpath);
		action1.setInputpaths(inputpath);
		actions.add(action);
		actions.add(action1);
		actions.add(action2);
		task.setAcions(actions);
		//task.setAligorithms(algorithms);
		task.setStandardFile("9a476b5e45b74e30a991736f71d09ae4");
		
		task.setName("test3_specialTaskAssemblyLine");
		
		
		//BasicTaskAssemblyLine basicTaskAssemblyLine = new BasicTaskAssemblyLine();
		//basicTaskAssemblyLine.stream(task);
		
		SpecialTaskAssemblyLine specialTaskAssemblyLine = new SpecialTaskAssemblyLine();
		specialTaskAssemblyLine.stream(task);
	}
}
