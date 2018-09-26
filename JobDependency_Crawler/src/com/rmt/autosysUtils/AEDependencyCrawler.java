/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rmt.autosysUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author kesav
 */
public class AEDependencyCrawler 
{
    static Logger logger = Logger.getRootLogger();
    public static void main(String args[]) throws FileNotFoundException, IOException, ClassNotFoundException, SQLException
    {
        String log4jLocation = "resources/log4j.properties";
	PropertyConfigurator.configure(log4jLocation);
        logger=Logger.getLogger("JilUtilities.main");
        logger.info("Application Logging Initialized...");
        String dbPropertiesFile = "resources/database.properties";
        Properties props = new Properties();
        props.load(new FileInputStream(dbPropertiesFile));
        String dbHostName = props.getProperty("AE_DB_HOSTNAME");
        String dbPortNumber = props.getProperty("AE_DB_PORT");
        String dbUser = props.getProperty("AE_DB_READUSER");
        String dbPass = props.getProperty("AE_DB_PASS");
        String dbName = props.getProperty("AE_DB_NAME");
        logger.info("Retrieved Connection details. Attempting to establish connection.");
        Class.forName("net.sourceforge.jtds.jdbc.Driver");
        String connectString = "jdbc:jtds:sqlserver://"+dbHostName+":"+dbPortNumber+":"+dbName;
        Connection sqlConnection = DriverManager.getConnection(connectString, dbUser, dbPass);
        DatabaseMetaData meta = sqlConnection.getMetaData();
        logger.info("Driver Information: "+meta.getDriverName());
        logger.info("Vendor Name: "+meta.getDatabaseProductName());
        logger.info("Database Version: "+meta.getDatabaseProductVersion());
        
    }
    
    
    
}
