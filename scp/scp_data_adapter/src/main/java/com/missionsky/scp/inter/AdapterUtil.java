
package com.missionsky.scp.inter;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.quartz.SchedulerException;












import com.missionsky.scp.dataadapter.dao.AdapterstatusDao;
import com.missionsky.scp.dataadapter.entity.AdapterTask;
import com.missionsky.scp.entity.Source;
import com.missionsky.scp.quartz.SimpleQuartz;



@SuppressWarnings("serial")
public class AdapterUtil extends UnicastRemoteObject implements IadapterUtil {
	
	private static final String GROUP = "adapter_group";
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static String rowkey;
	public static final int jobIsReady = 0;
	
	protected AdapterUtil() throws RemoteException {
		super();
	}

	
	@Override 
	public void runTask(Source source) throws RemoteException {
		AdapterTask adapterTask = new AdapterTask();
		adapterTask.setRowKey(source.getRowKey());
		adapterTask.setTaskName(source.getTaskName());
		adapterTask.setFileName(source.getFileName());
		adapterTask.setStartTime(source.getStartTime());
		rowkey=source.getRowKey();
		if (source.getTriggerType() != null) {
			Integer type = source.getTriggerType();
			if (type == 1) {
				try {
					SimpleQuartz.runDaily(source.getRowKey(), GROUP,
						sdf.parse(source.getStartTime()), source.getTime(),
							adapterTask);
				} catch (SchedulerException e) {
				
					e.printStackTrace();
				} catch (ParseException e) {
					
					e.printStackTrace();
				}
			} else if (type == 2) {
			
						try {
							SimpleQuartz.runWeek(source.getRowKey(), GROUP,
									sdf.parse(source.getStartTime()), source.getWeekday(),
									adapterTask);
						} catch (SchedulerException e) {
							
							e.printStackTrace();
						} catch (ParseException e) {
						
							e.printStackTrace();
						}
					
				}
			else if (type == 3) {
				
					try {
						SimpleQuartz.runOneTime(source.getRowKey(), GROUP,
								sdf.parse(source.getStartTime()), adapterTask);
					} catch (SchedulerException e) {
						e.printStackTrace();
					} catch (ParseException e) {
						e.printStackTrace();
					}
			
			}
		
		}
		try {
			AdapterstatusDao.getInstance().updateScheduleRecord(source.getRowKey(),
					jobIsReady);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	 public Integer getRunStatus(String rowKey) throws RemoteException {
			try {
				return AdapterstatusDao.getInstance().getRunStatus(rowKey);
			} catch (IOException e) {
				return null;
			}
	 }
	public void deleteJob(String rowkey) throws RemoteException{
		if (rowkey == null || "".equals(rowkey.trim())) {
			return;
		}
		try {
			SimpleQuartz.removeJob(rowkey, GROUP);
		} catch (SchedulerException e) {
			throw new RemoteException(e.getMessage());
		}
	}
}
