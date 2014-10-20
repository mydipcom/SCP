
package com.missionsky.scp.inter;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.quartz.SchedulerException;






import com.missionsky.scp.dataadapter.entity.AdapterTask;
import com.missionsky.scp.entity.Source;

import com.missionsky.scp.quartz.SimpleQuartz;



@SuppressWarnings("serial")
public class AdapterUtil extends UnicastRemoteObject implements IadapterUtil {
	
	private static final String GROUP = "adapter_group";
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
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

	}
	
}
