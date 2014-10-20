package com.missionsky.scp.dataadapter;

import java.io.IOException;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;

import com.missionsky.scp.dataadapter.dao.StandardFileDao;
import com.missionsky.scp.dataadapter.datafetcher.DataFetcherDispenser;
import com.missionsky.scp.dataadapter.entity.AdapterTask;
import com.missionsky.scp.dataadapter.entity.StandardFile;

public class Taskline {

	public Taskline(){
		
	}
	public void gainTask(AdapterTask task){
		StandardFile file = new StandardFile();
		Document doc=null;
		StandardFileDao standardfiledao=new StandardFileDao();
		if (task != null) {
			System.out.println("taskName:" + task.getTaskName());
			System.out.println("taskfileROWkey:" + task.getFileName());
				
				try {
					file=standardfiledao.getFileByRowKey("72c0639b381842d49acbf5918843b1f6");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if(file==null){
					System.out.println("file is null");
				}
				
				try {
					doc = DocumentHelper.parseText(file.getContent());
				} catch (DocumentException e) {
					
					e.printStackTrace();
				}
			DataFetcherDispenser dataFetcherDispenser = new DataFetcherDispenser();
			dataFetcherDispenser.gainParsedData(doc);
		}
		
	}
}
