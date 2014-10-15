package com.missionsky.scp.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.missionsky.scp.dao.ActionDao;
import com.missionsky.scp.dao.SourceDao;
import com.missionsky.scp.entity.Sourcefile;
import com.missionsky.scp.entity.StandardFile;



@Component
public class SourceService {
	private Logger logger=LoggerFactory.getLogger(SourceService.class);
	private static final String TABLE_NAME = "AdpaterTask";
	private static final String FAMILY = "task";
	private static final String[] QUALIFIERS = {"taskName","description","fileName","triggerType","startTime","time","weekday"};
	
	@Autowired
	private ActionDao actionDao;
    @Autowired
	private SourceDao sourcedao;
	
	@Autowired
	private FileService fileService;
	
	
	
	public List<Sourcefile> findBySourceName(Sourcefile sourcefile ) throws IOException{
		logger.info("select all Hbase Source :default pagesize 20");
		List<Sourcefile> sources = new ArrayList<Sourcefile>();
		if(sourcefile != null){
			sources = sourcedao.findAllsource(sourcefile.getSourceName());
		}else{
			sources = sourcedao.findAllsource("");
		}
		return sources;
		}

}
