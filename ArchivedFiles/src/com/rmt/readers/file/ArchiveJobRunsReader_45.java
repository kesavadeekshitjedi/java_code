package com.rmt.readers.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import org.apache.log4j.Logger;

public class ArchiveJobRunsReader_45 
{
	
	static Logger logger = Logger.getRootLogger();
	FileReader archiveFileReader;
	BufferedReader archiveFileBuffer;
	
	public void readJobRunsArchive(String archiveFolder) throws IOException
	{
	/*
	 * This is the order of the columns in AutoSys 4.5 archived_job_runs file
	 * joid,run_num,ntry,startime,endtime,status,exit_code,runtime,evt_num,machine
	 * 
	 */
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:MM:ss");
		
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
					while((currentArchiveLine=archiveFileBuffer.readLine())!=null)
					{
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
							jobStatus=archiveLineTuple[5].trim();
							jobExitCode=archiveLineTuple[6].trim();
							jobRunTime=archiveLineTuple[7].trim();
							jobRunEventNumber=archiveLineTuple[8].trim();
							jobRunMachine=archiveLineTuple[9].trim();
							
							jobStartTime=sdf.format(new Date(Long.parseLong(jobStartTimeEpoch)));
							jobEndTime=sdf.format(new Date(Long.parseLong(jobEndTimeEpoch)));
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
						}
						
						
					}
					Instant end=Instant.now();
					Duration elapsedTime = Duration.between(start, end);
					System.out.println("----------------------");
					System.out.println("Done with file: "+file+" in (ms)"+elapsedTime.toMillis());
					logger.info("Done reading file: "+file+" in "+elapsedTime.toMillis()+" (ms)");
						
				}
	}

}
