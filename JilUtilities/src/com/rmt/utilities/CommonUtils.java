package com.rmt.utilities;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

public class CommonUtils 
{
	String humanTime=null;
	
	static Logger logger = Logger.getRootLogger();
	public String convertEpochToDate(String epochTime)
	{
		
		logger.info("Converting "+epochTime+" to human time");
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:MM:ss");
		Long longEpochTime = Long.parseLong(epochTime);
		Date longDate = new Date(longEpochTime*1000L);
		logger.debug("Epoch ("+epochTime+") Date is :"+longDate);
		humanTime = sdf.format(longDate);
		
		
		return humanTime;
	}

}
