package com.rmt.main;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.rmt.readers.database.DBUtils;
import com.rmt.readers.file.ArchiveJobRunsReader_45;

public class ArchiveReader 
{
	static Logger logger = Logger.getRootLogger();
	static Connection dbConnection;

	public static void main(String[] args) throws FileNotFoundException, IOException 
	{
		String log4jLocation = "resources/log4j.properties";
		PropertyConfigurator.configure(log4jLocation);
		logger=Logger.getLogger("ArchivedFileReaderUtils.main");
		Properties aejobProps = new Properties();
		aejobProps.load(new FileInputStream("resources/archives.properties"));
		String archiveFolder=aejobProps.getProperty("ARCHIVE_FOLDER");
		ArchiveJobRunsReader_45 ajobs45 = new ArchiveJobRunsReader_45();
		ajobs45.readJobRunsArchive(archiveFolder);
		
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
		if(dbType.equalsIgnoreCase("Oracle"))
		{
			try
			{
				dbConnection=db.connect2Oracle(dbHostName,dbPort,dbUser,dbPass,dbName);
				logger.info("Oracle Database connection established");
			} 
		
			catch (ClassNotFoundException | SQLException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally
			{
				try 
				{
					dbConnection.close();
					logger.info("Oracle Database connection closed");
				} 
				catch (SQLException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		else if(dbType.equalsIgnoreCase("MSSQL"))
		{
			try
			{
				dbConnection=db.connect2SQLServer(dbHostName,dbPort,dbUser,dbPass,dbName);
				logger.info("SQL Server Database connection established");
			} 
		
			catch (ClassNotFoundException | SQLException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally
			{
				try 
				{
					dbConnection.close();
					logger.info("SQL Server Database connection closed");
				} 
				catch (SQLException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		else if(dbType.equalsIgnoreCase("Sybase"))
		{
			try
			{
				dbConnection=db.connect2Sybase(dbHostName, dbPort, dbUser, dbPass, dbName);
				logger.info("Sybase Database connection established");
			} 
		
			catch (ClassNotFoundException | SQLException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally
			{
				try 
				{
					dbConnection.close();
					logger.info("Sybase Database connection closed");
				} 
				catch (SQLException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		

	}

}
