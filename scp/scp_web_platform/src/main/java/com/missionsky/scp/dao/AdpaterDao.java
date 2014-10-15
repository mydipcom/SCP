package com.missionsky.scp.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;






import com.missionsky.scp.entity.Source;
import com.missionsky.scp.entity.Task;
import com.missionsky.scp.util.DateUtil;
import com.missionsky.scp.util.UUIDGenerator;
@Component
public class AdpaterDao {
	private static final String TABLE_NAME = "AdpaterTask";
	private static final String FAMILY = "task";
	private static final String[] QUALIFIERS = {"taskName","description","fileName","triggerType","startTime","time","weekday"};

	@Autowired
	private HbaseHelper helper;
	
	private Map<String, byte[]> getValues(Source source) {
		Map<String, byte[]> map = new HashMap<String, byte[]>();
		if (source.getTaskName() != null && !"".equals(source.getTaskName().trim())) {
			map.put(QUALIFIERS[0], Bytes.toBytes(source.getTaskName()));
			if (source.getDescription() != null) {
				map.put(QUALIFIERS[1], Bytes.toBytes(source.getDescription()));
			}
			if (source.getFileName() != null) {
				map.put(QUALIFIERS[2], Bytes.toBytes(source.getFileName()));
			}
			if (source.getTriggerType() != null) {
				map.put(QUALIFIERS[3], Bytes.toBytes(source.getTriggerType()));
				if (source.getTriggerType() == 1) {
					if (source.getTime() != null) {
						map.put(QUALIFIERS[5], Bytes.toBytes(source.getTime()));
					}
				} else if (source.getTriggerType() == 2) {
					if (source.getTime() != null) {
						map.put(QUALIFIERS[5], Bytes.toBytes(source.getTime()));
					}
					if (source.getWeekday() != null) {
						map.put(QUALIFIERS[6], Bytes.toBytes(source.getWeekday()));
					}
				}
			}
			if (source.getStartTime() != null) {
			//	 String date = DateUtil.format(source.getStartTime(), null);
				map.put(QUALIFIERS[4], Bytes.toBytes(source.getStartTime()));
			}
		}
		return map;
	}
	
	private Source getSource(Result result) {
		if (result != null) {
			Source source = new Source();
			source.setRowKey(Bytes.toString(result.getRow()));
			byte[] name = result.getValue(Bytes.toBytes(FAMILY),
					Bytes.toBytes(QUALIFIERS[0]));
			if (name != null) {
				source.setTaskName(Bytes.toString(name));
			}
			byte[] description = result.getValue(Bytes.toBytes(FAMILY),
					Bytes.toBytes(QUALIFIERS[1]));
			if (description != null) {
				source.setDescription(Bytes.toString(description));
			}
			byte[] file = result.getValue(Bytes.toBytes(FAMILY),
					Bytes.toBytes(QUALIFIERS[2]));
			if (file != null) {
				source.setFileName(Bytes.toString(file));
			}
			byte[] triggerType = result.getValue(Bytes.toBytes(FAMILY),
					Bytes.toBytes(QUALIFIERS[3]));
			if (triggerType != null && triggerType.length > 0) {
				
				source.setTriggerType(Bytes.toInt(triggerType));
			}
			byte[] startTime = result.getValue(Bytes.toBytes(FAMILY),
					Bytes.toBytes(QUALIFIERS[4]));
			if (startTime != null) {
				Date date = DateUtil.parse(Bytes.toString(startTime), null);
				//source.setStartTime(date);
	
			
			source.setStartTime(Bytes.toString(startTime));
				}
			byte[] time = result.getValue(Bytes.toBytes(FAMILY),
					Bytes.toBytes(QUALIFIERS[5]));
			if (time != null && time.length > 0) {
				
				source.setTime(Bytes.toInt(time));
			}
			byte[] weekday = result.getValue(Bytes.toBytes(FAMILY),
					Bytes.toBytes(QUALIFIERS[6]));
			if (weekday != null && weekday.length > 0) {
				
				source.setWeekday(Bytes.toInt(weekday));
			}
			return source;
		}
		return null;
	}

	
	public void saveTask(Source source) throws IOException {
		if (source != null) {
			Map<String, byte[]> map = getValues(source);
			if (!map.isEmpty()) {
				String rowKey = source.getRowKey();
				if (rowKey == null || rowKey.trim().equals("")) {
					rowKey = UUIDGenerator.getUUID();
				}
				helper.updateRowData(TABLE_NAME, rowKey, FAMILY, QUALIFIERS,
						map);
			}
		}
	}
	public Source findTaskByRowKey(String rowKey) throws IOException {
		Result result = helper.selectRowResult(TABLE_NAME, rowKey);
		return getSource(result);
	}

	public void deleteTaskByRowKey(String rowkey) throws IOException {
		helper.deleteRowData(TABLE_NAME, rowkey);
	}
	
	public List<Source> findAllTasks(String taskName) throws IOException {
		Map<String, byte[]> map = new HashMap<String, byte[]>();
		if (taskName != null && !"".equals(taskName.trim())) {
			map.put(QUALIFIERS[0], Bytes.toBytes(taskName));
		}
		List<Result> results = helper.getRowResults(TABLE_NAME, FAMILY,
				QUALIFIERS, map, null);
		List<Source> sources = new ArrayList<Source>();
		if (results != null && !results.isEmpty()) {
			for (Result result : results) {
				Source source = getSource(result);
				if (source != null) {
					sources.add(source);
				}
			}
		}
		return sources;
	}
}
