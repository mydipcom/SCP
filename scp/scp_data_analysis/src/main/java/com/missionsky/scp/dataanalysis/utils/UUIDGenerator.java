package com.missionsky.scp.dataanalysis.utils;

import java.util.UUID;

public class UUIDGenerator {
	public static String getUUID(){
		String uuid= UUID.randomUUID().toString();
		return uuid.replace("-", "");
	}

}
