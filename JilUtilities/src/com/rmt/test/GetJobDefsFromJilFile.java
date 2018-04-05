package com.rmt.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class GetJobDefsFromJilFile 
{
	static Logger logger = Logger.getRootLogger();

	static List<String> jobList;
	public static void main(String[] args) throws IOException 
	{
		String log4jLocation = "resources/log4j.properties";
		PropertyConfigurator.configure(log4jLocation);
		
		String instanceJobListFile="D:\\OneDrive-Business\\OneDrive - Robert Mark Technologies\\Goldman\\files\\aggregates";
		String instanceJobDefFiles="D:\\OneDrive-Business\\OneDrive - Robert Mark Technologies\\Goldman\\gs_jobdef";
		String outputJobDefFolder="D:\\OneDrive-Business\\OneDrive - Robert Mark Technologies\\Goldman\\gs_jobdef\\extracts";
		
		FileReader inputReader ;
		BufferedReader inputBuffer ;
		FileWriter outputWriter;
		BufferedWriter bufferedWriter;
		
		File folder = new File(instanceJobListFile);
		File[] listOfInstanceFiles  = folder.listFiles();
		for(int i=0;i<listOfInstanceFiles.length;i++) 
		{
			if(listOfInstanceFiles[i].isFile())
			{
				jobList = new ArrayList<String>();
				String currentFileName=listOfInstanceFiles[i].getName();
				String[] instanceNameArray=currentFileName.split("\\.");
				String instanceName=instanceNameArray[0].trim();
				String fileToRead = instanceJobListFile+"\\"+currentFileName.trim();
				logger.info("Parsing file: "+fileToRead);
				inputReader = new FileReader(fileToRead);
				inputBuffer = new BufferedReader(inputReader);
				String currentJobLine = null;
				while((currentJobLine=inputBuffer.readLine())!=null)
				{
					jobList.add(currentJobLine.trim());
				}
				logger.info("List of jobs for "+currentFileName+" gathered. Now getting job definitions");
				logger.debug(jobList);
				
				String instanceInputFile = instanceJobDefFiles+"\\"+instanceName+".out";
				logger.info("Definition file to read: "+instanceInputFile);
				String instanceOutputFile = outputJobDefFolder+"\\"+instanceName+".jil";
				logger.info("Output Destination folder: "+instanceOutputFile);
				logger.info("Total number of jobs to fetch :"+jobList.size());
				GetJobDefsFromJilFile.getJobsFromFile(instanceInputFile, instanceOutputFile, jobList);
				
			}
		}

	}
	
	public static void getJobsFromFile(String inputFile, String outputFile, List<String> jobList) throws IOException
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
						if(jobList.contains(jobName))
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
