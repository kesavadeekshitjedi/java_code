package com.rmt.readers.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.rmt.readers.database.DBUtils;

public class ArchiveJobRunsReader_45 
{
	
	static Logger logger = Logger.getRootLogger();
	FileReader archiveFileReader;
	BufferedReader archiveFileBuffer;
	Map<String, List<String>> archivedJobMap = new HashMap<String, List<String>>();
	List<String> joidList = new ArrayList<String>();
	static Connection dbConnection;
	FileWriter goodJobFileWriter;
	BufferedWriter goodJobBuffer;
	FileWriter badJobsFileWriter;
	BufferedWriter badJobsBuffer;
	public void close(Connection conn) throws SQLException
	{
		logger=Logger.getLogger("ArchivedFileReaderUtils.ArchiveJobRunsReader_45.close");
		conn.close();
		logger.info("Database connection closed");
	}
	public void readJobRunsArchive() throws IOException, SQLException, ParseException
	{
	/*
	 * This is the order of the columns in AutoSys 4.5 archived_job_runs file
	 * joid,run_num,ntry,startime,endtime,status,exit_code,runtime,evt_num,machine
	 * 
	 */
		Properties aejobProps = new Properties();
		aejobProps.load(new FileInputStream("resources/archives.properties"));
		String archiveFolder=aejobProps.getProperty("ARCHIVE_FOLDER");
		String goodJobsFile=aejobProps.getProperty("GOOD_JOBLIST");
		String badJobsFie=aejobProps.getProperty("BAD_JOBLIST");
		String jobIsOldDate=aejobProps.getProperty("JOB_IGNORE_START_DATE");
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:MM:ss");
		
		Date ignoreJobDate=sdf.parse(jobIsOldDate);
		Date jobStartDate;
		Date jobEndDate;
		
		String currentArchiveLine="";
		String jobName="";
		String jobID="";
		String jobRunMachine="";
		String jobRunNumber="";
		String jobNtry="";
		String jobStartTime="";
		String jobEndTime="";
		String jobRunTime="";
		String jobStatus="";
		String jobExitCode="";
		String jobRunEventNumber="";
		
		logger=Logger.getLogger("ArchivedFileReaderUtils.ArchiveJobRunsReader_45.readJobRunsArchive");
		Path p = Paths.get(archiveFolder);
		Path folderPath = p.getParent();
		System.out.println("The archive folder to look for archived_job_runs files will be: "+archiveFolder);
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
		logger.info("Database type is "+dbType);
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
			
		}
		FilenameFilter textFilter = new FilenameFilter()
				{
					public boolean accept(File dir, String name) 
					{
						String lowercaseName = name.toLowerCase();
						if ((lowercaseName.startsWith("archived_job_runs"))) 
						{
							return true;
						} 
						else 
						{
							return false;
						}
					}
				};
				File f = new File(archiveFolder);
				File[] files = f.listFiles(textFilter);
				for(File file : files)
				{
					archiveFileReader = new FileReader(file);
					archiveFileBuffer = new BufferedReader(archiveFileReader);
					System.out.println("Reading file: "+file);
					System.out.println("---------------------");
					logger.info("Reading file: "+file);
					Instant start=Instant.now(); 
					String myJobName="";
					while((currentArchiveLine=archiveFileBuffer.readLine())!=null)
					{
						List<String> jobAttrList = new ArrayList<String>();
						logger.debug(currentArchiveLine);
						String currentLine=currentArchiveLine.trim();
						String[] archiveLineTuple=currentLine.split("\\|");
						logger.debug(archiveLineTuple.length);
						if(archiveLineTuple.length==10)
						{
							jobID=archiveLineTuple[0].trim();
							jobRunNumber=archiveLineTuple[1].trim();
							jobNtry=archiveLineTuple[2].trim();
							String jobStartTimeEpoch=archiveLineTuple[3].trim();
							String jobEndTimeEpoch=archiveLineTuple[4].trim();
							logger.debug(jobStartTimeEpoch);
							logger.debug(jobEndTimeEpoch);
							jobStatus=archiveLineTuple[5].trim();
							jobExitCode=archiveLineTuple[6].trim();
							jobRunTime=archiveLineTuple[7].trim();
							jobRunEventNumber=archiveLineTuple[8].trim();
							jobRunMachine=archiveLineTuple[9].trim();
							
							jobStartTime=sdf.format(new Date(Long.parseLong(jobStartTimeEpoch)));
							
							jobEndTime=sdf.format(new Date(Long.parseLong(jobEndTimeEpoch)));
							logger.debug("Job ID: "+jobID+" Job Start: "+jobStartTime+" Job End Time: "+jobEndTime);
							jobStartDate=sdf.parse(jobStartTime);
							jobEndDate=sdf.parse(jobEndTime);
							myJobName=db.getJobName(dbConnection, jobID,dbType,dbName);
							if(jobStartDate.compareTo(ignoreJobDate)<0)
							{
								jobStartTime+="(OLD)";
								jobEndTime+="(OLD)";
							}
						}
						else if(archiveLineTuple.length==9)
						{
							jobID=archiveLineTuple[0].trim();
							jobRunNumber=archiveLineTuple[1].trim();
							jobNtry=archiveLineTuple[2].trim();
							String jobStartTimeEpoch=archiveLineTuple[3].trim();
							String jobEndTimeEpoch=archiveLineTuple[4].trim();
							jobStatus=archiveLineTuple[5].trim();
							jobExitCode=archiveLineTuple[6].trim();
							jobRunTime=archiveLineTuple[7].trim();
							jobRunEventNumber=archiveLineTuple[8].trim();
							jobStartTime=sdf.format(new Date(Long.parseLong(jobStartTimeEpoch)));
							jobEndTime=sdf.format(new Date(Long.parseLong(jobEndTimeEpoch)));
							logger.debug("Job ID: "+jobID+" Job Start: "+jobStartTime+" Job End Time: "+jobEndTime);
							jobStartDate=sdf.parse(jobStartTime);
							jobEndDate=sdf.parse(jobEndTime);
							myJobName=db.getJobName(dbConnection, jobID,dbType,dbName);
							if(jobStartDate.compareTo(ignoreJobDate)<0)
							{
								jobStartTime+="(OLD)";
								jobEndTime+="(OLD)";
							}
							
							
						}
						jobAttrList.add("JOBNAME= "+myJobName+"= RUN_NUM= "+jobRunNumber+"= NTRYS= "+jobNtry+"= STATUS= "+jobStatus+"= START_TIME= "+jobStartTime+"= END_TIME= "+jobEndTime+"= EXIT_CODE: "+jobExitCode);
						if(!archivedJobMap.containsKey(jobID))
						{
							archivedJobMap.put(jobID, jobAttrList);
						}
						//System.out.println(archivedJobMap);
						logger.info("Done retrieving job information from archive file");
						
						
						
						
					}
					
					Instant end=Instant.now();
					Duration elapsedTime = Duration.between(start, end);
					System.out.println("----------------------");
					System.out.println("Done with file: "+file+" in (ms)"+elapsedTime.toMillis());
					logger.info("Done reading file: "+file+" in "+elapsedTime.toMillis()+" (ms)");
					
						
				}
				boolean doesJobExist=false;
				
