package com.missionsky.scp.action;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.missionsky.scp.dao.DataClient;
import com.missionsky.scp.entity.Algorithm;
import com.missionsky.scp.entity.Source;
import com.missionsky.scp.entity.Sourcefile;
import com.missionsky.scp.entity.Task;

import com.missionsky.scp.service.AlgorithmService;
import com.missionsky.scp.service.FileService;
import com.missionsky.scp.service.SourceService;
import com.missionsky.scp.util.EncodingTool;


@Controller
@RequestMapping("source")
public class SourceAction {
	private static Logger logger = LoggerFactory
			.getLogger(SourceAction.class);
	@Autowired
	private FileService fileService;
	
	@Autowired
	private SourceService sourceService;

	
	@RequestMapping(value = "adapterTask", method = RequestMethod.GET)
	public String adapterlist(Model model, Source source){
		List<Source> list = new ArrayList<Source>();
		System.out.println(source);
		try {
			list = sourceService.findByTaskName(source);
			if(list != null && !list.isEmpty()){
				for(Source t:list){
					
			// t.setStatus(client.getRunStatus(t.getRowKey()));//使用RMI；
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		model.addAttribute("tasks", list);
		model.addAttribute("task", new Task());
		if(source != null){
			model.addAttribute("taskName", source.getTaskName());
			model.addAttribute("fileName", source.getFileName());
		}else{
			model.addAttribute("taskName", "");
			model.addAttribute("fileName", "");
		}
		return "source/adapterTask";
	}
	
	@RequestMapping(value = "sourcelist", method = RequestMethod.GET)
	public String sourcelist(Model model, Sourcefile sourcefile){
		List<Sourcefile> list = new ArrayList<Sourcefile>();//hadoop
		List<Sourcefile> lists = new ArrayList<Sourcefile>();//hbase

		try {
			list = fileService.findBysourceName(sourcefile);
			if(list != null && !list.isEmpty()){
				for(Sourcefile t:list){
					
			// t.setStatus(client.getRunStatus(t.getRowKey()));//使用RMI；
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		
	
		try{
			lists=sourceService.findBySourceName(sourcefile);
		}
			catch (IOException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		
		model.addAttribute("htasks", lists);
		model.addAttribute("tasks", list);
		
		if(sourcefile != null){
			model.addAttribute("taskName", sourcefile.getSourceName());
			//model.addAttribute("fileName", source.getFileName());
		}else{
			//model.addAttribute("taskName", "");
			//model.addAttribute("fileName", "");
		}
		return "source/sourcelist";
	}
	
	@RequestMapping(value = "adpaterupdatetask", method = RequestMethod.GET)
	public String updatetask(Model model,String rowKey){
		Source source=new Source();
		if(rowKey != null && !"".equals(rowKey.trim())){
			try {
				source = sourceService.findTask(rowKey);
			} catch (IOException e) {
				e.printStackTrace();
				logger.error(e.getMessage());
			}
		}
		model.addAttribute("source", source);
		try {
			model.addAttribute("files", fileService.getAllFiles());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "source/adpaupdateTask";
	}
	
	@RequestMapping(value="deletetask", method=RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> deleteTask(String rowKey){
		Map<String, Object> map = new HashMap<String,Object>();
		try {
			sourceService.deleteTask(rowKey);
		//	client.deleteJob(rowKey);
			map.put("msg", "success");
		} catch (IOException e) {
			e.printStackTrace();
			map.put("msg", "failure");
		}
		return map;
	}
	
	@RequestMapping(value="deletesource", method=RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> deletesource(String sourceName){
		Map<String, Object> map = new HashMap<String,Object>();
		try {
			fileService.deletesource(sourceName);
		//	client.deleteJob(rowKey);
			map.put("msg", "success");
		} catch (IOException e) {
			e.printStackTrace();
			map.put("msg", "failure");
		}
		return map;
	}
	@RequestMapping("/download/{sourceName}&&{sourceType}")
	public void downloadFile(@PathVariable("sourceName") String sourceName,@PathVariable("sourceType")String sourceType,
			HttpServletRequest request, HttpServletResponse response) {
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		response.setHeader("Content-Disposition", "attachment;fileName="
				+ sourceName+".txt");
		
		
		System.out.println(sourceName+"++++++++++"+sourceType);
		if(sourceType.equals("hadoop")){
		try {
			String value=fileService.downloadsource(sourceName);

		OutputStream os = response.getOutputStream();
			if(value != null && !value.isEmpty()){
			
					byte[] b = Bytes.toBytes(value);
					os.write(b);
				
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		}else if(sourceType.equals("hbase")){
			try {
				
			List<String> list=fileService.downLoadData(sourceName);
			OutputStream os = response.getOutputStream();
			if(list != null && !list.isEmpty()){
				for(String str:list){
					byte[] b = Bytes.toBytes(str);
					os.write(b);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		}
	}
	@RequestMapping(value="sourceview", method=RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> sourceview(String sourceName){
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			fileService.sourceview(sourceName,map);
		} catch (Exception e) {
			e.printStackTrace();
			map.put("msg", "failure");
		}
		return map;
	}
	@RequestMapping(value = "savatask", method = RequestMethod.POST)
	@ResponseBody
	public  Map<String, Object> saveTask(Source source) throws IOException{
		Map<String, Object> map = new HashMap<String,Object>();		
		String rowKey = source.getRowKey();
		sourceService.saveTask(source);
		System.out.println("++++++++++++++++++++++++++++++");
		map.put("msg", "success");
		return map;
	}
}
