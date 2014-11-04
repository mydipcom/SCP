package com.missionsky.scp.dataanalysis;

import java.io.IOException;

import org.apache.mahout.fpm.pfpgrowth.DeliciousTagsExample;

import com.missionsky.scp.dataanalysis.utils.RemoteHadoopUtil;

public class PGrowthTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		RemoteHadoopUtil.setConf(LogisticTest.class, Thread.currentThread(), "/hadoop");
		String arg[]=new String[]{"-i","/PFGrowth/retail.dat","-o","/PFGrowth/Test_Result","-regex","[ ]"};
         try {
			DeliciousTagsExample.main(arg);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
