package com.rmt.utilities;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class GetJobDefinition 
{
	
	static Logger logger = Logger.getRootLogger();
	Connection oraConnection;
	Connection sybConnection;
	Connection sqlConnection;
	List<String> jobInformationList = new ArrayList<String>();
	Map<String, List<String>> jobInfoMap = new HashMap<String, List<String>>();
	
	
	public void getJobDefinition(Connection conn,String jobName, int versionNumber)
	{
		
	}
	public void getVersionsForJob(Connection conn,String jobName)
	{
		
	}
	public void prefectchJobNames()
	{
		//  First read the Database.properties file
		// Create the Database connection and show metadata information
		// Retrieve job names in the DB with is_active, is_currver and joid. Store this in a Map
		
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
	public void closeDBConnection(Connection conn)
	{
		try
		{
			conn.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
}
