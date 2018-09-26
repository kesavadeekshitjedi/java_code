/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rmt.autosysUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
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
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author kesav
 */
public class AEDependencyCrawler 
{
    static Logger logger = Logger.getRootLogger();
    static Connection sqlConnection;
    static List<String> processedJobList = new ArrayList<String>();
    static List<String> masterJobDepList = new ArrayList<String>();
    static List<String> dependentJobList = new ArrayList<String>();
    static int levelCounter=0;
    static BufferedWriter depWriter;
    static FileWriter fw;
    public static void main(String args[]) throws FileNotFoundException, IOException, ClassNotFoundException, SQLException
    {
        String log4jLocation = "resources/log4j.properties";
	PropertyConfigurator.configure(log4jLocation);
        logger=Logger.getLogger("AEDependencyCrawler.main");
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
        logger.debug(dbHostName);
        logger.debug(dbPortNumber);
        logger.debug(dbName);
        Class.forName("net.sourceforge.jtds.jdbc.Driver");
        String connectString = "jdbc:jtds:sqlserver://"+dbHostName+":"+dbPortNumber+"/"+dbName;
        Connection sqlConnection = DriverManager.getConnection(connectString, dbUser, dbPass);
        DatabaseMetaData meta = sqlConnection.getMetaData();
        logger.info("Driver Information: "+meta.getDriverName());
        logger.info("Vendor Name: "+meta.getDatabaseProductName());
        logger.info("Database Version: "+meta.getDatabaseProductVersion());
        System.out.println("Enter the name of the job to process dependencies: ");
        InputStreamReader consoleReader = new InputStreamReader(System.in);
        BufferedReader consoleBuffer = new BufferedReader(consoleReader);
        String depJob = consoleBuffer.readLine();
        System.out.println("Enter the output file name:");
        String outputFile = consoleBuffer.readLine();
        fw = new FileWriter(outputFile);
        depWriter = new BufferedWriter(fw);
        depWriter.write("JOB,JOB_DEPENDENCY,LEVEL"+" \n");
        logger.info("Getting Job Dependency information for: "+depJob);
        AEDependencyCrawler.getDependentJobList(sqlConnection, depJob);
        processedJobList.add(depJob);
        for(int i=0;i<=dependentJobList.size()-1;i++)
        {
            logger.info("Getting Job Dependency information for :"+dependentJobList.get(0));
            if(!processedJobList.contains(dependentJobList.get(0)))
            {
                AEDependencyCrawler.getDependentJobList(sqlConnection, dependentJobList.get(0));
                processedJobList.add(dependentJobList.get(0));
                
            }
            dependentJobList.remove(dependentJobList.get(0));
        }
        for(int j=0;j<=masterJobDepList.size()-1;j++)
        {
            depWriter.write(masterJobDepList.get(j)+" \n");
        }
        depWriter.close();
        fw.close();
    }
    private static int getJobID(Connection sqlConn, String jobName)
    {
        int joid=0;
        String getJoidSQL=null;
        Statement getJoidStmt = null;
        ResultSet getJoidRslt = null;
        
        logger=Logger.getLogger("AEDependencyCrawler.getJobID");
        try
        {
            if(!sqlConn.isClosed())
            {
                logger.debug("Connection open.");
                getJoidSQL="select joid from ujo_job where job_name='"+jobName+"' and is_active=\'1\' and is_currver=\'1\'";
                logger.debug(getJoidSQL);
                getJoidStmt = sqlConn.createStatement();
                getJoidRslt = getJoidStmt.executeQuery(getJoidSQL);
                while(getJoidRslt.next())
                {
                    joid = getJoidRslt.getInt("joid");
                    logger.debug("Joid for job: "+jobName+" is: "+joid);
                }
            }
        }
        catch(SQLException se)
        {
            logger.error(se);
            se.printStackTrace();
        }
        return joid;
    }
    
    private static void getDependentJobList(Connection sqlConn, String jobName) throws SQLException
    {
        logger=Logger.getLogger("AEDependencyCrawler.getDependentJobs");
        logger.info("Attempting to retrieve joid for "+jobName);
        int joid = AEDependencyCrawler.getJobID(sqlConn, jobName);
        logger.info("JOID retrieved..."+joid);
        String getDependentJobSQL = null;
        Statement getDependentJobStmt = null;
        ResultSet getDependentJobRslt = null;
        String condJobName="NO_FURTHER_DEPENDENCIES";
        if(!sqlConn.isClosed())
        {
            getDependentJobSQL = "select cond.cond_job_name from ujo_job_cond cond,ujo_job job where cond.joid=job.joid and cond.joid='"+joid+"' and (type='s' or type='S' or type='f' or type='F' or type='t' or type='T' or type='n' or type='N' or type='d' or type='D' or type='e' or type='E') and (job.is_active='1' and job.is_currver='1')";
            logger.debug(getDependentJobSQL);
            getDependentJobStmt=sqlConn.createStatement();
            getDependentJobRslt=getDependentJobStmt.executeQuery(getDependentJobSQL);
            while(getDependentJobRslt.next())
            {
                condJobName = getDependentJobRslt.getString("cond_job_name");
                logger.debug("Job: "+jobName+" has a dependency: "+condJobName);
                dependentJobList.add(condJobName);
                masterJobDepList.add(jobName+","+condJobName+","+levelCounter);
            }
            levelCounter++;
           
        }
        
    }
    
    
}
