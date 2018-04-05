package com.rmt.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

public class ExtractJobInfo_File 
{
	static Logger logger = Logger.getRootLogger();

	static FileReader archiveFileReader;
	static BufferedReader archiveFileReaderBuffer;
	static FileWriter archiveFileWriter;
	static BufferedWriter archiveFileWriterBuffer;
	
	
	static String archivedJobCutOffDate="01/01/2017";
	static String archived2016Date = "01/01/2016";
	static String archived2015Date = "01/01/2015";
	static String archived2014Date = "01/01/2014";
	
	static String archivedFileReportFolder = "D:\\OneDrive-Business\\OneDrive - Robert Mark Technologies\\Goldman\\files\\My_Reports";
	
	public void getListOfFiles(String folderName) throws IOException, ParseException
	{
		ExtractJobInfo_File ef = new ExtractJobInfo_File();
		
		File folder = new File(folderName);
		File[] listOfInstanceFiles  = folder.listFiles();
		for(int i=0;i<listOfInstanceFiles.length;i++)
		{
			if(listOfInstanceFiles[i].isFile())
			{
				String fileName=folderName+"\\"+listOfInstanceFiles[i].getName();
				String[] instanceNameArray=fileName.split("\\.");
				String instanceName=listOfInstanceFiles[i].getName().split("_")[0];
				ef.getJobsAfterDateFromArchiveFile(fileName, instanceName);
			}
		}
	}
	public void getJobsAfterDateFromArchiveFile(String archiveInputFile, String instanceName) throws IOException, ParseException
	{
		String archiveLine=null;
		String jobName=null;
		String archivedJobDate=null;
		SimpleDateFormat sdf = new SimpleDateFormat("mm/dd/yyyy");
		
		Date cutOffDate_2017 = sdf.parse(archivedJobCutOffDate);
		Date jobRunDate;
		Date cutOffDate_2014 = sdf.parse(archived2014Date);
		Date cutOffDate_2015 = sdf.parse(archived2015Date);
		Date cutOffDate_2016 = sdf.parse(archived2016Date);
		
		logger=Logger.getLogger("Goldman-Analysis.utils.ExtractjobInfo_Files.getJobsAfterDateFromArchiveFile");
		archiveFileReader = new FileReader(archiveInputFile);
		archiveFileReaderBuffer = new BufferedReader(archiveFileReader);
		logger.info("File "+archiveInputFile+" open for reading...");
		
		archiveFileWriter = new FileWriter(archivedFileReportFolder+"\\"+instanceName+"_JobList.txt");
		archiveFileWriterBuffer = new BufferedWriter(archiveFileWriter);
		
		FileWriter fileReportWriter_2015 = new FileWriter(archivedFileReportFolder+"\\"+instanceName+"_CountsByYear_2015.txt");
		BufferedWriter fileReportWriterBuffer_2015 = new BufferedWriter(fileReportWriter_2015);
		FileWriter fileReportWriter_2016 = new FileWriter(archivedFileReportFolder+"\\"+instanceName+"_CountsByYear_2016.txt");
		BufferedWriter fileReportWriterBuffer_2016 = new BufferedWriter(fileReportWriter_2016);
		FileWriter fileReportWriter_2017 = new FileWriter(archivedFileReportFolder+"\\"+instanceName+"_CountsByYear_2017.txt");
		BufferedWriter fileReportWriterBuffer_2017 = new BufferedWriter(fileReportWriter_2017);
		FileWriter report = new FileWriter(archivedFileReportFolder+"\\"+instanceName+"_OverallReport.txt");
		
		
		logger.info("Writers open");
		List<String> goodJobList = new ArrayList<String>();
		while((archiveLine=archiveFileReaderBuffer.readLine())!=null)
		{
			logger.debug(archiveLine);
			String[] archiveLineTuple=archiveLine.split(" ");
			jobName=archiveLineTuple[1].trim();
			archivedJobDate = archiveLineTuple[9].trim();
			jobRunDate = sdf.parse(archivedJobDate);
			logger.debug("Job: "+jobName+" ran on "+jobRunDate+". Check of this is > "+cutOffDate_2017);
			if(jobName.equalsIgnoreCase("iu8-GSAM_PNL_INTRADAY3-LoadFxRates"))
			{
				logger.info("bling");
			}
			if(jobRunDate.compareTo(cutOffDate_2017)>0)
			{
				
				
					archiveFileWriterBuffer.write(jobName+" has start time : "+archivedJobDate+"\n");
				
			}
			if(jobRunDate.compareTo(cutOffDate_2014) > 0 && jobRunDate.compareTo(cutOffDate_2015) < 0)
			{
				fileReportWriterBuffer_2015.write(jobName+"\n");
			}
			if(jobRunDate.compareTo(cutOffDate_2015) >0 && jobRunDate.compareTo(cutOffDate_2016) < 0)
			{
				fileReportWriterBuffer_2016.write(jobName+"\n");
			}
			if(jobRunDate.compareTo(cutOffDate_2016) > 0 && jobRunDate.compareTo(cutOffDate_2017) < 0)
			{
				fileReportWriterBuffer_2017.write(jobName+"\n");
			}
			
		}
		archiveFileWriterBuffer.close();
		archiveFileWriter.close();
		archiveFileReader.close();
		archiveFileReaderBuffer.close();
		fileReportWriterBuffer_2015.close();
		fileReportWriterBuffer_2016.close();
		fileReportWriterBuffer_2017.close();
		fileReportWriter_2017.close();
		fileReportWriter_2016.close();
		fileReportWriter_2015.close();
		
		
		
		
	}
	
