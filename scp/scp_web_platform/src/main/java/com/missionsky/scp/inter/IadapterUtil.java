package com.missionsky.scp.inter;

import java.rmi.Remote;
import java.rmi.RemoteException;

import com.missionsky.scp.entity.Source;

public interface IadapterUtil extends Remote {
	public void runTask(Source source) throws RemoteException;
}
