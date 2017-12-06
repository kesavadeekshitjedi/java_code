package com.rmt.readers.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class DBUtils 
{
	Connection oraConnection;
	Connection sqlConnection;
	Connection sybConnection;
	Statement sqlStatement;
	ResultSet sqlResult;
	static Logger logger = Logger.getRootLogger();
	private String dbHost;
	private String dbUser;
	private String dbPass;
	private String dbName;
	private int dbPort;
	static SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
	
	public String getJobName(Connection conn, String joid, String dbType, String dbName) throws SQLException
	{
		
		String sql="";
		String dbSchema=dbName+".dbo";
		if(dbType.equalsIgnoreCase("SYBASE")) {
			dbSchema=dbName+".dbo.";
			sql="select job_name from "+dbSchema+"job where joid="+joid+"";
		}
		else if(dbType.equalsIgnoreCase("ORACLE"))
		{
			dbSchema="AEDBADMIN."; // for r11. no clue what this will be for 4.5
			sql="select job_name from "+dbSchema+"job where joid='"+joid+"'";
		}
		else if(dbType.equalsIgnoreCase("MSSQL"))
		{
			dbSchema = "dbo."; // for r11. no clue what it is for 4.5
			sql="select job_name from "+dbSchema+"job where joid='"+joid+"'";
		}
		String jobName="";
		logger=Logger.getLogger("ArchivedReaderUtilities.DBUtils.getJobName");
		
		logger.debug("getJobName SQL String: "+sql);
		try
		{
			sqlStatement = conn.createStatement();
			sqlResult = sqlStatement.executeQuery(sql);
			while(sqlResult.next())
			{
				
				jobName=sqlResult.getString("job_name");
				logger.info("Job ID: "+joid+" belongs to JOB: "+jobName);
			}
			
			
		}
		catch(SQLException se)
		{
			se.printStackTrace();
		}
		finally
		{
			sqlResult.close();
			sqlStatement.close();
			
		}
		
		return jobName;
	}
	private static Date getDateFromString(String date)
	{
		logger=Logger.getLogger("ArchivedReaderUtilities.DBUtils.getDateFromString");
		// Helper method to get current date from a string
		Date myDate=null;
		try
		{
			myDate=sdf.parse(date);
		}
		catch(ParseException pe)
		{
			logger.error(pe.toString());
		}
		return myDate;
	}
	private static Date[] getMyDateArray(int numberOfDays)
	{
		logger=Logger.getLogger("ArchivedReaderUtilities.DBUtils.getMyDateArray");
		// Helper method to get current date and date in the past. 
		Date[] dates = new Date[2];
		
		if(numberOfDays==0)
		{
			
			Date referenceDate = new Date();
			Calendar c = Calendar.getInstance(); 
			
			c.setTime(referenceDate);
			logger.debug(c.getTime());
			String todaysDate = (sdf.format(c.getTime()));
			dates[0]=getDateFromString(todaysDate);
			logger.info("Current Date: "+todaysDate);
			c.add(Calendar.MONTH, -0);
			logger.debug(c.getTime());
			String secondDate = (sdf.format(c.getTime()));
			dates[1]=getDateFromString(secondDate);
			logger.info("Past Date: "+secondDate);
			logger.debug(sdf.format(c.getTime()));
			
			
		}
		else
		{
			Date referenceDate = new Date();
			Calendar c = Calendar.getInstance(); 
			
			c.setTime(referenceDate);
			logger.debug(c.getTime());
			String todaysDate = (sdf.format(c.getTime()));
			logger.info("Current Date: "+todaysDate);
			c.add(Calendar.DATE, -numberOfDays);
			logger.debug(c.getTime());
			String secondDate = (sdf.format(c.getTime()));
			logger.info("Past Date: "+secondDate);
			logger.debug(sdf.format(c.getTime()));
			dates[0]=getDateFromString(todaysDate);
			dates[1]=getDateFromString(secondDate);
		}
		
		return dates;
	}
	public List<String> getUnusedJobs(Connection conn, String dbType, int numDays, String jobPattern, String dbName)
	{
		logger=Logger.getLogger("ArchivedReaderUtilities.DBUtils.getUnusedJobs");
		List<String> jobsToRetire = new ArrayList<String>();
		try 
		{
			logger.info("Getting jobs with pattern: "+jobPattern);
			List<String> jobSuperSet = getJobList(conn, dbType, jobPattern,dbName);
			// got the list of jobs and joids. 
			// now get teh last run date for the joid
			// Check if the last run date is beyond X days old
			
			
			logger.info("Done getting jobs with pattern: "+jobPattern);
		} 
		catch (SQLException e) 
		{


			e.printStackTrace();
		}
		
		
		return jobsToRetire;
		
	}
	
	public void getLastRunForJob(Connection conn, int joid) throws SQLException
	{
		logger=Logger.getLogger("ArchivedReaderUtilities.DBUtils.getLastRunForJob");
		//String sqlQuery = "select TO_CHAR(TO_DATE('19700101000000', 'YYYYMMDDHH24MISS')+((last_end-18000) /(60*60*24)),'YYYYMMDDHH24MISS') AS LAST_END, TO_CHAR(TO_DATE('19700101000000', 'YYYYMMDDHH24MISS')+((last_start-18000) /(60*60*24)),'YYYYMMDDHH24MISS') AS LAST_START from AEBDADMIN.ujo_jobst where job_name='"+jobName+"'";
		String sqlQuery="select last_start, last_end from"+dbName+".dbo.job_status where joid="+joid;
    	logger.debug(sqlQuery);
    	try
    	{
    		sqlStatement = conn.createStatement();
    		sqlResult = sqlStatement.executeQuery(sqlQuery);
    		while(sqlResult.next())
    		{
    			String last_StartTime=sqlResult.getString("last_start").trim();
    			String last_EndTime=sqlResult.getString("last_end").trim();
    		}
    	}
    	finally
    	{
    		sqlResult.close();
    		sqlStatement.close();
    	}
	}
	public List<String> getJobList(Connection conn, String dbType, String jobPattern, String dbName) throws SQLException
	{
		logger=Logger.getLogger("ArchivedReaderUtilities.DBUtils.getJobList");
		List<String> jobList = new ArrayList<String>();
		// Gets the list of jobs based on the pattern supplied. if the pattern is *, then all jobs in the DB are returned.
		String sql="";
		String dbSchema="";
		String tempjobName="";
		if(dbType.equalsIgnoreCase("SYBASE")) 
		{
			dbSchema=dbName+".dbo.";
			sql="select job_name,joid from "+dbSchema+"job where job_name like '"+jobPattern+"'";
			logger.debug(sql);
		}
		else if(dbType.equalsIgnoreCase("ORACLE"))
		{
			dbSchema="AEDBADMIN."; // for r11. no clue what this will be for 4.5
			sql="select job_name,joid from "+dbSchema+"job where job_name like '"+jobPattern+"'";
			logger.debug(sql);
		}
		else if(dbType.equalsIgnoreCase("MSSQL"))
		{
			dbSchema = "dbo."; // for r11. no clue what it is for 4.5
			sql="select job_name,joid from "+dbSchema+"job where job_name like '"+jobPattern+"'";
			logger.debug(sql);
			
		}
		try
		{
			sqlStatement = conn.createStatement();
			sqlResult = sqlStatement.executeQuery(sql);
			while(sqlResult.next())
			{
				tempjobName=sqlResult.getString("job_name").trim();
				int joid=sqlResult.getInt("joid");
				if(!jobList.contains(tempjobName))
				{
					jobList.add(tempjobName+":"+joid);
				}
			}
			
		}
		finally
		{
			sqlResult.close();
			sqlStatement.close();
		}
		
		
		return jobList;
	}
	public Map<String, List<String>> getCalendarDates45(Connection conn, List<String> calList, String dbType,String dbName) throws SQLException, ParseException
	{
		// takes the list of calendars and gets the dates for all calendars.
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
		
		/*Date ignoreJobDate=sdf.parse(jobIsOldDate);
		Date jobStartDate;
		Date jobEndDate;*/
		
		logger=Logger.getLogger("ArchivedReaderUtilities.DBUtils.getCalendarDates45");
		Map<String, List<String>> calendarDates = new HashMap<String, List<String>>();
		List<String> calDates;
		String dbSchema="autosys.dbo";
		String sql="";
		String tempDay="";
		String lastDateInCal="";
		if(dbType.equalsIgnoreCase("SYBASE")) {
			dbSchema=dbName+".dbo";
			//sql="select day from "+dbSchema+"";
		}
		else if(dbType.equalsIgnoreCase("ORACLE"))
		{
			dbSchema="AEDBADMIN."; // for r11. no clue what this will be for 4.5
			//sql="select name from "+dbSchema;
		}
		else if(dbType.equalsIgnoreCase("MSSQL"))
		{
			dbSchema = "dbo."; // for r11. no clue what it is for 4.5
			//sql="select name from "+dbSchema;
		}
		for(String calName:calList)
		{
			calDates=new ArrayList<String>();
			switch(dbType)
			{
			case "SYBASE":
				
				sql="select day from "+dbSchema+"calendar where name='"+calName+"'";
				logger.debug(sql);
				break;
			case "ORACLE":
				sql="select day from "+dbSchema+"calendar where name='"+calName+"'";
				logger.debug(sql);
				break;
			case "MSSQL":
				sql="select day from "+dbSchema+"calendar where name='"+calName+"'";
				logger.debug(sql);
				break;
			case "Sybase":
				sql="select day from "+dbSchema+"calendar where name='"+calName+"'";
				logger.debug(sql);
				break;
			case "Oracle":
				sql="select day from "+dbSchema+"calendar where name='"+calName+"'";
				logger.debug(sql);
				break;
			case "mssql":
				sql="select day from "+dbSchema+"calendar where name='"+calName+"'";
				logger.debug(sql);
				break;
			}
			sqlStatement=conn.createStatement();
			sqlResult=sqlStatement.executeQuery(sql);
			while(sqlResult.next())
			{
				tempDay=sqlResult.getString("day");
				String subTempString=tempDay.substring(0,10);
				//Date tempString=sdf.parse(subTempString);
				calDates.add(subTempString);
				logger.debug("Added "+subTempString);
			}
			if(!calendarDates.containsKey(calName)) 
			{
				Collections.sort(calDates);
				calendarDates.put(calName, calDates);
				lastDateInCal=calDates.get(calDates.size()-1);
				
			}
			else
			{
				logger.error("Duplicate calendar name identified: "+calName);
			}
			
		}
		
		
		return calendarDates;
	}
	public List<String> getCalendarList45(Connection conn, String dbType, String dbName) throws SQLException
	{
		logger=Logger.getLogger("ArchivedReaderUtilities.DBUtils.getCalendarList45");
		List<String> calendarList = new ArrayList<String>();
		String calendarName="";
		
		String dbSchema="autosys.dbo";
		String sql="";
		if(dbType.equalsIgnoreCase("SYBASE")) {
			dbSchema=dbName+".dbo";
			sql="select name from "+dbSchema+"calendar";
		}
		else if(dbType.equalsIgnoreCase("ORACLE"))
		{
			dbSchema="AEDBADMIN."; // for r11. no clue what this will be for 4.5
			sql="select name from "+dbSchema+"calendar";
		}
		else if(dbType.equalsIgnoreCase("MSSQL"))
		{
			dbSchema = "dbo."; // for r11. no clue what it is for 4.5
			sql="select name from "+dbSchema+"calendar";
		}
		
		logger.debug(sql);
		logger.info("Retrieving list of calendars");
		try
		{
			sqlStatement=conn.createStatement();
			sqlResult=sqlStatement.executeQuery(sql);
			while(sqlResult.next())
			{
				calendarName=sqlResult.getString("name").trim();
				if(!calendarList.contains(calendarName))
				{
					calendarList.add(calendarName);
				}
			}
		}
		
		finally
		{
			sqlResult.close();
			sqlStatement.close();
		}
		return calendarList;
	}
	public boolean doesJobExistInAE45(Connection conn, String jobName, int joid, String dbType, String dbName) throws SQLException
	{
		boolean doesExist=false;
		String dbSchema="autosys.dbo";
		String sql="";
		if(dbType.equalsIgnoreCase("SYBASE")) {
			dbSchema=dbName+".dbo.";
			sql="select job_name,joid from "+dbSchema+"job where job_name='"+jobName+"'";
		}
		else if(dbType.equalsIgnoreCase("ORACLE"))
		{
			dbSchema="AEDBADMIN."; // for r11. no clue what this will be for 4.5
			sql="select job_name,joid from "+dbSchema+"job where job_name='"+jobName+"'";
		}
		else if(dbType.equalsIgnoreCase("MSSQL"))
		{
			dbSchema = "dbo."; // for r11. no clue what it is for 4.5
			sql="select job_name,joid from "+dbSchema+"job where job_name='"+jobName+"'";
		}
		logger=Logger.getLogger("ArchivedReaderUtilities.DBUtils.doesJobExist");
		logger.info("Checking for job: "+jobName);
		//String sql="select job_name,joid from job where job_name='"+jobName+"'";
		logger.debug(sql);
		try
		{
			sqlStatement=conn.createStatement();
			sqlResult=sqlStatement.executeQuery(sql);
		
			while(sqlResult.next())
			{
				String aejobName=sqlResult.getString("job_name");
				int aejoid=sqlResult.getInt("joid");
				if(aejobName.equals(jobName))
				{
					if(aejoid==joid)
					{
						logger.info("Job exists: "+" JOB: "+jobName+" JOID: "+joid);
						doesExist=true;
					}
					else if(aejoid!=joid)
					{
						logger.info("Job exists: "+" JOB: "+jobName+" with a different JOID: "+joid);
						doesExist=true;
					}
				}
			}
		}
		finally
		{
			sqlStatement.close();
			sqlResult.close();
		}
		
		return doesExist;
	}
	


	public Connection connect2Oracle(String dbHost, String dbPort, String dbUser, String dbPass, String dbSID) throws ClassNotFoundException, SQLException
	{
		logger=Logger.getLogger("ArchivedReaderUtilities.DBUtils.connect2Oracle");
		Class.forName("oracle.jdbc.driver.OracleDriver");
		String connectUrl = "jdbc:oracle:thin:@//"+dbHost+":"+dbPort+"/"+dbSID;
		
		logger.info("Connection String: "+connectUrl);
		oraConnection=DriverManager.getConnection(connectUrl,dbUser,dbPass);
		DatabaseMetaData meta = oraConnection.getMetaData();
		System.out.println("Driver Information: "+meta.getDriverName());
		System.out.println("Vendor Name: "+meta.getDatabaseProductName());
		System.out.println("Database Version: "+meta.getDatabaseProductVersion());
		return oraConnection;
		
	}
	public Connection connect2SQLServer(String dbHost, String dbPort, String dbUser, String dbPass, String dbName) throws ClassNotFoundException, SQLException
	{
		logger=Logger.getLogger("ArchivedReaderUtilities.DBUtils.connect2SQLServer");
		Class.forName("net.sourceforge.jtds.jdbc.Driver");
		String connectUrl = "jdbc:jtds:sqlserver://"+dbHost+":"+dbPort+"/"+dbName;
		logger.info("Connection String: "+connectUrl);
		sqlConnection=DriverManager.getConnection(connectUrl,dbUser,dbPass);
		DatabaseMetaData meta = sqlConnection.getMetaData();
		System.out.println("Driver Information: "+meta.getDriverName());
		System.out.println("Vendor Name: "+meta.getDatabaseProductName());
		System.out.println("Database Version: "+meta.getDatabaseProductVersion());
		return sqlConnection;
	}
	public Connection connect2Sybase(String dbHostName, String dbPort, String dbUser, String dbPass, String dbName) throws ClassNotFoundException, SQLException
	{
		logger=Logger.getLogger("ArchivedReaderUtilities.DBUtils.connect2Sybase");
		Class.forName("net.sourceforge.jtds.jdbc.Driver");
		String connectUrl="jdbc:jtds:sybase://"+dbHostName+":"+dbPort;
		logger.info("Connection String: "+connectUrl);
		sybConnection=DriverManager.getConnection(connectUrl,dbUser,dbPass);
		DatabaseMetaData meta = sybConnection.getMetaData();
		System.out.println("Driver Information: "+meta.getDriverName());
		System.out.println("Vendor Name: "+meta.getDatabaseProductName());
		System.out.println("Database Version: "+meta.getDatabaseProductVersion());
		return sybConnection;
		
	}

}
