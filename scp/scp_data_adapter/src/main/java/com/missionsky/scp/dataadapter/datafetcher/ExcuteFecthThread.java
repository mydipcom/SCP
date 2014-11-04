package com.missionsky.scp.dataadapter.datafetcher;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.missionsky.scp.dataadapter.common.SingleInstance;
import com.missionsky.scp.dataadapter.common.SystemConstants;
import com.missionsky.scp.dataadapter.dao.AdapterstatusDao;
import com.missionsky.scp.dataadapter.datafilter.DataFilterApi;
import com.missionsky.scp.dataadapter.entity.DataSource;
import com.missionsky.scp.dataadapter.util.FileNameUtil;
import com.missionsky.scp.inter.AdapterUtil;

/**
 * @author Ellis Xie
 * @version 1.0
 * data-adapter线程
 */
public class ExcuteFecthThread extends Thread {

	private DataSource dataSource;

	private DataFetcher dataFetcher;
	
	private AdapterUtil adapterutil;
	
	public static String filename;//未使用
	
	private Logger logger = LoggerFactory.getLogger(ExcuteFecthThread.class);
	
	public ExcuteFecthThread(DataSource dataSource, DataFetcher dataFetcher) {
		this.dataSource = dataSource;
		this.dataFetcher = dataFetcher;
	}

	@Override
	public void run() {
		
		HashMap<String, DataSource> dataSources = SingleInstance.getDataSources();
		Integer step = dataSource.getStep();
		// filename 定义了但是没使用，可以再这里通过filename的使用实现存放不同时间段的数据源
		try {
			filename = FileNameUtil.getFileName(dataSource.getName());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		Integer offset = 0;
		
		if (dataSources.get(dataSource.getName()).getIsFirst()) {
			
			Integer total = divideProcess(offset, step);
			
			dataSource.setIsFirst(false);
			dataSource.setTotal(total);
			dataSources.put(dataSource.getName(), dataSource);
			SingleInstance.setDataSources(dataSources);
		} else {
			
			Integer currentTotal = dataFetcher.getTotal(dataSource);
			
			if (dataSource.getTotal() != null && currentTotal != null) {
				if (dataSource.getTotal() < currentTotal) {
					// 增量update,保存增加的数据
					divideProcess(dataSource.getTotal(), step);
				} else if (dataSource.getTotal() > currentTotal) {
					divideProcess(offset, step);
				}
			}

		}
		
		System.out.println("the thread is end +++++++++++++");
		try {
			AdapterstatusDao.getInstance().updateScheduleRecord(adapterutil.rowkey,3);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	// 分块处理
	protected Integer divideProcess(Integer offset, Integer step){
		
		Integer total = 0;
		Integer result = null;
		if (step != null && step > 0) {
			do {
				result = dataProcess(offset);
				offset += step;
				total += result;
			} while (result.equals(step));
		} else {
			total = dataProcess(null);
		}
		return total;
	}
	
	// 处理数据
	protected Integer dataProcess(Integer offset) {
		
		List<Map<String, String>> records = dataFetcher.renderMergedFetchedData(dataSource, offset);
		
		logger.info("Save data to hdfs.");
		DataFilterApi.filterClassifiedStorage(records, dataSource.getName(), SystemConstants.DATA_FLITER_TYPE_INSERT);
        
		return records.size();
	}

}
