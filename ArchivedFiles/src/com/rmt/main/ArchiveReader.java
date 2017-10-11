package com.rmt.main;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.rmt.readers.database.DBUtils;
import com.rmt.readers.file.ArchiveJobRunsReader_45;

public class ArchiveReader 
{
	static Logger logger = Logger.getRootLogger();
	

	public static void main(String[] args) throws FileNotFoundException, IOException, SQLException, ParseException 
	{
		String log4jLocation = "resources/log4j.properties";
		PropertyConfigurator.configure(log4jLocation);
		logger=Logger.getLogger("ArchivedFileReaderUtils.main");
		Properties aejobProps = new Properties();
		aejobProps.load(new FileInputStream("resources/archives.properties"));
		String archiveFolder=aejobProps.getProperty("ARCHIVE_FOLDER");
		ArchiveJobRunsReader_45 ajobs45 = new ArchiveJobRunsReader_45();
		ajobs45.readJobRunsArchive();
		
		
		

	}

}
