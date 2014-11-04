package com.missionsky.scp.inter;

import java.rmi.Remote;
import java.rmi.RemoteException;

import com.missionsky.scp.entity.Product;
import com.missionsky.scp.entity.Source;

public interface IadapterUtil extends Remote {

	public void runTask(Source source) throws RemoteException;
	public Integer getRunStatus(String rowKey) throws RemoteException;
	public void deleteJob(String rowkey) throws RemoteException;
}
