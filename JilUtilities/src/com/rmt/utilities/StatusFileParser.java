package com.rmt.utilities;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import com.rmt.main.JilUtils;



public class StatusFileParser 
{
	// parses the autosys status files to get the status of each job and store it in a Map
	// run autorep -J ALL -n to get the status file and use it as input to this code.
	
	Map<String,String> jobStatusMap = new HashMap<String,String>();
	FileReader jilFileReader;
	BufferedReader jilFileBuffer;
	String outputJilFile;
	public void readJobStatusFile(String jilFileName)
	{
		try
		{
			jilFileReader = new FileReader(jilFileName);
			jilFileBuffer = new BufferedReader(jilFileReader);
			
		}
		catch(FileNotFoundException fnfe)
		{
			fnfe.printStackTrace();
		}
	}

}
