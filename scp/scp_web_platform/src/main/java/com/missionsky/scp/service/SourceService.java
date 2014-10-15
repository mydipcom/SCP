package com.missionsky.scp.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.missionsky.scp.dao.ActionDao;
import com.missionsky.scp.dao.AdpaterDao;
import com.missionsky.scp.dao.SourceDao;
import com.missionsky.scp.entity.Action;
import com.missionsky.scp.entity.Source;
import com.missionsky.scp.entity.Sourcefile;
import com.missionsky.scp.entity.StandardFile;
import com.missionsky.scp.util.UUIDGenerator;



@Component
public class SourceService {
	private Logger logger=LoggerFactory.getLogger(SourceService.class);
	private static final String TABLE_NAME = "AdpaterTask";
	private static final String FAMILY = "task";
	private static final String[] QUALIFIERS = {"taskName","description","fileName","triggerType","startTime","time","weekday"};
	
	@Autowired
	private ActionDao actionDao;
	
	@Autowired
	private AdpaterDao  adpaterDao;
	
	@Autowired
	private SourceDao sourcedao;
	
	@Autowired
	private FileService fileService;
	
	public void saveTask(Source source) throws IOException{
		logger.info("save source task into hbase");
		String rowKey = source.getRowKey();
		if(rowKey == null || "".equals(rowKey.trim())){
			rowKey = UUIDGenerator.getUUID();
			source.setRowKey(rowKey);
		}else{
			List<String> rowKeys = actionDao.getrowkeys(rowKey);
			if(rowKeys != null && !rowKeys.isEmpty()){
				actionDao.deleteActions(rowKeys);
			}
		}
		adpaterDao.saveTask(source);
		List<Action> actions = source.getActions();
		if(actions != null && !actions.isEmpty()){
			actionDao.saveActions(actions, rowKey);
		}
	}
	
	public Source findTask(String rowKey) throws IOException{
		Source source  = adpaterDao.findTaskByRowKey(rowKey);
		if(source != null){
			List<Action> actions = actionDao.findActionsByTaskId(source.getRowKey());
			if(actions != null){
				source.setActions(actions);
			}
		}
		return source;
	}
	
	public void deleteTask(String rowKey) throws IOException{
		if(rowKey != null && !"".equals(rowKey.trim())){
			actionDao.deleteActions(actionDao.getrowkeys(rowKey)); //delete relate action msg
			adpaterDao.deleteTaskByRowKey(rowKey);
			logger.info("delete row which rowKey is " + rowKey + " success!");
		}
	}
	
	
	public List<Source> findByTaskName(Source searchtask) throws IOException {
		logger.info("select all task records:default pageSize 20");
		List<Source> sources = new ArrayList<Source>();
		if(searchtask != null){
			sources = adpaterDao.findAllTasks(searchtask.getTaskName());
		}else{
			sources = adpaterDao.findAllTasks("");
		}
		if(sources != null && !sources.isEmpty()){
			for(Source source:sources){
				String fileId = source.getFileName();
				if(fileId != null && !"".equals(fileId.trim())){
					StandardFile file = fileService.getFileByRowKey(fileId);
					if(file != null){
						source.setFileName(file.getName());
					}else{
						source.setFileName("");
					}
				}else{
					source.setFileName("");
				}
				List<Action> actions = actionDao.findActionsByTaskId(source.getRowKey());
				if(actions != null){
					source.setActions(actions);
				}
			}
		}
		return sources;
	}
	public List<Sourcefile> findBySourceName(Sourcefile sourcefile) throws IOException {
		logger.info("select all task records:default pageSize 20");
		List<Sourcefile> sources = new ArrayList<Sourcefile>();
		if(sourcefile != null){
			sources = sourcedao.findAllsource(sourcefile.getSourceName());
		}else{
			sources = sourcedao.findAllsource("");
		}
		return sources;
		}

}
