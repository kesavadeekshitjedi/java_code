package com.rmt.main;

import java.io.IOException;

import org.apache.log4j.PropertyConfigurator;

import com.rmt.utils.ExtractJobInfo_File;
import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;

public class GS {

	public static void main(String[] args) throws IOException, java.text.ParseException
	{
		String log4jLocation = "resources/log4j.properties";
		PropertyConfigurator.configure(log4jLocation);
		
		ExtractJobInfo_File ef = new ExtractJobInfo_File();
		String archivedFilesFolder="D:\\OneDrive-Business\\OneDrive - Robert Mark Technologies\\Goldman\\files\\Prod_neededFiles";
		ef.getListOfFiles(archivedFilesFolder);
		

	}

}
