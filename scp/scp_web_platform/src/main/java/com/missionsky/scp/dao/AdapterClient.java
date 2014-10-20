
package com.missionsky.scp.dao;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import org.springframework.stereotype.Component;

import com.missionsky.scp.entity.Source;
import com.missionsky.scp.inter.IadapterUtil;


@Component
public class AdapterClient {
	
	private static IadapterUtil adapterUtil=null;
	
	static{
		try {

			adapterUtil =(IadapterUtil)Naming.lookup("rmi://localhost:8888/adapterutil");
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			e.printStackTrace();
		}
	}
	 public void runTask(Source source) throws RemoteException {
			adapterUtil.runTask(source);
	}
	
	
}
