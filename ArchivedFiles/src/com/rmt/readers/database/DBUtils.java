package com.rmt.readers.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
	
	
	public String getJobName(Connection conn, String joid, String dbType) throws SQLException
	{
		String sql="";
		String dbSchema="autosys.dbo";
		if(dbType.equalsIgnoreCase("SYBASE")) {
			dbSchema="autosys.dbo.";
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
		
		logger.debug("SQL String: "+sql);
		try
		{
			sqlStatement = conn.createStatement();
			sqlResult = sqlStatement.executeQuery(sql);
			while(sqlResult.next())
			{
				
				jobName=sqlResult.getString("job_name");
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

	public boolean doesJobExistInAE45(Connection conn, String jobName, int joid, String dbType) throws SQLException
	{
		boolean doesExist=false;
		String dbSchema="autosys.dbo";
		String sql="";
		if(dbType.equalsIgnoreCase("SYBASE")) {
			dbSchema="autosys.dbo.";
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
