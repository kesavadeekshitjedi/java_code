package com.rmt.database;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

public class autosysDBUtils 
{
	static Logger logger = Logger.getRootLogger();
	Connection dbConnection;
	Connection oraConnection;
	Connection sqlConnection;
	Connection sybConnection;
	Statement sqlStatement;
	ResultSet sqlResult;
	private String dbHost;
	private String dbUser;
	private String dbPass;
	private String dbName;
	private int dbPort;
	
	public void autosysDBUtils()
	{
		
		
	}
	public String getLastRunForJob(Connection conn, int joid) throws SQLException
	{
		logger=Logger.getLogger("JobAnalyzer.autosysDBUtils.getLastRunForJob");
		String lastStart="";
		String sqlQuery="select last_start, last_end from autosys.dbo.job_status where joid="+joid;
    	logger.debug(sqlQuery);
    	try
    	{
    		sqlStatement = conn.createStatement();
    		sqlResult = sqlStatement.executeQuery(sqlQuery);
    		while(sqlResult.next())
    		{
    			lastStart=sqlResult.getString("last_start").trim();
    			String last_EndTime=sqlResult.getString("last_end").trim();
    		}
    	}
    	finally
    	{
    		sqlResult.close();
    		sqlStatement.close();
    	}
    	return lastStart;
	}
	public String getInstanceName(Connection conn, String dbType) throws SQLException
	{
		// return the AutoSys instance name
		String aeInstanceName="";
		String sql="";
		String dbSchema="";
		String tempjobName="";
		if(dbType.equalsIgnoreCase("SYBASE")) 
		{
			dbSchema="autosys.dbo.";
			sql="select str_val from "+dbSchema+"alamode where type='AUTOSERV'";
			logger.debug(sql);
		}
		else if(dbType.equalsIgnoreCase("ORACLE"))
		{
			dbSchema="AEDBADMIN."; // for r11. no clue what this will be for 4.5
			sql="select str_val from "+dbSchema+"alamode where type='AUTOSERV'";
			logger.debug(sql);
		}
		else if(dbType.equalsIgnoreCase("MSSQL"))
		{
			dbSchema = "dbo."; // for r11. no clue what it is for 4.5
			sql="select str_val from "+dbSchema+"alamode where type='AUTOSERV'";
			logger.debug(sql);
			
		}
		try
		{
			sqlStatement=conn.createStatement();
			sqlResult=sqlStatement.executeQuery(sql);
			while(sqlResult.next())
			{
				aeInstanceName=sqlResult.getString("str_val");
			}
		}
		finally
		{
			sqlResult.close();
			sqlStatement.close();
		}
		return aeInstanceName;
	}
	public List<String> getJobList(Connection conn, String dbType, String jobPattern) throws SQLException
	{
		logger=Logger.getLogger("JobAnalyzer.autosysDBUtils.getJobList");
		List<String> jobList = new ArrayList<String>();
		// Gets the list of jobs based on the pattern supplied. if the pattern is *, then all jobs in the DB are returned.
		String sql="";
		String dbSchema="";
		String tempjobName="";
		if(dbType.equalsIgnoreCase("SYBASE")) 
		{
			dbSchema="autosys.dbo.";
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
	public Connection connect2Oracle(String dbHost, String dbPort, String dbUser, String dbPass, String dbSID) throws ClassNotFoundException, SQLException
	{
		logger=Logger.getLogger("JobAnalyzer.autosysDBUtils.connect2Oracle");
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
		logger=Logger.getLogger("JobAnalyzer.autosysDBUtils.connect2SQLServer");
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
		logger=Logger.getLogger("JobAnalyzer.autosysDBUtils.connect2Sybase");
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
