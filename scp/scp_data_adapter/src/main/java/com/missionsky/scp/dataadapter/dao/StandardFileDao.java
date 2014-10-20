package com.missionsky.scp.dataadapter.dao;

import java.io.IOException;


//import org.apache.hadoop.hbase.client.Result;
//import org.apache.hadoop.hbase.util.Bytes;





import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

import com.missionsky.scp.dataadapter.entity.StandardFile;
import com.missionsky.scp.dataadapter.util.HbaseUtil;



public class StandardFileDao {
	
	private static final String TABLE_NAME = "standardFile";
	private static final String FAMILY = "file";
	private static final String[] QUALIFIERS = { "fileName","content","description"};
	public StandardFileDao(){
	}
	
	public  StandardFile getFileByRowKey(String rowKey) throws IOException{
		Result result = HbaseUtil.selectRowResult(TABLE_NAME, rowKey);
		if(result != null){
			return transToFile(result);
		}
		return null;
	}
	
	@SuppressWarnings("unused")
	public  StandardFile transToFile(Result result){
		if(result != null){
			StandardFile file = new StandardFile();
			file.setRowKey(Bytes.toString(result.getRow()));
			
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
			if(file==null){
				System.out.println("this null  why");
			}
			return file;
		}
		return null;
	}
public static void main(String arg[]){
	StandardFileDao dao = new StandardFileDao();
	try {
		StandardFile file=dao.getFileByRowKey("72c0639b381842d49acbf5918843b1f6");
		System.out.println(file.getContent());
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
}
}
