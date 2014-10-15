package com.missionsky.scp.dao;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.antlr.grammar.v3.ANTLRv3Parser.finallyClause_return;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.missionsky.scp.dataanalysis.utils.HbaseUtil;
import com.missionsky.scp.dataanalysis.utils.UUIDGenerator;

public class SourceDao {
	private Logger logger=LoggerFactory.getLogger(SourceDao.class);
	private final static String TABLE_NAME="Sourcefile";
	private final static String FAMILY = "source";
	private final static String [] QUALIFIERS = {"sourceName","sourceType","StorageTime"};
	private static Calendar calendar = Calendar.getInstance();
	private static SimpleDateFormat sf;
	public static void saveSourceFile(String sourename,String sourcetpye) throws IOException{
		String rowkey=UUIDGenerator.getUUID();
		
		sf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		Map <String,byte[]> map = new HashMap<String, byte[]>();
	    map.put(QUALIFIERS[0],Bytes.toBytes(sourename));
	    map.put(QUALIFIERS[1],Bytes.toBytes(sourcetpye));
	    map.put(QUALIFIERS[2], Bytes.toBytes(sf.format(calendar.getTime())));
	    
		HbaseUtil.updateRowData(TABLE_NAME, rowkey, FAMILY, QUALIFIERS, map);
		
	}

}
