package aeUtilities;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

public class AEDatabaseUtils 
{
	Connection oraConnection;
	Connection sqlConnection;
	Connection sybConnection;
	static Logger logger = Logger.getRootLogger();
	public Map<String, String> getDBProperties(String dbPropertiesFile) throws FileNotFoundException, IOException, ClassNotFoundException, SQLException
	{
		logger=Logger.getLogger("ConversionUtilities.aeUtilities.getDBProperties");
		Map<String, String> jobStatusMap = new HashMap<String,String>();
		Properties props = new Properties();
		props.load(new FileInputStream(dbPropertiesFile));
		String dbHostName=props.getProperty("AEDB_HOST");
		String dbPort=props.getProperty("AEDB_DB_PORT");
		String dbType=props.getProperty("AEDB_DB_TYPE");
		String dbName=props.getProperty("AEDB_DB_SID");
		String dbUser=props.getProperty("AEDB_DB_USER");
		String dbPass = props.getProperty("AEDB_DB_PASS");
		
		if(dbType.equalsIgnoreCase("ORA"))
		{
			connect2Oracle(dbHostName, dbPort, dbUser, dbPass, dbName);
			if(!oraConnection.isClosed())
			{
				logger.info("Oracle connection open");
				DatabaseMetaData meta = oraConnection.getMetaData();
				System.out.println("Driver Information: "+meta.getDriverName());
				System.out.println("Database Name: "+meta.getDatabaseProductName());
				System.out.println("Database Version: "+meta.getDatabaseProductVersion());
				Map<String, Integer> joidMap=prefetchJobNames(sybConnection,"AEDBADMIN");
				oraConnection.close();
			}
		}
		else if(dbType.equalsIgnoreCase("SQL"))
		{
			connect2SQLServer(dbHostName, dbPort, dbUser, dbPass, dbName);
			if(!sqlConnection.isClosed())
			{
				logger.info("SQL Server connection open");
				DatabaseMetaData meta = sqlConnection.getMetaData();
				System.out.println("Driver Information: "+meta.getDriverName());
				System.out.println("Database Name: "+meta.getDatabaseProductName());
				System.out.println("Database Version: "+meta.getDatabaseProductVersion());
				Map<String, Integer> joidMap=prefetchJobNames(sybConnection,"dbo");
				sqlConnection.close();
			}
		}
		else if(dbType.equalsIgnoreCase("SYB"))
		{
			connect2Sybase(dbHostName, dbPort, dbUser, dbPass, dbName);
			if(!sybConnection.isClosed())
			{
				logger.info("Sybase connection open");
				DatabaseMetaData meta = sybConnection.getMetaData();
				System.out.println("Driver Information: "+meta.getDriverName());
				System.out.println("Database Name: "+meta.getDatabaseProductName());
				System.out.println("Database Version: "+meta.getDatabaseProductVersion());
				Map<String, Integer> joidMap=prefetchJobNames(sybConnection,"dbo");
				sybConnection.close();
			}
		}
		return jobStatusMap;
		
		
	}
	public Map<String, Integer> prefetchJobNames(Connection conn, String dbOwner) throws SQLException
	{
		Map<String, Integer> joidMap = new HashMap<String,Integer>();
		String jobSQL="select job_name,joid from "+dbOwner+".ujo_job where is_active='1' and is_currver='1'";
		Statement jobSt = conn.createStatement();
		ResultSet jobRs = jobSt.executeQuery(jobSQL);
		while(jobRs.next())
		{
			String jobName=jobRs.getString("job_name");
			int joid=jobRs.getInt("joid");
			if(joidMap.containsKey(jobName))
				
			{
				joidMap.put(jobName, joid);
			}
			logger.info("Prefetch Operation complete");
		}
		return joidMap;
		
	}
	public Connection connect2Oracle(String dbHost, String dbPort, String dbUser, String dbPass, String dbSID) throws ClassNotFoundException, SQLException
	{
		logger=Logger.getLogger("ConversionUtilities.aeUtilities.connect2Oracle");
		Class.forName("oracle.jdbc.driver.OracleDriver");
		String connectUrl = "jdbc:oracle:thin:@//"+dbHost+":"+dbPort+"/"+dbSID;
		
		logger.info("Connection String: "+connectUrl);
		oraConnection=DriverManager.getConnection(connectUrl,dbUser,dbPass);
		return oraConnection;
		
	}
	public Connection connect2SQLServer(String dbHost, String dbPort, String dbUser, String dbPass, String dbName) throws ClassNotFoundException, SQLException
	{
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		String connectUrl = "jdbc:jtds:sqlserver://"+dbHost+":"+dbPort+"/"+dbName;
		logger.info("Connection String: "+connectUrl);
		sqlConnection=DriverManager.getConnection(connectUrl,dbUser,dbPass);
		return sqlConnection;
	}
	public Connection connect2Sybase(String dbHostName, String dbPort, String dbUser, String dbPass, String dbName) throws ClassNotFoundException, SQLException
	{
		
		Class.forName("net.sourceforge.jtds.jdbc.Driver");
		String connectUrl="jdbc:jtds:sybase://"+dbHostName+":"+dbPort;
		logger.info("Connection String: "+connectUrl);
		sybConnection=DriverManager.getConnection(connectUrl,dbUser,dbPass);
		return sybConnection;
		
	}

}
