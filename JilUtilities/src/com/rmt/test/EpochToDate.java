package com.rmt.test;

import java.text.SimpleDateFormat;
import java.util.Date;

public class EpochToDate {

	public static void main(String[] args)
	{
		long epochTime=1506924025; // this translates to GMT: Monday, October 2, 2017 6:00:25 AM
		//Your time zone: Monday, October 2, 2017 1:00:25 AM GMT-05:00 DST https://www.epochconverter.com/
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:MM:SS");
		Date d = new Date(epochTime*1000L);
		System.out.println(d);
		

	}

}
