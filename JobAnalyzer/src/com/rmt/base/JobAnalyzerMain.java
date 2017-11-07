package com.rmt.base;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.rmt.database.autosysDBUtils;

public class JobAnalyzerMain {
	static Connection dbConnection;

	static Logger logger = Logger.getRootLogger();
	public static Date now;
	public static SimpleDateFormat sdf;
	public static String myTime;
	static int argLength;
	static Date checkjobLastRunDate;
	static Date checktodaysDate;
	static Date actualjobLastRunDate;
	static Date tempDate;
	public static void main(String[] args) throws ParseException, SQLException
	{
		
		now = new Date();
		sdf = new SimpleDateFormat("yyyy-MM-dd HH:MM:ss.SSS");
		myTime = sdf.format(now);
		
		String log4jLocation = "resources/log4j.properties";
		PropertyConfigurator.configure(log4jLocation);
		logger=Logger.getLogger("jobanalyzer.main");
		logger.info("log4j initialized");
		if(args[0].equalsIgnoreCase("DECOM") || args[0].equalsIgnoreCase("1"))
		{
			
				
			logger.info("Begin analysing jobs");
			String jobPattern=args[1].trim();
			//String lastRunDate=args[2].trim();
			String lastRunDate="2005-12-01 00:00:00.000";
			//checkjobLastRunDate=sdf.parse(sdf.format(new Date(Long.parseLong(lastRunDate))));
			checkjobLastRunDate=sdf.parse(lastRunDate);
			logger.info("Last run date cannot be less than "+checkjobLastRunDate);
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
			autosysDBUtils dbUtils = new autosysDBUtils();
			String dbHostName=dbProps.getProperty("AEDB_HOST");
			String dbPort=dbProps.getProperty("AEDB_DB_PORT");
			String dbType=dbProps.getProperty("AEDB_DB_TYPE");
			String dbName=dbProps.getProperty("AEDB_DB_SID");
			String dbUser=dbProps.getProperty("AEDB_DB_USER");
			String dbPass = dbProps.getProperty("AEDB_DB_PASS");
			logger.info("Database type is "+dbType);
			if(dbType.equalsIgnoreCase("Oracle"))
			{
				try
				{
					dbConnection=dbUtils.connect2Oracle(dbHostName,dbPort,dbUser,dbPass,dbName);
					logger.info("Oracle Database connection established");
					logger.info("Checking if jobs exist...");
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
					dbConnection=dbUtils.connect2SQLServer(dbHostName,dbPort,dbUser,dbPass,dbName);
					logger.info("SQL Server Database connection established");
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
					dbConnection=dbUtils.connect2Sybase(dbHostName, dbPort, dbUser, dbPass, dbName);
					logger.info("Sybase Database connection established");
				} 
			
				catch (ClassNotFoundException | SQLException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
			List<String> jobList = dbUtils.getJobList(dbConnection, dbType, "%");
			System.out.println(jobList);
			// Got Job List
			
			// Iterate through job list to get joid after splitting and then get the last run for each job/joid
			
			for(String element: jobList)
			{
				String[] myTuple=element.split(":");
				int joid=Integer.parseInt(myTuple[1]);
				logger.debug(joid);
				String lastRunDateEpochString=dbUtils.getLastRunForJob(dbConnection, joid);
				logger.debug(lastRunDateEpochString);
				if(!lastRunDateEpochString.isEmpty())
				{
				actualjobLastRunDate=sdf.parse(sdf.format(new Date(Long.parseLong(lastRunDateEpochString))));
				}
				else
				{
					lastRunDateEpochString="0L";
				}
				logger.debug(actualjobLastRunDate);
				if(actualjobLastRunDate.before(checkjobLastRunDate))
				{
					logger.info("Job: "+myTuple[0]+" joid: "+joid+" has a last start date of :"+actualjobLastRunDate);
				}
			}
		}
		
	}
	
	
	public static void displayHelp(String option)
	{
		if(option.equalsIgnoreCase("1"))
		{
			System.out.println("Insufficient arguments to command line. Syntax: java -jar JobAnalyzer.jar <option> <jobPattern> <lastrundate>");
			System.out.println("Example: java -jar JobAnalyzer.jar \"1\" \"job_pattern\" \"date\" ");
		}
	}
	

}
