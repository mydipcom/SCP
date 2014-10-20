package com.missionsky.scp.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.missionsky.scp.entity.Sourcefile;

@Component
public class SourceDao {
	private static final String TABLE_NAME = "Sourcefile";
	private static final String FAMILY = "source";
	private static final String[] QUALIFIERS = {"sourceName","sourceType","StorageTime"};
	
	@Autowired
	private HbaseHelper helper;
	
	private Sourcefile getSourcefile(Result result) {
		if (result != null) {
			Sourcefile sourcefile = new Sourcefile();
			sourcefile.setRowKey(Bytes.toString(result.getRow()));
			byte[] name = result.getValue(Bytes.toBytes(FAMILY),
					Bytes.toBytes(QUALIFIERS[0]));
			if (name != null) {
				sourcefile.setSourceName(Bytes.toString(name));
		
			}
			
			byte[] sourceType = result.getValue(Bytes.toBytes(FAMILY),
					Bytes.toBytes(QUALIFIERS[1]));
			if (sourceType != null) {
				sourcefile.setSourceType(Bytes.toString(sourceType));
			}
			byte[] StorageTime = result.getValue(Bytes.toBytes(FAMILY),
					Bytes.toBytes(QUALIFIERS[2]));
			if (StorageTime != null ) {
				
				sourcefile.setStorageTime(Bytes.toString(StorageTime));
			}
			
			return sourcefile;
		}
		return null;
	}

	
	public List<Sourcefile> findAllsource(String sourceName) throws IOException {
		Map<String, byte[]> map = new HashMap<String, byte[]>();
		if (sourceName != null && !"".equals(sourceName.trim())) {
			map.put(QUALIFIERS[0], Bytes.toBytes(sourceName));
		}
		List<Result> results = helper.getRowResults(TABLE_NAME, FAMILY,
				QUALIFIERS, map, null);
		List<Sourcefile> sources = new ArrayList<Sourcefile>();
		if (results != null && !results.isEmpty()) {
			for (Result result : results) {
				Sourcefile sourcefile = getSourcefile(result);
				if (sourcefile != null) {
					System.out.println("rowkey:++++++++"+sourcefile.getRowKey());
					sources.add(sourcefile);
				}
			}
		}
		return sources;
	}

		public void deletesource(String sourceName,String rowkey){
			try {
				helper.deleteRowData(TABLE_NAME, rowkey);
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				helper.deleteTable(sourceName);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
}
