package com.missionsky.scp.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.missionsky.scp.dao.BasicDao;
import com.missionsky.scp.dao.HadoopFileUtil;
import com.missionsky.scp.dao.HbaseHelper;
import com.missionsky.scp.entity.Action;
import com.missionsky.scp.entity.Source;
import com.missionsky.scp.entity.Sourcefile;
import com.missionsky.scp.entity.StandardFile;
import com.missionsky.scp.util.UUIDGenerator;

@Component
public class FileService {
	private Logger logger = LoggerFactory.getLogger(FileService.class);
	private static final String TABLE_NAME = "standardFile";
	private static final String FAMILY = "file";
	private static final String[] QUALIFIERS = { "fileName","content","description","type"};
	
	@Autowired
	private HbaseHelper helper;
	
	@Autowired
	private HadoopFileUtil hdfs;
	
	@Autowired
	private BasicDao basicDao;
	
	public Map<String, String> getAllFiles() throws IOException{
		List<Result> list = helper.getRowResults(TABLE_NAME, FAMILY, QUALIFIERS, null, null);
		Map<String, String> map = new HashMap<String,String>();
		if(list != null && !list.isEmpty()){
			for(Result result:list){
				byte[] rowKey = result.getRow();
				byte[] fileName = result.getValue(Bytes.toBytes(FAMILY), Bytes.toBytes(QUALIFIERS[0]));
				if(rowKey != null && fileName != null){
					map.put(Bytes.toString(rowKey), Bytes.toString(fileName));
				}
			}
		}
		return map;
	}
	
	public Map<String, String> getFilesBytype(String type) throws IOException{
		Map<String, byte[]> values = new HashMap<String, byte[]>();
		values.put(QUALIFIERS[3], Bytes.toBytes(type));
		List<Result> list = helper.getRowResults(TABLE_NAME, FAMILY, QUALIFIERS, values, null);
		Map<String, String> map = new HashMap<String,String>();
		if(list != null && !list.isEmpty()){
			for(Result result:list){
				byte[] rowKey = result.getRow();
				byte[] fileName = result.getValue(Bytes.toBytes(FAMILY), Bytes.toBytes(QUALIFIERS[0]));
				if(rowKey != null && fileName != null){
					map.put(Bytes.toString(rowKey), Bytes.toString(fileName));
				}
			}
		}
		return map;
	}
	public StandardFile getFileByRowKey(String rowKey) throws IOException{
		Result result = helper.selectRowResult(TABLE_NAME, rowKey);
		if(result != null){
			StandardFile file = new StandardFile();
			byte[] name = result.getValue(Bytes.toBytes(FAMILY), Bytes.toBytes(QUALIFIERS[0]));
			if(name != null){
				file.setName(Bytes.toString(name));
			}
			byte[] content = result.getValue(Bytes.toBytes(FAMILY), Bytes.toBytes(QUALIFIERS[1]));
			if(content != null){
				file.setContent(Bytes.toString(content));
			}
			byte[] description = result.getValue(Bytes.toBytes(FAMILY), Bytes.toBytes(QUALIFIERS[2]));
			if(description != null){
				file.setDescription(Bytes.toString(description));
			}
			file.setRowKey(Bytes.toString(result.getRow()));
			return file;
		}
		return null;
	}
	
	public List<StandardFile> findAllFiles(String fileName) throws IOException{
		List<StandardFile> files = new ArrayList<StandardFile>();
		List<Result> list = new ArrayList<Result>();
		Map<String, byte[]> values = new HashMap<String, byte[]>();
		if(fileName != null && !"".equals(fileName.trim())){
			values.put(QUALIFIERS[0], Bytes.toBytes(fileName));
		}
		list = helper.getRowResults(TABLE_NAME, FAMILY, QUALIFIERS, values, null);
		if(list != null && !list.isEmpty()){
			for(Result result:list){
				StandardFile file = new StandardFile();
				byte[] name = result.getValue(Bytes.toBytes(FAMILY), Bytes.toBytes(QUALIFIERS[0]));
				if(name != null){
					file.setName(Bytes.toString(name));
				}
				byte[] description = result.getValue(Bytes.toBytes(FAMILY), Bytes.toBytes(QUALIFIERS[2]));
				if(description != null){
					file.setDescription(Bytes.toString(description));
				}
				byte[] type = result.getValue(Bytes.toBytes(FAMILY), Bytes.toBytes(QUALIFIERS[3]));
				if(type != null){
					file.setType(Bytes.toString(type));
				}
				file.setRowKey(Bytes.toString(result.getRow()));
				files.add(file);
			}
		}
		return files;
	}
	
