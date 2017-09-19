package com.udemy.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.Logger;

public class CreateDBConnection 
{
	static Logger logger = Logger.getRootLogger();
	Connection oraConnection;
	Connection sqlServerConnection;
	Connection sybaseConnection;
	
	public Connection connect2Oracle(String dbHost, String dbPort, String dbUser, String dbPass, String dbSID) throws ClassNotFoundException, SQLException
	{
		logger=Logger.getLogger("ConversionUtilities.aeUtilities.connect2Oracle");
		Class.forName("oracle.jdbc.driver.OracleDriver");
		String connectUrl = "jdbc:oracle:thin:@//"+dbHost+":"+dbPort+"/"+dbSID;
		
		logger.info("Connection String: "+connectUrl);
		oraConnection=DriverManager.getConnection(connectUrl,dbUser,dbPass);
		return oraConnection;
		
	}

}
