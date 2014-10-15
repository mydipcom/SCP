package com.missionsky.scp.dataanalysis.entity;


import java.util.HashMap;
import java.util.List;

import com.missionsky.scp.entity.Action;

/**
 * Standard task entity class
 * @author ellis.xie
 * @version 1.0
 */

public class StandardTask {
	//
	private String rowKey;
	// task name
	private String name;
	
	// task acton list
	private List<Action> acions;
	
	//task assembly
	private String assembly;
	
	// standard file name
	private String standardFile;
	
	// configuration file name map(key:intput path,value:configuration file name)
	private HashMap<String, String> confPathMap;
    
	
	public String getRowKey() {
		return rowKey;
	}

	public void setRowKey(String rowKey) {
		this.rowKey = rowKey;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


   public String getStandardFile() {
		return standardFile;
	}

	public void setStandardFile(String standardFile) {
		this.standardFile = standardFile;
	}

	public HashMap<String, String> getConfPathMap() {
		return confPathMap;
	}

	public void setConfPathMap(HashMap<String, String> confPathMap) {
		this.confPathMap = confPathMap;
	}



	public List<Action> getAcions() {
		return acions;
	}

	public void setAcions(List<Action> acions) {
		this.acions = acions;
	}

	public String getAssembly() {
		return assembly;
	}

	public void setAssembly(String assembly) {
		this.assembly = assembly;
	}
	
}
