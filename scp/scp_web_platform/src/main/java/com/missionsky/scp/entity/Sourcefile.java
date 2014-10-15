package com.missionsky.scp.entity;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Sourcefile implements Serializable{
	
	private String rowKey;
	
	private String sourceName;
	
	private String content;
	
	private String sourceType;
	
	private String StorageTime;
	
	public String getRowKey() {
		return rowKey;
	}

	public void setRowKey(String rowKey) {
		this.rowKey = rowKey;
	}

    public String getSourceName() {
		return sourceName;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

    
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	public String getStorageTime() {
		return StorageTime;
	}

	public void setStorageTime(String storageTime) {
		StorageTime = storageTime;
	}
	
}
