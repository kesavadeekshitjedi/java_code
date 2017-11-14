package com.rmt.utilities;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class DBStatusParser 
{
	Connection databaseConnection;
	Statement sqlStatement;
	ResultSet sqlResultSet;
	static Logger logger = Logger.getRootLogger();
	List<String> autosysJobList;
	Map<String, String> jobStatusMap = new HashMap<String,String>();
	
	public Connection connect2Oracle(String dbHost, String dbPort, String dbUser, String dbPass, String dbSID) throws ClassNotFoundException, SQLException
	{
		logger=Logger.getLogger("JilUtilities.DBStatusParser.connect2Oracle");
		Class.forName("oracle.jdbc.driver.OracleDriver");
		String connectUrl = "jdbc:oracle:thin:@//"+dbHost+":"+dbPort+"/"+dbSID;
		
		logger.info("Connection String: "+connectUrl);
		databaseConnection=DriverManager.getConnection(connectUrl,dbUser,dbPass);
		DatabaseMetaData meta = databaseConnection.getMetaData();
		System.out.println("Driver Information: "+meta.getDriverName());
		System.out.println("Vendor Name: "+meta.getDatabaseProductName());
		System.out.println("Database Version: "+meta.getDatabaseProductVersion());
		return databaseConnection;
		
	}
	public Connection connect2SQLServer(String dbHost, String dbPort, String dbUser, String dbPass, String dbName) throws ClassNotFoundException, SQLException
	{
		logger=Logger.getLogger("JilUtilities.DBStatusParser.connect2SQLServer");
		Class.forName("net.sourceforge.jtds.jdbc.Driver");
		String connectUrl = "jdbc:jtds:sqlserver://"+dbHost+":"+dbPort+"/"+dbName;
		logger.info("Connection String: "+connectUrl);
		databaseConnection=DriverManager.getConnection(connectUrl,dbUser,dbPass);
		DatabaseMetaData meta = databaseConnection.getMetaData();
		System.out.println("Driver Information: "+meta.getDriverName());
		System.out.println("Vendor Name: "+meta.getDatabaseProductName());
		System.out.println("Database Version: "+meta.getDatabaseProductVersion());
		return databaseConnection;
	}
	public Connection connect2Sybase(String dbHostName, String dbPort, String dbUser, String dbPass, String dbName) throws ClassNotFoundException, SQLException
	{
		logger=Logger.getLogger("JilUtilities.DBStatusParser.connect2Sybase");
		Class.forName("net.sourceforge.jtds.jdbc.Driver");
		String connectUrl="jdbc:jtds:sybase://"+dbHostName+":"+dbPort;
		logger.info("Connection String: "+connectUrl);
		databaseConnection=DriverManager.getConnection(connectUrl,dbUser,dbPass);
		DatabaseMetaData meta = databaseConnection.getMetaData();
		System.out.println("Driver Information: "+meta.getDriverName());
		System.out.println("Vendor Name: "+meta.getDatabaseProductName());
		System.out.println("Database Version: "+meta.getDatabaseProductVersion());
		return databaseConnection;
		
	}

	
	public void prefetchJobNames(Connection conn, String dbType) throws SQLException
	{
		String dbSchema=null;
		String sql=null;
		 // this is designed to work for the 11.0+ database types.
		logger=Logger.getLogger("JilUtilities.DBStatusParser.prefetchJobNames");
		autosysJobList = new ArrayList<String>();
		if(dbType.equalsIgnoreCase("SYBASE")) {
			dbSchema="";
			sql="select job_name from "+dbSchema+"ujo_job where is_active=1 and is_currver=1";
		}
		else if(dbType.equalsIgnoreCase("ORACLE") || (dbType.equalsIgnoreCase("ORA")))
		{
			dbSchema="AEDBADMIN."; // for r11. no clue what this will be for 4.5
			sql="select job_name from "+dbSchema+"ujo_job where is_active=1 and is_currver=1";
		}
		else if(dbType.equalsIgnoreCase("MSSQL"))
		{
			dbSchema = "dbo."; // for r11. no clue what it is for 4.5
			sql="select job_name from "+dbSchema+"ujo_job where is_active=1 and is_currver=1";
		}
		logger.debug(sql);
		try
		{
			sqlStatement = conn.createStatement();
			sqlResultSet = sqlStatement.executeQuery(sql);
			while(sqlResultSet.next())
			{
				String jobName=sqlResultSet.getString("job_name");
				if(!autosysJobList.contains(jobName))
				{
					autosysJobList.add(jobName);
				}
			}
		}
		catch(Exception se)
		{
			se.printStackTrace();
		}
		
		
	}
	public void prefetchJobNamesr11(Connection conn, String dbType) throws SQLException
	{
		String dbSchema=null;
		String sql=null;
		 // this is designed to work for the 11.0+ database types.
		logger=Logger.getLogger("JilUtilities.DBStatusParser.prefetchJobNames");
		autosysJobList = new ArrayList<String>();
		if(dbType.equalsIgnoreCase("SYBASE")) {
			dbSchema="";
			sql="select job_name from "+dbSchema+"ujo_job";
		}
		else if(dbType.equalsIgnoreCase("ORACLE") || (dbType.equalsIgnoreCase("ORA")))
		{
			dbSchema="mdbadmin."; // for r11. no clue what this will be for 4.5
			sql="select job_name from "+dbSchema+"ujo_job";
		}
		else if(dbType.equalsIgnoreCase("MSSQL"))
		{
			dbSchema = "dbo."; // for r11. no clue what it is for 4.5
			sql="select job_name from "+dbSchema+"ujo_job";
		}
		logger.debug(sql);
		try
		{
			sqlStatement = conn.createStatement();
			sqlResultSet = sqlStatement.executeQuery(sql);
			while(sqlResultSet.next())
			{
				String jobName=sqlResultSet.getString("job_name");
				if(!autosysJobList.contains(jobName))
				{
					autosysJobList.add(jobName);
				}
			}
		}
		catch(Exception se)
		{
			se.printStackTrace();
		}
		
		logger.info("Done with pre-fetch mechanism");
		sqlResultSet.close();
		sqlStatement.close();
	}
	public void close() throws SQLException
	{
		sqlResultSet.close();
		sqlStatement.close();
	}
	public Map<String, String> getJobStatusr110(Connection conn, String dbType) throws Exception
	{
		autosysJobList=new ArrayList<String>(); // re-initializing the List so we dont use a stale list from another operation.
		prefetchJobNamesr11(conn, dbType);
		Map<Integer, String> statusMap = new HashMap<Integer, String>();
		statusMap.put(1, "RU");
		statusMap.put(3, "ST");
		statusMap.put(4, "SU");
		statusMap.put(5, "FA");
		statusMap.put(6, "TE");
		statusMap.put(7, "OI");
		statusMap.put(8,"IN");
		statusMap.put(9, "AC");
		statusMap.put(10, "RE");
		statusMap.put(11, "OH");
		statusMap.put(14, "PE");
		
		String dbSchema=null;
		String sql=null;
		String jobStatusString=null;
		 // this is designed to work for the 11.0+ database types.
		logger=Logger.getLogger("JilUtilities.DBStatusParser.getJobStatusr110");
		logger.info("Initialize Map...");	
		jobStatusMap=new HashMap<String,String>(); // Map is now wiped out and no stale entries are left in it.
		for(int i=0;i<autosysJobList.size();i++)
		{
			logger.debug("Getting job status for job: "+autosysJobList.get(i));
			if(dbType.equalsIgnoreCase("SYBASE")) {
				dbSchema="autosys.dbo.";
				sql="select status from "+dbSchema+"ujo_jobst where job_name='"+autosysJobList.get(i)+"'";
			}
			else if(dbType.equalsIgnoreCase("ORACLE") || (dbType.equalsIgnoreCase("ORA")))
			{
				dbSchema="MDBADMIN."; // for r11. no clue what this will be for 4.5
				sql="select status from mdbadmin.ujo_jobst where job_name='"+autosysJobList.get(i)+"'";
			}
			else if(dbType.equalsIgnoreCase("MSSQL"))
			{
				dbSchema = "dbo."; // for r11. no clue what it is for 4.5
				sql="select job_name from "+dbSchema+"ujo_jobst where job_name='"+autosysJobList.get(i)+"'";
			}
			Statement sqlStatement1 = null;
			ResultSet sqlResultSet1 = null;
			try
			{
				sqlStatement1=conn.createStatement();
				sqlResultSet1=sqlStatement1.executeQuery(sql);
				while(sqlResultSet1.next())
				{
					int jobStatusInt = sqlResultSet1.getInt("status");
					logger.debug("Status: "+jobStatusInt);
					jobStatusString=statusMap.get(jobStatusInt);
					jobStatusMap.put(autosysJobList.get(i), jobStatusString);
					
				}
				
			}
		
			catch(Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				sqlResultSet1.close();
				sqlStatement1.close();
			}
		}
		logger.info("Done getting status");
		System.out.println(jobStatusMap);
		return jobStatusMap;
	}
	public void getJobStatusr113x(Connection conn, String dbType) throws SQLException
	{
		prefetchJobNames(conn, dbType);
		System.out.println("test");
		Map<Integer, String> statusMap = new HashMap<Integer, String>();
		statusMap.put(1, "RU");
		statusMap.put(3, "ST");
		statusMap.put(4, "SU");
		statusMap.put(5, "FA");
		statusMap.put(6, "TE");
		statusMap.put(7, "OI");
		statusMap.put(8,"IN");
		statusMap.put(9, "AC");
		statusMap.put(10, "RE");
		statusMap.put(11, "OH");
		statusMap.put(14, "PE");
		
		String dbSchema=null;
		String sql=null;
		String jobStatusString=null;
		 // this is designed to work for the 11.3+ database schemas.
		logger=Logger.getLogger("JilUtilities.DBStatusParser.getJobStatusr113x");
		
		/*if(dbType.equalsIgnoreCase("SYBASE")) {
			dbSchema="";
			sql="select status from "+dbSchema+"ujo_jobst where is_active=1 and is_currver=1";
		}
		else if(dbType.equalsIgnoreCase("ORACLE"))
		{
			dbSchema="AEDBADMIN."; // for r11. no clue what this will be for 4.5
			sql="select status from "+dbSchema+"ujo_job where is_active=1 and is_currver=1";
		}
		else if(dbType.equalsIgnoreCase("MSSQL"))
		{
			dbSchema = "dbo."; // for r11. no clue what it is for 4.5
			sql="select job_name from "+dbSchema+"ujo_job where is_active=1 and is_currver=1";
		}*/
		logger.debug(sql);
		for(int i=0;i<autosysJobList.size();i++)
		{
			logger.debug("Getting job status for job: "+autosysJobList.get(i));
			if(dbType.equalsIgnoreCase("SYBASE")) {
				dbSchema="autosys.dbo.";
				sql="select status from "+dbSchema+"ujo_jobst where job_name='"+autosysJobList.get(i)+"'";
			}
			else if(dbType.equalsIgnoreCase("ORACLE"))
			{
				dbSchema="AEDBADMIN."; // for r11. no clue what this will be for 4.5
				sql="select status from "+dbSchema+"ujo_jobst where job_name='"+autosysJobList.get(i)+"'";
			}
			else if(dbType.equalsIgnoreCase("MSSQL"))
			{
				dbSchema = "dbo."; // for r11. no clue what it is for 4.5
				sql="select job_name from "+dbSchema+"ujo_jobst where job_name='"+autosysJobList.get(i)+"'";
			}
			try
			{
				sqlStatement=conn.createStatement();
				sqlResultSet=sqlStatement.executeQuery(sql);
				while(sqlResultSet.next())
				{
					int jobStatusInt = sqlResultSet.getInt("status");
					logger.debug("Status: "+jobStatusInt);
					jobStatusString=statusMap.get(jobStatusInt);
					jobStatusMap.put(autosysJobList.get(i), jobStatusString);
					sqlResultSet.close();
					sqlStatement.close();
				}
				
			}
		
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		close();
		System.out.println(jobStatusMap);
	}
}
