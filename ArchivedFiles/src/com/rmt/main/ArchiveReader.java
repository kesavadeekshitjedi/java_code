package com.rmt.main;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
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
		Connection dbConnection;
		String log4jLocation = "resources/log4j.properties";
		PropertyConfigurator.configure(log4jLocation);
		logger=Logger.getLogger("ArchivedFileReaderUtils.main");
		Properties aejobProps = new Properties();
		aejobProps.load(new FileInputStream("resources/archives.properties"));
		String archiveFolder=aejobProps.getProperty("ARCHIVE_FOLDER");
		ArchiveJobRunsReader_45 ajobs45 = new ArchiveJobRunsReader_45();
		ajobs45.readJobRunsArchive();
		DBUtils db = new DBUtils();
		Properties dbProps = new Properties();
		try 
		{
			dbProps.load(new FileInputStream("resources/DB.properties"));
		} 
		catch (IOException e1) 
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String dbHostName=dbProps.getProperty("AEDB_HOST");
		String dbPort=dbProps.getProperty("AEDB_DB_PORT");
		String dbType=dbProps.getProperty("AEDB_DB_TYPE");
		String dbName=dbProps.getProperty("AEDB_DB_SID");
		String dbUser=dbProps.getProperty("AEDB_DB_USER");
		String dbPass = dbProps.getProperty("AEDB_DB_PASS");
		String jobIsOldDate=aejobProps.getProperty("JOB_IGNORE_START_DATE");
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:MM:ss");
		
		Date ignoreJobDate=sdf.parse(jobIsOldDate);
		
		logger.info("Database type is "+dbType);
		
		if(dbType.equalsIgnoreCase("Oracle"))
		{
			try
			{
				dbConnection=db.connect2Oracle(dbHostName,dbPort,dbUser,dbPass,dbName);
				logger.info("Oracle Database connection established");
				db.getCalendarList45(dbConnection, dbType);
			} 
		
			catch (ClassNotFoundException | SQLException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		else if(dbType.equalsIgnoreCase("MSSQL"))
		{
			try
			{
				dbConnection=db.connect2SQLServer(dbHostName,dbPort,dbUser,dbPass,dbName);
				logger.info("SQL Server Database connection established");
				db.getCalendarList45(dbConnection, dbType);
			} 
		
			catch (ClassNotFoundException | SQLException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		else if(dbType.equalsIgnoreCase("Sybase"))
		{
			try
			{
				dbConnection=db.connect2Sybase(dbHostName, dbPort, dbUser, dbPass, dbName);
				logger.info("Sybase Database connection established");
				List<String> calList=db.getCalendarList45(dbConnection, dbType);
				Map<String, List<String>> calDates = db.getCalendarDates45(dbConnection, calList, dbType);
				System.out.println(calDates);
			} 
		
			catch (ClassNotFoundException | SQLException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		
		

	}

}