	public Map<String, Object> saveStandardFile(Map<String, Object> map,StandardFile file) throws IOException{
		if(file != null){
			Map<String, byte[]> map2 = new HashMap<String, byte[]>();
			if(file.getName() != null && !"".equals(file.getName())){
				map2.put(QUALIFIERS[0], Bytes.toBytes(file.getName()));
			}
			if(file.getContent() != null && !"".equals(file.getContent())){
				map2.put(QUALIFIERS[1], Bytes.toBytes(file.getContent()));
			}
			if(file.getDescription() != null && !"".equals(file.getDescription())){
				map2.put(QUALIFIERS[2], Bytes.toBytes(file.getDescription()));
			}
			if(file.getType() != null && !"".equals(file.getType())){
				map2.put(QUALIFIERS[3], Bytes.toBytes(file.getType()));
			}
			if(!map2.isEmpty()){
				helper.updateRowData(TABLE_NAME, UUIDGenerator.getUUID(), FAMILY, QUALIFIERS, map2);
				map.put("msg", "success");
			}else{
				map.put("msg", "empty");
			}
		}else{
			map.put("msg", "empty");
		}
		return map;
	}

	public List<Sourcefile> findBysourceName(Sourcefile searchtask) throws IOException {
		logger.info("select all task records:default pageSize 20");
		List<Sourcefile> sources = new ArrayList<Sourcefile>();
		
		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
		if(searchtask != null){
			//sources = adpaDao.findAllTasks(searchtask.getsourceName());
			String sourcename=searchtask.getSourceName();
			if(sourcename!=null){
				if(hdfs.checkFile("/Source/"+sourcename))
				list=hdfs.getFileLoc(sourcename);
				else{
					list=null;
				}
			}else{
			list=hdfs.listAllFile("Source");
			}
		}else{
			list=hdfs.listAllFile("Source");
			//sources = adpaDao.findAllTasks("");
		}
		if(list != null && !list.isEmpty()){
			
			
			for (int i = 0; i < list.size(); i++) {
				Map<String, String> map = list.get(i) ;
				Sourcefile source=new Sourcefile();
				 Set<String> set = map.keySet(); 
				  for (String s:set) {
					  
					  if(s.equals("time")){
						  source.setStorageTime(map.get(s));
					  }else if(s.equals("path")){
						  source.setSourceName(map.get(s));
					  }
					  source.setSourceType("Original Data");
				   System.out.println(s+","+map.get(s));
				  }
				sources.add(source);
			}
			/*
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
			*/
		}
		return sources;
	}
	
	public void deletesource(String sourceName) throws IOException {
		hdfs.deleteFile(sourceName);
		logger.info("delete row which sourceName is" + sourceName + " success!");
	}
	
	
	public String downloadsource(String sourceName){//download form hadoop
		String filevalue=null;
		filevalue= hdfs.downFile(sourceName, sourceName);
		return filevalue;
	}
	
	public List<String> downLoadData(String rowKey) throws IOException {
		List<String> list = new ArrayList<String>();
		
		if(rowKey == null || "".equals(rowKey.trim())){
			return list;
		}
		
		 List<String> columns = basicDao.findColumnByRowkey(rowKey);
		if(columns.isEmpty()){
			return list;
		}
		
		List<Result> results = basicDao.findDataByTableAndSize("task"+rowKey, null);
		if(results == null || results.isEmpty()){
			return list;
		}
		/*for(Result result:results){
			StringBuffer sb = new StringBuffer();
			for (KeyValue keyValue : result.raw()){
				sb.append(new String(keyValue.getValue()));
				sb.append(" ;");
				if(sb.length() > 0){
					list.add(sb.toString());
				}
			}
		}
		*/
		
		for(Result result:results){
			StringBuffer sb = new StringBuffer();
			byte[] obj = result.getValue(Bytes.toBytes("id"), null);
			if(obj != null){
				sb.append(Bytes.toString(obj)+";");
			}else {
				sb.append(" ;");
			}
			/*
			 for(int i=0;i<columns.size();i++){
				byte[] obj = result.getValue(Bytes.toBytes("task"), Bytes.toBytes(columns.get(i)));
				if(obj != null){
					sb.append(Bytes.toString(obj)+";");
				}else {
					sb.append(" ;");
				}
			}
			*/
			if(sb.length() > 0){
				list.add(sb.toString());
			}
		}
		
		return list;
	}
	public void sourceview(String sourceName, Map<String, Object> map){
		if(sourceName == null || "".equals(sourceName.trim())){
			map.put("msg", "empty");
			return;
		}
		String filevalue=null;
		filevalue= hdfs.viewFile(sourceName, sourceName);
		List<String> columns=new ArrayList<String>();
		columns.add("sourceName:     "+sourceName);
		List<List<String>> list = new ArrayList<List<String>>();
		List<String> str=new ArrayList<String>();
		str.add(filevalue);
		list.add(str);
		map.put("msg", "success");
		map.put("list", list);
		map.put("columns", columns);
	}
	public void deleteFile(String rowKey) throws IOException {
		helper.deleteRowData(TABLE_NAME, rowKey);
		logger.info("delete row which rowKey is" + rowKey + " success!");
	}
	
	
}