				String joidJobName="";
				List<String> joidAttribs;
				goodJobFileWriter=new FileWriter(goodJobsFile);
				goodJobBuffer=new BufferedWriter(goodJobFileWriter);
				badJobsFileWriter = new FileWriter(badJobsFie);
				badJobsBuffer=new BufferedWriter(badJobsFileWriter);
				String[] stringSplit;
				// opening file buffers.
				for(String k : archivedJobMap.keySet())
				{
					String joidAttribString = "";
					//joidAttribs=new ArrayList<String>();
					logger.debug("Getting value for joid: "+k);
					joidAttribs=archivedJobMap.get(k);
					StringBuilder sb = new StringBuilder();
					for(String s: joidAttribs)
					{
						joidAttribString+=s;
					}
					stringSplit=joidAttribString.split("=");
					logger.debug(stringSplit[1]);
					doesJobExist=db.doesJobExistInAE45(dbConnection, stringSplit[1].trim(), Integer.parseInt(k),dbType,dbName);
					if(doesJobExist==true)
					{
						if(!stringSplit[9].contains("(OLD)")) // this is only if Goldman needs to ignore jobs that are also older than a certain date. Otherwise comment this logic and just write to the buffers.
						{
							goodJobBuffer.write(joidAttribString.replace("=", "")+" JOID: "+k+"\n");
						}
						else
						{
							badJobsBuffer.write(joidAttribString.replace("=", "")+" JOID: "+k+"\n");
						}
					}
					else
					{
						badJobsBuffer.write(joidAttribString.replace("=", "")+" JOID: "+k+"\n");
					}
				}
				close(dbConnection);
				goodJobBuffer.close();
				badJobsBuffer.close();
				goodJobFileWriter.close();
				badJobsFileWriter.close();
				
				
	}

}