	public void getJobsFromFile(String inputFile, String outputFile, List<String> jobList) throws IOException
	{
		logger=Logger.getLogger("JilUtilities.utilities.addStatusLine2JilFile");
		logger.info("Make a copy of the original jil file");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
		Date currentDate = new Date();
		String dateFormat = sdf.format(currentDate);
		String outputTarget=outputFile+"."+dateFormat+".definitions";
		FileReader inputFileReader = new FileReader(inputFile);
		BufferedReader inputFileBuffer = new BufferedReader(inputFileReader);
		FileWriter outputFileWriter = new FileWriter(outputFile);
		BufferedWriter outputFileBuffer = new BufferedWriter(outputFileWriter);
		
		/*
		 * Making a copy of the original file in the next few lines here.
		 */
		InputStream input=null;
		OutputStream output = null;
		String targetFile=inputFile+".backup."+dateFormat;
		input = new FileInputStream(new File(inputFile));
		output = new FileOutputStream(new File(targetFile));
		byte[] buff = new byte[1024];
		int bytesRead;
		while((bytesRead = input.read(buff))>0)
		{
			output.write(buff, 0, bytesRead);
		}
		output.close();
		input.close();
		logger.info("File copied successfully");
		// File back up operation complete.
		
		String jilFileLine=null;
		String jobName=null;
		boolean foundNewJob=false;
		String[] lineSplitter = null;
		String newLine=null;
		logger.info("attempting to read "+inputFile);
		while((jilFileLine=inputFileBuffer.readLine())!=null)
		{
			String currentJilLine=jilFileLine.trim();
			if(!currentJilLine.contains("#") && (!currentJilLine.isEmpty() && (!currentJilLine.contains("/*") && (!currentJilLine.contains("//")))))
			{
				logger.debug(jilFileLine);
				lineSplitter=currentJilLine.split(":");
				if(lineSplitter.length==3 && lineSplitter[0].contains("insert_job"))
				{
					
					if(lineSplitter.length==3 && lineSplitter[0].contains("insert_job"))
					{
						foundNewJob=true;
						String[] jobLine=lineSplitter[1].trim().split(" ");
						jobName=jobLine[0].trim();
						if(!jobList.contains(jobName))
						{
							foundNewJob=true;
						}
						else
							foundNewJob=false;
					}
					
				}
				
			}
			if(foundNewJob==true && (!currentJilLine.contains("#")) && (!currentJilLine.contains("/*")))
			{
				outputFileBuffer.write(currentJilLine+" \n");
			}
		}
		outputFileBuffer.close();
		outputFileWriter.close();
		inputFileBuffer.close();
		inputFileReader.close();
		
	}

}
