package jmoUtilities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;



import mainUtils.JilUtilMain;

public class JMOExtractAnalyzer 
{
	static String jobSeperator=".";
	static Logger logger = Logger.getRootLogger();
	static String jobDefString="DEFINE JOB ID=";
	static String jobsetDefString="DEFINE JOBSET ID=";
	static String triggerDefString="DEFINE TRIGGER ID=";
	static String calendarDefString="DEFINE CAL ID=";
	static String machineDefString="DEFINE STATION ID=";
	static String jobPredDefString="DEFINE JOBPRED ID=";
	static String jobsetPredDefString="DEFINE JOBSETPRED ID=";
	static String resourceDefString="DEFINE RESOURCE ID=";
	static String stationGroupDefString="DEFINE STATIONGROUP ID=";
	static List<String> jobList=new ArrayList<String>();
	static List<String> fullJobList = new ArrayList<String>();
	static List<String> machineList = new ArrayList<String>();
	static List<String> jobsetList = new ArrayList<String>();
	static List<String> triggerList = new ArrayList<String>();
	static List<String> calendarList = new ArrayList<String>();
	static List<String> resourceList=new ArrayList<String>();
	static Map<String, String> predecessorJobStatus = new HashMap<String, String>();
	static Map<String, String> predecessorJobsetStatus = new HashMap<String, String>();
	static Map<String, List<String>> jobsetJobMap = new HashMap<String, List<String>>();
	static Map<String, List<String>> calendarJobMap = new HashMap<String, List<String>>();
	static Map<String, List<String>> jobDependencyMap = new HashMap<String, List<String>>();
	static Map<String, String> machineMap = new HashMap<String,String>();
	static Map<String, List<String>> stationGroupMap = new HashMap<String, List<String>>();
	
	
	public void readJMOExtractHighLevel(String jmoFile) throws IOException
	{
		logger=Logger.getLogger("ConversionUtilities.jmoUtilities.readJMOExtractHighLevel");
		BufferedWriter jobWriter;
		BufferedWriter jobsetWriter;
		BufferedWriter triggerWriter;
		BufferedWriter resourceWriter;
		BufferedWriter stationWriter;
		BufferedWriter calendarWriter;
		FileWriter jobFileWriter;
		FileWriter jobsetFileWriter;
		FileWriter triggerFileWriter;
		FileWriter resourceFileWriter;
		FileWriter stationFileWriter;
		FileWriter calendarFileWriter;
		FileWriter jmoReportFileWriter;
		BufferedWriter jmoReportWriter;
		String jmoReportFile="";
		
		
		System.out.println("Split files will be stored in the same path with the current date.");
		Path p = Paths.get(jmoFile);
		Path folderPath = p.getParent();
		System.out.println("File is at this location: "+folderPath);
		String extractFolder=folderPath+"\\"+JilUtilMain.myTime;
		System.out.println("Creating new folder: "+extractFolder);
		String splitJobsFile=extractFolder+"\\"+"JMO__JOBS.txt";
		String splitJobsetFile=extractFolder+"\\"+"JMO__JOBSETS.txt";
		String splitTriggerFile=extractFolder+"\\"+"JMO__TRIGGERS.txt";
		String splitResourcesFile=extractFolder+"\\"+"JMO__RESOURCES.txt";
		String splitStationFile=extractFolder+"\\"+"JMO__STATIONS.txt";
		String splitCalendarFile=extractFolder+"\\"+"JMO__CALENDARS.txt";
		jmoReportFile=extractFolder+"\\JMO_Report"+JilUtilMain.myTime+".txt";
		System.out.println("The following files will be created: "+splitJobsFile+" \n"+splitJobsetFile+"\n"+splitTriggerFile+"\n"+splitResourcesFile+"\n"+splitStationFile+"\n"+splitCalendarFile+"\n"+jmoReportFile+"\n");
		File myextractFolder=new File(extractFolder);
		myextractFolder.mkdir();
		System.out.println("Folder "+extractFolder+" created...");
		
		jobFileWriter=new FileWriter(splitJobsFile,true);
		jobWriter = new BufferedWriter(jobFileWriter);
		
		jobsetFileWriter=new FileWriter(splitJobsetFile,true);
		jobsetWriter = new BufferedWriter(jobsetFileWriter);
		
		triggerFileWriter=new FileWriter(splitTriggerFile,true);
		triggerWriter = new BufferedWriter(triggerFileWriter);
		
		resourceFileWriter=new FileWriter(splitResourcesFile,true);
		resourceWriter = new BufferedWriter(resourceFileWriter);
		
		stationFileWriter=new FileWriter(splitStationFile,true);
		stationWriter = new BufferedWriter(stationFileWriter);
		
		calendarFileWriter=new FileWriter(splitCalendarFile,true);
		calendarWriter = new BufferedWriter(calendarFileWriter);
		
		jmoReportFileWriter=new FileWriter(jmoReportFile);
		jmoReportWriter = new BufferedWriter(jmoReportFileWriter);
		
		System.out.println("All Writers open...");
		
		logger=Logger.getLogger("ConversionUtilities.jmoUtilities.readJMOExtract");
		FileReader jmoExtractReader;
		BufferedReader jmoBuffer;
		String currentJMOLine="";
		int startIndex=0;
		int endIndex=0;
		String tempString="";
		try
		{
			jmoExtractReader = new FileReader(jmoFile);
			jmoBuffer = new BufferedReader(jmoExtractReader);
			while((currentJMOLine=jmoBuffer.readLine())!=null)
			{
				
				String jmoLine=currentJMOLine.trim();
				if((jmoLine.contains(jobDefString) || (jmoLine.contains("PARM"))))
				{
					
					jobWriter.write(jmoLine+"\n");
					if(jmoLine.contains(jobDefString))
					{
						logger.info("Job Found");
						//logger.debug(jmoLine);
						startIndex=jmoLine.indexOf(jobDefString)+jobDefString.length();
						if(jmoLine.contains("DESCRIPTION") && !(jmoLine.contains("FAILCOND")))
						{
							endIndex=jmoLine.indexOf("DESCRIPTION");
						}
						else if(jmoLine.contains("FAILCOND"))
						{
							endIndex=jmoLine.indexOf("FAILCOND");
						}
						else if(jmoLine.contains("STATION"))
						{
							endIndex=jmoLine.indexOf("STATION");
						}
						tempString=jmoLine.substring(startIndex, endIndex).trim();
						fullJobList.add(tempString);
						logger.info("Full Job Line added to fulljobList "+jmoLine);
						// Job extraction is complete at this point.
						
						String[] jobTuple=tempString.split(",");
						String jobName=jobTuple[1].trim();
						String jobNumber=jobTuple[2].replace(")","").trim();
						String jobsetName=jobTuple[0].replace("(", "");
						logger.debug("Jobset : "+jobsetName);
						// This code here is creating the relationship between the jobset (as the key) and the job^jobNumber as the list of values.
						String myJobName=jobName+"^"+jobNumber+"^"+jobsetName;
						logger.debug("Job and Job Number: "+myJobName);
						if(!(jobList.contains(myJobName)))
						{
							jobList.add(myJobName);
							logger.debug("Job: "+myJobName+" added to jobList...");
						}
						if(!(jobsetJobMap.containsKey(jobsetName)))
						{
							List<String> jobsForJobset = new ArrayList<String>();
							jobsForJobset.add(myJobName);
							jobsetJobMap.put(jobsetName, jobsForJobset);
						}
						else
						{
							List<String> jobsForJobset = new ArrayList<String>();
							jobsForJobset = jobsetJobMap.get(jobsetName);
							jobsForJobset.add(myJobName);
							jobsetJobMap.put(jobsetName, jobsForJobset);
							
						}
					}
				}
				else if (jmoLine.contains(jobsetDefString))
				{
					jobsetWriter.write(jmoLine+"\n");
					logger.info("Jobset Found");
					startIndex=jmoLine.indexOf(jobsetDefString)+jobsetDefString.length();
					endIndex=jmoLine.indexOf("FAILCOND");
					tempString=jmoLine.substring(startIndex,endIndex).trim();
					if(!(jobsetList.contains(tempString)))
					{
						jobsetList.add(tempString);
						logger.debug("Jobset "+tempString+" added to list");
					}
				}
				else if(jmoLine.contains(triggerDefString))
				{
					logger.debug(jmoLine);
					triggerWriter.write(jmoLine+"\n");
					logger.info("Trigger found");
					startIndex=jmoLine.indexOf(triggerDefString)+triggerDefString.length();
					if(jmoLine.contains("DESCRIPTION"))
					{
						endIndex=jmoLine.indexOf("DESCRIPTION");
					}
					
					tempString=jmoLine.substring(startIndex, endIndex).trim();
					String[] triggerTuple=tempString.split(",");
					String triggerName=triggerTuple[0].replace("(","").trim();
					String triggerType=triggerTuple[1].replace(")","").trim();
					if(!triggerList.contains(triggerName))
					{
						triggerList.add(triggerName);
					}
				}
				else if(jmoLine.contains(machineDefString))
				{
					stationWriter.write(jmoLine+"\n");
					startIndex=jmoLine.indexOf(machineDefString)+machineDefString.length();
					endIndex=jmoLine.indexOf("NODE");
					
					String[] machineTuple=jmoLine.substring(startIndex, endIndex).split(",");
					String machineName=machineTuple[0].replace("(", "");
					startIndex=jmoLine.indexOf("NODE")+"NODE=".length();
					endIndex=jmoLine.indexOf("NODETYPE");
					if(endIndex<startIndex)
					{
						int tempStartIndex=endIndex;
						int tempEndIndex=startIndex;
						startIndex=tempStartIndex;
						endIndex=tempEndIndex;
					}
					String nodeName=jmoLine.substring(startIndex,endIndex).trim();
					if(!machineList.contains(machineName))
					{
						machineList.add(machineName);
						logger.info(machineName+" added to machineList");
					}
					if(!machineMap.containsKey(machineName))
					{
						machineMap.put(machineName, nodeName);
						logger.info(machineName+" added to machineMap");
					}
				}
				else if(jmoLine.contains(calendarDefString))
				{
					calendarWriter.write(jmoLine+"\n");
					startIndex=jmoLine.indexOf(calendarDefString)+calendarDefString.length();
					endIndex=jmoLine.indexOf("DESC");
					String tempCalName=jmoLine.substring(startIndex, endIndex).trim();
					if(!calendarList.contains(tempCalName))
					{
						calendarList.add(tempCalName);
					}
				}
				else if(jmoLine.contains(resourceDefString))
				{
					resourceWriter.write(jmoLine+"\n");
					startIndex=jmoLine.indexOf(resourceDefString)+resourceDefString.length();
					
					endIndex=jmoLine.indexOf("AMOUNT");
					String resName="";
					String resMach="";
					String resAmt="";
					String tempResLine=jmoLine.substring(startIndex,endIndex).trim();
					String[] tempResTuple=tempResLine.split(",");
					resName=tempResTuple[0].replace("(", "").trim();
					resMach=tempResTuple[1].replace(")", "").trim();
					startIndex=jmoLine.indexOf("AMOUNT");
					if(jmoLine.contains("WEIGHT"))
					{
					endIndex=jmoLine.indexOf("WEIGHT");
					resAmt=jmoLine.substring(startIndex,endIndex).trim();
					}
					else
					{
						resAmt=jmoLine.substring(startIndex).trim();
					}
					
					if(!resourceList.contains(resName))
					{
						resourceList.add(resName+"="+resMach);
						logger.info("Added resource name and machine to list");
					}
					
				}
			}
			logger.info("Parsing JMO Extract is complete.");
			jmoBuffer.close();
			jmoExtractReader.close();
			jobWriter.close();
			jobsetWriter.close();
			triggerWriter.close();
			resourceWriter.close();
			stationWriter.close();
			calendarWriter.close();
			jobFileWriter.close();
			jobsetFileWriter.close();
			triggerFileWriter.close();
			resourceFileWriter.close();
			stationFileWriter.close();
			calendarFileWriter.close();
			System.out.println("Creating JMO Report");
			int jobCount=jobList.size();
			int jobsetCount=jobsetList.size();
			int triggerCount=triggerList.size();
			int resourceCount=resourceList.size();
			int stationCount=machineList.size();
			int calendarCount=calendarList.size();
			jmoReportWriter.write("------------------------------------------------"+"\n");
			jmoReportWriter.write("------------------------JMO Object Report created @ "+JilUtilMain.myTime+"------------------------"+"\n");
			jmoReportWriter.write("JMO Extract File: "+jmoFile+"\n");
			jmoReportWriter.write("Report File Location: "+jmoReportFile+"\n");
			jmoReportWriter.write("\n");
			jmoReportWriter.write("Total Number of Jobs: "+jobCount+" \n");
			jmoReportWriter.write("Total Number of Jobsets: "+jobsetCount+" \n");
			jmoReportWriter.write("Total Number of Triggers: "+triggerCount+" \n");
			jmoReportWriter.write("Total Number of Resources: "+resourceCount+" \n");
			jmoReportWriter.write("Total Number of Stations: "+stationCount+" \n");
			jmoReportWriter.write("Total Number of Calendars: "+calendarCount+" \n");
			jmoReportWriter.write("\n");
			checkJobPredecessors(jmoFile,jmoFile);
			checkJobsetPredecessors(jmoFile);
			/*jmoReportWriter.write("------------------------Job Predecessor Report------------------------"+"\n");
			System.out.println("All files and buffers closed. Report buffer still pending...");
			
			System.gc();
			checkJobPredecessors(jmoFile);
			checkJobsetPredecessors(jmoFile);
			logger.info("Writing Job Predecessor checks in report");
			Iterator it = predecessorJobStatus.entrySet().iterator();
			while(it.hasNext())
			{
				Map.Entry kvPair = (Map.Entry)it.next();
				String jobName=(String) kvPair.getKey();
				String predCheckValue = (String) kvPair.getValue();
				if(predCheckValue.equalsIgnoreCase("NotExist"))
				{
					jmoReportWriter.write(jobName+"\n");
				}
			}
			jmoReportWriter.write("\n");
			jmoReportWriter.write("------------------------ End of Job Predecessor Report------------------------"+"\n");
			jmoReportWriter.write("\n");
			jmoReportWriter.write("\n");
			jmoReportWriter.write("------------------------Jobset Predecessor Report------------------------"+"\n");
			logger.info("Writing Jobset Predecessor checks in report");
			it=predecessorJobsetStatus.entrySet().iterator();
			while(it.hasNext())
			{
				Map.Entry kvPair = (Map.Entry)it.next();
				String jobsetName=(String) kvPair.getKey();
				String predCheckValue = (String) kvPair.getValue();
				if(predCheckValue.equalsIgnoreCase("NotExists"))
				{
					jmoReportWriter.write(jobsetName+"\n");
				}
			}
			jmoReportWriter.write("------------------------ End Jobset Predecessor Report------------------------"+"\n");*/
			jmoReportWriter.write("\n");
			jmoReportWriter.close();
			jmoReportFileWriter.close();
			System.out.println("All files and buffers closed.");
			
		}
		catch(IOException fnfe)
		{
			fnfe.printStackTrace();
		}
	}

	public boolean checkIfPredExists(String checkString, String checkFile) throws IOException
	{
		logger=Logger.getLogger("ConversionUtilities.jmoUtilities.checkIfPredExists");
		boolean doesPredExist=false;
		FileReader checkFileReader = new FileReader(checkFile);
		BufferedReader checkFileBuffer = new BufferedReader(checkFileReader);
		String currentJMOLine="";
		while((currentJMOLine=checkFileBuffer.readLine())!=null)
		{
			
			String jmoLine=currentJMOLine.trim();
			if(jmoLine.contains("(srp_ipb_fib,FIB_pre_caller_ALL,0001)")) 
			{
				logger.info("alert");
			}
			if(jmoLine.contains(checkString))
			{
				doesPredExist=true;
				break;
			}
		}
		checkFileBuffer.close();
		checkFileReader.close();
		return doesPredExist;
	}
	public void createReport(String jmoFile) throws IOException
	{
		logger=Logger.getLogger("ConversionUtilities.jmoUtilities.createReport");
		BufferedWriter jobWriter;
		BufferedWriter jobsetWriter;
		BufferedWriter triggerWriter;
		BufferedWriter resourceWriter;
		BufferedWriter stationWriter;
		BufferedWriter calendarWriter;
		FileWriter jobFileWriter;
		FileWriter jobsetFileWriter;
		FileWriter triggerFileWriter;
		FileWriter resourceFileWriter;
		FileWriter stationFileWriter;
		FileWriter calendarFileWriter;
		FileWriter jmoReportFileWriter;
		BufferedWriter jmoReportWriter;
		
		
		System.out.println("Split files will be stored in the same path with the current date.");
		Path p = Paths.get(jmoFile);
		Path folderPath = p.getParent();
		System.out.println("File is at this location: "+folderPath);
		String extractFolder=folderPath+"\\"+JilUtilMain.myTime;
		System.out.println("Creating new folder: "+extractFolder);
		String splitJobsFile=extractFolder+"\\"+"JMO__JOBS.txt";
		String splitJobsetFile=extractFolder+"\\"+"JMO__JOBSETS.txt";
		String splitTriggerFile=extractFolder+"\\"+"JMO__TRIGGERS.txt";
		String splitResourcesFile=extractFolder+"\\"+"JMO__RESOURCES.txt";
		String splitStationFile=extractFolder+"\\"+"JMO__STATIONS.txt";
		String splitCalendarFile=extractFolder+"\\"+"JMO__CALENDARS.txt";
		String jmoReportFile=extractFolder+"\\JMO_Report"+JilUtilMain.myTime+".txt";
		System.out.println("The following files will be created: "+splitJobsFile+" \n"+splitJobsetFile+"\n"+splitTriggerFile+"\n"+splitResourcesFile+"\n"+splitStationFile+"\n"+splitCalendarFile+"\n"+jmoReportFile+"\n");
		File myextractFolder=new File(extractFolder);
		myextractFolder.mkdir();
		System.out.println("Folder "+extractFolder+" created...");
		
		jobFileWriter=new FileWriter(splitJobsFile,true);
		jobWriter = new BufferedWriter(jobFileWriter);
		
		jobsetFileWriter=new FileWriter(splitJobsetFile,true);
		jobsetWriter = new BufferedWriter(jobsetFileWriter);
		
		triggerFileWriter=new FileWriter(splitTriggerFile,true);
		triggerWriter = new BufferedWriter(triggerFileWriter);
		
		resourceFileWriter=new FileWriter(splitResourcesFile,true);
		resourceWriter = new BufferedWriter(resourceFileWriter);
		
		stationFileWriter=new FileWriter(splitStationFile,true);
		stationWriter = new BufferedWriter(stationFileWriter);
		
		calendarFileWriter=new FileWriter(splitCalendarFile,true);
		calendarWriter = new BufferedWriter(calendarFileWriter);
		
		jmoReportFileWriter=new FileWriter(jmoReportFile);
		jmoReportWriter = new BufferedWriter(jmoReportFileWriter);
		
		System.out.println("All Writers open...");
		
		logger=Logger.getLogger("ConversionUtilities.jmoUtilities.readJMOExtract");
		FileReader jmoExtractReader;
		BufferedReader jmoBuffer;
		String currentJMOLine="";
		int startIndex=0;
		int endIndex=0;
		String tempString="";
		try
		{
			jmoExtractReader = new FileReader(jmoFile);
			jmoBuffer = new BufferedReader(jmoExtractReader);
			while((currentJMOLine=jmoBuffer.readLine())!=null)
			{
				
				String jmoLine=currentJMOLine.trim();
				if((jmoLine.contains(jobDefString) || (jmoLine.contains("PARM"))))
				{
					
					jobWriter.write(jmoLine+"\n");
					if(jmoLine.contains(jobDefString))
					{
						logger.info("Job Found");
						//logger.debug(jmoLine);
						startIndex=jmoLine.indexOf(jobDefString)+jobDefString.length();
						if(jmoLine.contains("DESCRIPTION") && !(jmoLine.contains("FAILCOND")))
						{
							endIndex=jmoLine.indexOf("DESCRIPTION");
						}
						else if(jmoLine.contains("FAILCOND"))
						{
							endIndex=jmoLine.indexOf("FAILCOND");
						}
						else if(jmoLine.contains("STATION"))
						{
							endIndex=jmoLine.indexOf("STATION");
						}
						tempString=jmoLine.substring(startIndex, endIndex).trim();
						fullJobList.add(tempString);
						logger.info("Full Job Line added to fulljobList "+jmoLine);
						// Job extraction is complete at this point.
						
						String[] jobTuple=tempString.split(",");
						String jobName=jobTuple[1].trim();
						String jobNumber=jobTuple[2].replace(")","").trim();
						String jobsetName=jobTuple[0].replace("(", "");
						logger.debug("Jobset : "+jobsetName);
						// This code here is creating the relationship between the jobset (as the key) and the job^jobNumber as the list of values.
						String myJobName=jobName+"^"+jobNumber;
						logger.debug("Job and Job Number: "+myJobName);
						if(!(jobList.contains(myJobName)))
						{
							jobList.add(myJobName);
							logger.debug("Job: "+myJobName+" added to jobList...");
						}
						if(!(jobsetJobMap.containsKey(jobsetName)))
						{
							List<String> jobsForJobset = new ArrayList<String>();
							jobsForJobset.add(myJobName);
							jobsetJobMap.put(jobsetName, jobsForJobset);
						}
						else
						{
							List<String> jobsForJobset = new ArrayList<String>();
							jobsForJobset = jobsetJobMap.get(jobsetName);
							jobsForJobset.add(myJobName);
							jobsetJobMap.put(jobsetName, jobsForJobset);
							
						}
					}
				}
				else if (jmoLine.contains(jobsetDefString))
				{
					jobsetWriter.write(jmoLine+"\n");
					logger.info("Jobset Found");
					startIndex=jmoLine.indexOf(jobsetDefString)+jobsetDefString.length();
					endIndex=jmoLine.indexOf("FAILCOND");
					tempString=jmoLine.substring(startIndex,endIndex).trim();
					if(!(jobsetList.contains(tempString)))
					{
						jobsetList.add(tempString);
						logger.debug("Jobset "+tempString+" added to list");
					}
				}
				else if(jmoLine.contains(triggerDefString))
				{
					triggerWriter.write(jmoLine+"\n");
					logger.info("Trigger found");
					startIndex=jmoLine.indexOf(triggerDefString)+triggerDefString.length();
					if(jmoLine.contains("DESCRIPTION"))
					{
						endIndex=jmoLine.indexOf("DESCRIPTION");
					}
					else if(jmoLine.contains("CRITKEYS"))
					{
						endIndex=jmoLine.indexOf("CRITKEYS");
					}
					else if(jmoLine.contains("STATION"))
					{
						endIndex=jmoLine.indexOf("STATION");
						
					}
					tempString=jmoLine.substring(startIndex, endIndex).trim();
					if(!triggerList.contains(tempString))
					{
						triggerList.add(tempString);
					}
				}
				else if((jmoLine.contains(machineDefString) || (jmoLine.contains("STATIONGROUP"))))
				{
					
					String machineName="";
					String nodeName="";
					String stationGroupName="";
					List<String> stationGroupList=new ArrayList<String>();
					stationWriter.write(jmoLine+"\n");
					if(jmoLine.contains(machineDefString) && !(jmoLine.contains("STATIONGROUP")))
					{
					startIndex=jmoLine.indexOf(machineDefString)+machineDefString.length();
					endIndex=jmoLine.indexOf("NODE");
					String[] machineTuple=jmoLine.substring(startIndex, endIndex).split(",");
					machineName=machineTuple[0].replace("(", "");
					startIndex=jmoLine.indexOf("NODE")+"NODE".length();
					endIndex=jmoLine.indexOf("NODETYPE");
					nodeName=jmoLine.substring(startIndex,endIndex).trim();
					}
					else if(jmoLine.contains("STATIONGROUP"))
					{
						startIndex=jmoLine.indexOf(stationGroupDefString)+stationGroupDefString.length();
						endIndex=jmoLine.indexOf("STATIONS");
						
					}
					
					if(!machineList.contains(machineName))
					{
						machineList.add(machineName);
						logger.info(machineName+" added to machineList");
					}
					if(!machineMap.containsKey(machineName))
					{
						machineMap.put(machineName, nodeName);
						logger.info(machineName+" added to machineMap");
					}
				}
				else if(jmoLine.contains(calendarDefString))
				{
					calendarWriter.write(jmoLine+"\n");
					startIndex=jmoLine.indexOf(calendarDefString)+calendarDefString.length();
					endIndex=jmoLine.indexOf("DESC");
					String tempCalName=jmoLine.substring(startIndex, endIndex).trim();
					if(!calendarList.contains(tempCalName))
					{
						calendarList.add(tempCalName);
					}
				}
				else if(jmoLine.contains(resourceDefString))
				{
					resourceWriter.write(jmoLine+"\n");
					startIndex=jmoLine.indexOf(resourceDefString)+resourceDefString.length();
					String[] lineTuple=jmoLine.split(" ");
					int amtIndex=jmoLine.indexOf("AMOUNT");
					String resName="";
					String resMach="";
					String amt="";
					for(int k=0;k<lineTuple.length;k++)
					{
						if(lineTuple[k].contains("AMOUNT"))
						{
							amt=lineTuple[k];
							
						}
						if(lineTuple[k].contains("("))
						{
							String tempRes=lineTuple[k];
							String[] resTuple=tempRes.split(",");
							resName=resTuple[0].replace("(", "").trim();
							resMach=resTuple[1].replace(")", "").trim();
						}
					}
					if(!resourceList.contains(resName))
					{
						resourceList.add(resName+"="+resMach);
						logger.info("Added resource name and machine to list");
					}
					
				}
			}
			logger.info("Parsing JMO Extract is complete.");
			jobWriter.close();
			jobsetWriter.close();
			triggerWriter.close();
			resourceWriter.close();
			stationWriter.close();
			calendarWriter.close();
			jobFileWriter.close();
			jobsetFileWriter.close();
			triggerFileWriter.close();
			resourceFileWriter.close();
			stationFileWriter.close();
			calendarFileWriter.close();
			System.out.println("Creating JMO Report");
			int jobCount=jobList.size();
			int jobsetCount=jobsetList.size();
			int triggerCount=triggerList.size();
			int resourceCount=resourceList.size();
			int stationCount=machineList.size();
			int calendarCount=calendarList.size();
			jmoReportWriter.write("------------------------------------------------"+"\n");
			jmoReportWriter.write("                JMO Object Report - "+JilUtilMain.myTime+"-------------"+"\n");
			jmoReportWriter.write("JMO Extract File: "+jmoFile);
			jmoReportWriter.write("Report File Location: "+jmoReportFile+"\n");
			jmoReportWriter.write("\n");
			jmoReportWriter.write("Total Number of Jobs: "+jobCount+" \n");
			jmoReportWriter.write("Total Number of Jobsets: "+jobsetCount+" \n");
			jmoReportWriter.write("Total Number of Triggers: "+triggerCount+" \n");
			jmoReportWriter.write("Total Number of Resources: "+resourceCount+" \n");
			jmoReportWriter.write("Total Number of Stations: "+stationCount+" \n");
			jmoReportWriter.write("Total Number of Calendars: "+calendarCount+" \n");
			jmoReportWriter.write("\n");
			jmoReportWriter.close();
			jmoReportFileWriter.close();
			System.out.println("All files and buffers closed.");
			
		}
		catch(IOException fnfe)
		{
			fnfe.printStackTrace();
		}
	}
	public void checkJobPredecessors(String jmoFile, String jobFile) throws IOException
	{
		logger=Logger.getLogger("ConversionUtilities.jmoUtilities.checkJobPredecessors");
		FileWriter jobPredecessorWriter;
		BufferedWriter jobPredBuffer;
		Path p = Paths.get(jmoFile);
		Path folderPath = p.getParent();
		System.out.println("File is at this location: "+folderPath);
		String extractFolder=folderPath+"\\"+JilUtilMain.myTime;
		
		String jmoPredReportFile=extractFolder+"\\JMO_JobPredReport"+JilUtilMain.myTime+".txt";
		jobPredecessorWriter = new FileWriter(jmoPredReportFile);
		jobPredBuffer = new BufferedWriter(jobPredecessorWriter);
		logger=Logger.getLogger("ConversionUtilities.jmoUtilities.checkJobPredecessors");
		FileReader jmoExtractReader;
		BufferedReader jmoBuffer;
		String currentJMOLine="";
		int startIndex=0;
		int endIndex=0;
		int pJobIndex=0;
		int pJobsetIndex=0;
		int pJobNumberIndex=0;
		int tempIndex=0;
		int tridIndex=0;
		int trevIndex=0;
		int workdayIndex=0;
		
		
		String tempString="";
		try
		{
			jmoExtractReader = new FileReader(jmoFile);
			jmoBuffer = new BufferedReader(jmoExtractReader);
			while((currentJMOLine=jmoBuffer.readLine())!=null)
			{
				
				String jmoLine=currentJMOLine.trim();
				if(jmoLine.contains("DEFINE JOBPRED ID=(ASETransfer_GVA,ASETransfer_GVA,0001) WORKDAY=CURRENT PSET=ASETransfer_GVA PJOB=ASETransfer_GVA PJNO=0020"))
				{
					logger.info("bralksjlkajflsdkj");
				}
				if(jmoLine.contains(jobPredDefString))
				{
					if(jmoLine.contains("WORKDAY"))
					{
						workdayIndex=jmoLine.indexOf("WORKDAY");
					}
					logger.info("Job Predecessor Found");
					logger.debug(jmoLine);
					startIndex=jmoLine.indexOf(jobPredDefString)+jobPredDefString.length();
					if(jmoLine.contains("PJOB"))
					{
						pJobIndex=jmoLine.indexOf("PJOB");
						pJobsetIndex=jmoLine.indexOf("PSET");
						pJobNumberIndex=jmoLine.indexOf("PJNO");
						
						/*if(pJobIndex < pJobsetIndex && pJobIndex < pJobNumberIndex && pJobIndex < workdayIndex)
						{
							tempIndex=pJobIndex;
							endIndex=tempIndex;
						}
						else if(pJobsetIndex < pJobIndex && pJobsetIndex < pJobNumberIndex && pJobIndex < workdayIndex)
						{
							tempIndex=pJobsetIndex;
							endIndex=tempIndex;
						}
						else if(pJobNumberIndex < pJobsetIndex && pJobNumberIndex < pJobIndex && pJobIndex < workdayIndex)
						{
							tempIndex=pJobNumberIndex;
							endIndex=tempIndex;
						}
						else if(workdayIndex <  pJobsetIndex && workdayIndex < pJobIndex && workdayIndex < pJobNumberIndex)
						{
							tempIndex=workdayIndex;
							endIndex=tempIndex;
						}*/
							
						tempIndex=Math.min(Math.min(pJobIndex, pJobsetIndex), pJobNumberIndex);
						if(workdayIndex!=0)
						{
						endIndex=Math.min(workdayIndex,tempIndex);
						}
						else
						{
							endIndex=tempIndex;
						}
						String tempJobString=jmoLine.substring(startIndex, endIndex).trim();
						String[] tempJobTuple=tempJobString.split(",");
						String jobsetName=tempJobTuple[0].replace("(", "");
						String jobName=tempJobTuple[1].trim();
						String jobNumber=tempJobTuple[2].replace(")", "");
						String overallJobName="("+jobsetName+","+jobName+","+jobNumber+")";
						startIndex=jmoLine.indexOf("PJOB")+"PJOB=".length();
						endIndex=jmoLine.indexOf("PSET");
						String predecessorJobName=jmoLine.substring(startIndex,endIndex).trim();
						startIndex=jmoLine.indexOf("PSET")+"PSET=".length();
						endIndex=jmoLine.indexOf("PJNO");
						String predecessorJobsetName=jmoLine.substring(startIndex, endIndex).trim();
						startIndex=jmoLine.indexOf("PJNO")+"PJNO=".length();
						endIndex=jmoLine.indexOf("WORKDAY");
						String predecessorJobNumber=jmoLine.substring(startIndex, endIndex).trim();
						String checkString = "("+predecessorJobsetName+","+predecessorJobName+","+predecessorJobNumber+")";
						logger.info("Checking for "+checkString+" in the file: "+jobFile);
						boolean doesPredExist = checkIfPredExists(checkString, jobFile);
						if(doesPredExist==true)
						{
							logger.info("Job Pred: "+checkString+" exists");
							predecessorJobStatus.put(checkString, "Exists");
						}
						else
						{
							logger.error("Job Pred: "+checkString+" doesnt exist");
							logger.error(jmoLine);
							jobPredBuffer.write(jmoLine+" Predecessor Check Failed...\n");
							jobPredBuffer.write(checkString+"\n");
							jobPredBuffer.write("\n");
							predecessorJobStatus.put(jmoLine,"NotExists");
							predecessorJobStatus.put(checkString, "NotExists");
						}
						/*if(fullJobList.contains(checkString))
						{
							logger.info("Job Pred: "+checkString+" exists");
							predecessorJobStatus.put(checkString, "Exists");
						}
						else
						{
							logger.error("Job Pred: "+checkString+" doesnt exist");
							logger.error(jmoLine);
							jobPredBuffer.write(jmoLine+" Predecessor Check Failed...\n");
							jobPredBuffer.write(checkString+"\n");
							jobPredBuffer.write("\n");
							predecessorJobStatus.put(jmoLine,"NotExists");
							predecessorJobStatus.put(checkString, "NotExists");
						}*/
						
					}
					else if ((jmoLine.contains("PSET") && (!jmoLine.contains("PJOB"))))
					{
						logger.info("Job Depends on jobset only. Not on job.");
						
						if(jmoLine.contains("WORKDAY"))
						{
							workdayIndex=jmoLine.indexOf("WORKDAY");
						}
						if(jmoLine.contains("PSET"))
						{
							pJobsetIndex=jmoLine.indexOf("PSET");
						}
						tempIndex=Math.min(pJobsetIndex,workdayIndex);
						endIndex=tempIndex;
						String tempJobString=jmoLine.substring(startIndex, endIndex).trim();
						String[] tempJobTuple=tempJobString.split(",");
						String jobsetName=tempJobTuple[0].replace("(", "");
						String jobName=tempJobTuple[1].trim();
						String jobNumber=tempJobTuple[2].replace(")", "");
						String overallJobName="("+jobsetName+","+jobName+","+jobNumber+")";
						startIndex=jmoLine.indexOf("PSET=")+"PSET=".length();
						workdayIndex=jmoLine.indexOf("WORKDAY");
						if(startIndex > workdayIndex)
						{
							tempIndex=Math.min(pJobIndex, pJobNumberIndex);
						}
						else if(workdayIndex!=0 && startIndex < workdayIndex)
						{
							tempIndex=Math.min(Math.min(pJobIndex, pJobNumberIndex), workdayIndex);
						}
						endIndex=tempIndex;
						String tempPSETString=jmoLine.substring(startIndex, endIndex).trim();
						String checkString=tempPSETString.trim();
						logger.info("Checking for "+checkString+" in the file: "+jobFile);
						boolean doesPredExist = checkIfPredExists(checkString, jobFile);
						if(doesPredExist==true)
						{
							logger.info("Job Pred: "+checkString+" exists");
							predecessorJobStatus.put(checkString, "Exists");
						}
						else
						{
							logger.error("Job Pred: "+checkString+" doesnt exist");
							logger.error(jmoLine);
							jobPredBuffer.write(jmoLine+" Predecessor Check Failed...\n");
							jobPredBuffer.write(checkString+"\n");
							jobPredBuffer.write("\n");
							predecessorJobStatus.put(jmoLine,"NotExists");
							predecessorJobStatus.put(checkString, "NotExists");
						}
						/*if(jobsetList.contains(checkString))
						{
							logger.info("Job Pred: "+checkString+" exists");
							predecessorJobStatus.put(checkString, "Exists");
						}
						else
						{
							logger.error("Job Pred: "+checkString+" doesnt exist");
							logger.error(jmoLine);
							jobPredBuffer.write(jmoLine+" Predecessor Check Failed...\n");
							jobPredBuffer.write(checkString+"\n");
							jobPredBuffer.write("\n");
							predecessorJobStatus.put(jmoLine,"NotExists");
							predecessorJobStatus.put(checkString, "NotExists");
						}*/
						
					}
					else if (jmoLine.contains("TRID"))
					{
						tridIndex=jmoLine.indexOf("TRID");
						trevIndex=jmoLine.indexOf("TREV");
						logger.info("Job Depends on Trigger.");
						startIndex=jmoLine.indexOf(jobPredDefString)+jobPredDefString.length();
						if(jmoLine.contains("WORKDAY"))
						{
							workdayIndex=jmoLine.indexOf("WORKDAY");
							/*if(tridIndex < trevIndex && tridIndex < workdayIndex)
							{
								endIndex=tridIndex;
							}
							else if(trevIndex < workdayIndex && trevIndex < tridIndex)
							{
								endIndex=trevIndex;
							}
							else if(workdayIndex < tridIndex && workdayIndex < trevIndex)
							{
								endIndex=workdayIndex;
							}*/
							tempIndex=Math.min(tridIndex, trevIndex);
							if(workdayIndex!=0)
							{
								endIndex=Math.min(tempIndex, workdayIndex);
							}
							else
							{
								endIndex=tempIndex;
							}
							String tempJobString=jmoLine.substring(startIndex, endIndex).trim();
							String[] tempJobTuple=tempJobString.split(",");
							String jobsetName=tempJobTuple[0].replace("(", "");
							String jobName=tempJobTuple[1].trim();
							String jobNumber=tempJobTuple[2].replace(")", "");
							String overallJobName="("+jobsetName+","+jobName+","+jobNumber+")";
							startIndex=jmoLine.indexOf("TREV")+"TREV=".length();
							String predTriggerType="";
							//endIndex=jmoLine.indexOf("TRID");
							//predTriggerType=jmoLine.substring(startIndex,endIndex).trim();
							
							if(tridIndex<trevIndex && workdayIndex<trevIndex)
							{
								predTriggerType=jmoLine.substring(startIndex).trim();
							}
							else if(workdayIndex>trevIndex && workdayIndex<tridIndex)
								{
									
									endIndex=workdayIndex;
									predTriggerType=jmoLine.substring(startIndex,endIndex).trim();
								}
							else if(workdayIndex<trevIndex && workdayIndex<tridIndex && trevIndex<tridIndex)
							{
								predTriggerType=jmoLine.substring(startIndex).trim();
							}
							startIndex=jmoLine.indexOf("TRID")+"TRID=".length();
							tempIndex=Math.max(tridIndex, trevIndex);
							String predTriggerName="";
							if(workdayIndex<tridIndex && workdayIndex<trevIndex)
							{
								endIndex=tempIndex;
								predTriggerName=jmoLine.substring(startIndex,endIndex).trim();
							}
							else
								if(workdayIndex>tempIndex)
								{
									endIndex=workdayIndex;
								
									predTriggerName=jmoLine.substring(startIndex,endIndex).trim();
								}
								else if(workdayIndex<tridIndex && workdayIndex<trevIndex && trevIndex<tridIndex)
								{
									predTriggerType=jmoLine.substring(startIndex).trim();
								}
							
							
							String checkString=predTriggerName;
							logger.info("Checking for "+checkString+" in the file: "+jobFile);
							boolean doesPredExist = checkIfPredExists(checkString, jobFile);
							if(doesPredExist==true)
							{
								logger.info("Job Pred: "+checkString+" exists");
								predecessorJobStatus.put(checkString, "Exists");
							}
							else
							{
								logger.error("Job Pred: "+checkString+" doesnt exist");
								logger.error(jmoLine);
								jobPredBuffer.write(jmoLine+" Predecessor Check Failed...\n");
								jobPredBuffer.write(checkString+"\n");
								jobPredBuffer.write("\n");
								predecessorJobStatus.put(jmoLine,"NotExists");
								predecessorJobStatus.put(checkString, "NotExists");
							}
							/*if(triggerList.contains(checkString))
							{
								logger.info("Job Pred: "+checkString+" exists");
								predecessorJobStatus.put(checkString, "Exists");
							}
							else
							{
								logger.error("Job Pred: "+checkString+" doesnt exist");
								logger.error(jmoLine);
								jobPredBuffer.write(jmoLine+" Predecessor Check Failed...\n");
								jobPredBuffer.write(checkString+"\n");
								jobPredBuffer.write("\n");
								predecessorJobStatus.put(jmoLine,"NotExists");
								predecessorJobStatus.put(checkString, "NotExists");
							}*/
						}
						/*else if(jmoLine.contains("TREV"))
						{
							endIndex=jmoLine.indexOf("TREV");
							//String triggerType=jmoLine.substring(startIndex,endIndex).trim();
							String tempJobString=jmoLine.substring(startIndex, endIndex).trim();
							String[] tempJobTuple=tempJobString.split(",");
							String jobsetName=tempJobTuple[0].replace("(", "");
							String jobName=tempJobTuple[1].trim();
							String jobNumber=tempJobTuple[2].replace(")", "");
						}*/
					}
				}
			}
			jobPredBuffer.close();
			jobPredecessorWriter.close();
			jmoBuffer.close();
			jmoExtractReader.close();
		}
		
		
		catch(Exception e)
		{
			e.printStackTrace();
		}
		logger.info("Done with job predecessor checks");
		
		
	}
	
	public void checkJobsetPredecessors(String jmoFile) throws IOException
	{
		logger=Logger.getLogger("ConversionUtilities.jmoUtilities.checkJobsetPredecessors");
		FileWriter jobsetPredecessorWriter;
		BufferedWriter jobsetPredBuffer;
		Path p = Paths.get(jmoFile);
		Path folderPath = p.getParent();
		System.out.println("File is at this location: "+folderPath);
		String extractFolder=folderPath+"\\"+JilUtilMain.myTime;
		
		String jmoPredReportFile=extractFolder+"\\JMO_JobsetPredReport"+JilUtilMain.myTime+".txt";
		logger.info("Creating "+jmoPredReportFile);
		jobsetPredecessorWriter = new FileWriter(jmoPredReportFile);
		jobsetPredBuffer = new BufferedWriter(jobsetPredecessorWriter);
		logger=Logger.getLogger("ConversionUtilities.jmoUtilities.checkJobsetPredecessors");
		FileReader jmoExtractReader;
		BufferedReader jmoBuffer;
		String currentJMOLine="";
		int startIndex=0;
		int endIndex=0;
		int pJobIndex=0;
		int pJobsetIndex=0;
		int pJobNumberIndex=0;
		int tempIndex=0;
		int tridIndex=0;
		int trevIndex=0;
		int workdayIndex=0;
		String tempString="";
		try
		{
			jmoExtractReader = new FileReader(jmoFile);
			jmoBuffer = new BufferedReader(jmoExtractReader);
			while((currentJMOLine=jmoBuffer.readLine())!=null)
			{
				
				String jmoLine=currentJMOLine.trim();
				if(jmoLine.contains(jobsetPredDefString))
				{
					logger.info("Jobset Predecessor found");
					logger.debug(jmoLine);
					if(jmoLine.contains("PSET") && !jmoLine.contains("PJOB")) 
					{
						pJobsetIndex=jmoLine.indexOf("PSET");
						if(jmoLine.contains("WORKDAY"))
						{
							workdayIndex=jmoLine.indexOf("WORKDAY");
						}
						startIndex=jmoLine.indexOf(jobsetPredDefString)+jobsetPredDefString.length();
						//endIndex=jmoLine.indexOf("PSET");
						endIndex=Math.min(workdayIndex, pJobsetIndex);
						String jobsetName=jmoLine.substring(startIndex, endIndex).trim();
						startIndex=jmoLine.indexOf("PSET")+"PSET=".length();
						//endIndex=jmoLine.indexOf("WORKDAY");
						endIndex=Math.max(workdayIndex, pJobsetIndex);
						String predecessorJobset="";
						//predecessorJobset=jmoLine.substring(startIndex, endIndex).trim();
						if(startIndex>endIndex)
						{
							predecessorJobset=jmoLine.substring(startIndex).trim();
						}
						else
						{
							predecessorJobset=jmoLine.substring(startIndex, endIndex).trim();
						}
						String checkString=predecessorJobset;
						//logger.info("Checking for "+checkString+" in jobsets list");
						logger.info("Checking for "+checkString+" in the file: "+jmoFile);
						boolean doesPredExist = checkIfPredExists(checkString, jmoFile);
						if(doesPredExist==true)
						{
							logger.info("Job Pred: "+checkString+" exists");
							predecessorJobsetStatus.put(checkString, "Exists");
						}
						else
						{
							logger.error("Job Pred: "+checkString+" doesnt exist");
							logger.error(jmoLine);
							jobsetPredBuffer.write(jmoLine+" Predecessor Check Failed...\n");
							jobsetPredBuffer.write(checkString+"\n");
							jobsetPredBuffer.write("\n");
							predecessorJobsetStatus.put(jmoLine,"NotExists");
							predecessorJobsetStatus.put(checkString, "NotExists");
						}
						/*if(jobsetList.contains(checkString))
						{
							logger.info("Jobset Pred: "+checkString+" exists");
							predecessorJobsetStatus.put(checkString, "Exists");
						}
						else
						{
							logger.error("Jobset Pred: "+checkString+" doesnt exist");
							logger.error(jmoLine);
							jobsetPredBuffer.write(jmoLine+" Predecessor Check Failed...\n");
							jobsetPredBuffer.write(checkString+"\n");
							jobsetPredBuffer.write("\n");
							predecessorJobsetStatus.put(jmoLine,"NotExists");
							predecessorJobsetStatus.put(checkString, "NotExists");
						}*/
						
						
						
					}
					else if(jmoLine.contains("PJOB"))
					{
						
						workdayIndex=jmoLine.indexOf("WORKDAY");
						if(jmoLine.contains("DEFINE JOBSETPRED ID=aar_dashboard_alert_gva WORKDAY=CURRENT PSET=aar_load_batch_gva PJOB=UpdateBatchEndTime PJNO=0591"))
						{
							System.out.println("bling");
						}
						startIndex=jmoLine.indexOf(jobsetPredDefString)+jobsetPredDefString.length();
						pJobIndex=jmoLine.indexOf("PJOB");
						pJobsetIndex=jmoLine.indexOf("PSET");
						pJobNumberIndex=jmoLine.indexOf("PJNO");
						if(jmoLine.contains("WORKDAY"))
						{
							workdayIndex=jmoLine.indexOf("WORKDAY");
						}
						int highestIndex=Math.max(Math.max(Math.max(workdayIndex, pJobNumberIndex), pJobsetIndex), pJobIndex);
						if(highestIndex==workdayIndex)
						{
							logger.debug("Highest Index: "+workdayIndex);
						}
						else if(highestIndex==pJobNumberIndex)
						{
							logger.debug("Highest Index: "+pJobNumberIndex);
						}
						else if(highestIndex==pJobsetIndex)
						{
							logger.debug("Highest Index: "+pJobsetIndex);
						}
						else if(highestIndex==pJobIndex)
						{
							logger.debug("Highest Index: "+pJobIndex);
						}
						int lowestIndex=Math.min(Math.min(Math.min(workdayIndex, pJobNumberIndex), pJobsetIndex), pJobIndex);
						
						/*if(pJobIndex < pJobsetIndex && pJobIndex < pJobNumberIndex && pJobIndex < workdayIndex)
						{
							tempIndex=pJobIndex;
							endIndex=tempIndex;
						}
						else if(pJobsetIndex < pJobIndex && pJobsetIndex < pJobNumberIndex && pJobIndex < workdayIndex)
						{
							tempIndex=pJobsetIndex;
							endIndex=tempIndex;
						}
						else if(pJobNumberIndex < pJobsetIndex && pJobNumberIndex < pJobIndex && pJobIndex < workdayIndex)
						{
							tempIndex=pJobNumberIndex;
							endIndex=tempIndex;
						}
						else if(workdayIndex <  pJobsetIndex && workdayIndex < pJobIndex && workdayIndex < pJobNumberIndex)
						{
							tempIndex=workdayIndex;
							endIndex=tempIndex;
						}*/
						//endIndex=jmoLine.indexOf("PJOB");
						tempIndex=Math.min(Math.min(pJobIndex, pJobsetIndex), pJobNumberIndex);
						if(workdayIndex!=0)
						{
						endIndex=Math.min(workdayIndex,tempIndex);
						}
						else
						{
							endIndex=tempIndex;
						}
						String jobsetName=jmoLine.substring(startIndex, endIndex).trim();
						startIndex=jmoLine.indexOf("PJOB")+"PJOB=".length();
						//endIndex=jmoLine.indexOf("PSET");
						
						String predecessorJobName="";
						if(pJobsetIndex<pJobIndex && pJobsetIndex<pJobNumberIndex && pJobIndex<pJobNumberIndex)
						{
							endIndex=pJobNumberIndex;
							predecessorJobName=jmoLine.substring(startIndex,endIndex).trim();
						}
						
						startIndex=jmoLine.indexOf("PSET")+"PSET=".length();
						//endIndex=jmoLine.indexOf("PJNO");
						String predecessorJobsetName="";
						if(pJobsetIndex==highestIndex)
						{
							
							predecessorJobsetName=jmoLine.substring(startIndex).trim();
						}
						else if(pJobNumberIndex==highestIndex && pJobsetIndex<pJobIndex)
						{
							endIndex=pJobNumberIndex;
							predecessorJobsetName=jmoLine.substring(startIndex,endIndex).trim();
						}
						if(pJobIndex>pJobsetIndex && pJobIndex<pJobNumberIndex)
						{
							endIndex=pJobIndex;
							predecessorJobsetName=jmoLine.substring(startIndex,endIndex).trim();
						}
						
						startIndex=jmoLine.indexOf("PJNO")+"PJNO=".length();
						String predecessorJobNumber="";
						//endIndex=jmoLine.indexOf("WORKDAY");
						if(pJobNumberIndex==highestIndex)
						{
							predecessorJobNumber=jmoLine.substring(startIndex).trim();
						}
						else if(pJobNumberIndex==highestIndex && pJobsetIndex<pJobIndex)
						{
							endIndex=pJobNumberIndex;
							predecessorJobNumber=jmoLine.substring(startIndex,endIndex).trim();
						}
						//String predecessorJobNumber=jmoLine.substring(startIndex, endIndex).trim();
						String checkString="("+predecessorJobsetName+","+predecessorJobName+","+predecessorJobNumber+")";
						logger.info("Checking for "+checkString+" in the file: "+jmoFile);
						boolean doesPredExist = checkIfPredExists(checkString, jmoFile);
						if(doesPredExist==true)
						{
							logger.info("Job Pred: "+checkString+" exists");
							predecessorJobsetStatus.put(checkString, "Exists");
						}
						else
						{
							logger.error("Job Pred: "+checkString+" doesnt exist");
							logger.error(jmoLine);
							jobsetPredBuffer.write(jmoLine+" Predecessor Check Failed...\n");
							jobsetPredBuffer.write(checkString+"\n");
							jobsetPredBuffer.write("\n");
							predecessorJobsetStatus.put(jmoLine,"NotExists");
							predecessorJobsetStatus.put(checkString, "NotExists");
						}
						/*
						if(jobList.contains(checkString))
						{
							logger.info("Jobset Pred: "+checkString+" exists");
							predecessorJobsetStatus.put(checkString, "Exists");
						}
						else
						{
							logger.error("Jobset Pred: "+checkString+" doesnt exist");
							logger.error(jmoLine);
							jobsetPredBuffer.write(jmoLine+" Predecessor Check Failed...\n");
							jobsetPredBuffer.write(checkString+"\n");
							jobsetPredBuffer.write("\n");
							predecessorJobsetStatus.put(jmoLine,"NotExists");
							predecessorJobsetStatus.put(checkString, "NotExists");
						}*/
						
					}
					else if(jmoLine.contains("TRID"))
					{
						tridIndex=jmoLine.indexOf("TRID");
						trevIndex=jmoLine.indexOf("TREV");
						if(jmoLine.contains("WORKDAY"))
						{
							workdayIndex=jmoLine.indexOf("WORKDAY");
						}
						startIndex=jmoLine.indexOf(jobsetPredDefString)+jobsetPredDefString.length();
						//endIndex=jmoLine.indexOf("WORKDAY");
						tempIndex=Math.min(tridIndex, trevIndex);
						if(workdayIndex!=0)
						{
							endIndex=Math.min(tempIndex, workdayIndex);
						}
						else
						{
							endIndex=tempIndex;
						}
						String jobsetName=jmoLine.substring(startIndex, endIndex).trim();
						/*startIndex=jmoLine.indexOf("TREV")+"TREV=".length();
						endIndex=jmoLine.indexOf("TRID");
						String predecessorTriggerType=jmoLine.substring(startIndex, endIndex).trim();
						startIndex=jmoLine.indexOf("TRID")+"TRID=".length();
						String predeceesorTriggerName=jmoLine.substring(startIndex).trim();*/
						startIndex=jmoLine.indexOf("TREV")+"TREV=".length();
						String predecessorTriggerType="";
						if(trevIndex>tridIndex && trevIndex>workdayIndex)
						{
							predecessorTriggerType=jmoLine.substring(startIndex).trim();
						}
						else
							if(workdayIndex<tridIndex && workdayIndex<trevIndex && tridIndex<trevIndex)
							{
								endIndex=trevIndex;
								predecessorTriggerType=jmoLine.substring(startIndex, endIndex).trim();
							}
							else if(tridIndex<trevIndex && tridIndex<workdayIndex && workdayIndex<trevIndex)
							{
								endIndex=workdayIndex;
								predecessorTriggerType=jmoLine.substring(startIndex, endIndex).trim();
							}
						//endIndex=jmoLine.indexOf("TRID");
						
						startIndex=jmoLine.indexOf("TRID")+"TRID=".length();
						String predeceesorTriggerName="";
						if(tridIndex<trevIndex && workdayIndex<tridIndex)
						{
							endIndex=trevIndex;
							predeceesorTriggerName=jmoLine.substring(startIndex, endIndex).trim();
						}
						String checkString=predeceesorTriggerName;
						logger.info("Checking for "+checkString+" in the file: "+jmoFile);
						boolean doesPredExist = checkIfPredExists(checkString, jmoFile);
						if(doesPredExist==true)
						{
							logger.info("Job Pred: "+checkString+" exists");
							predecessorJobsetStatus.put(checkString, "Exists");
						}
						else
						{
							logger.error("Job Pred: "+checkString+" doesnt exist");
							logger.error(jmoLine);
							jobsetPredBuffer.write(jmoLine+" Predecessor Check Failed...\n");
							jobsetPredBuffer.write(checkString+"\n");
							jobsetPredBuffer.write("\n");
							predecessorJobsetStatus.put(jmoLine,"NotExists");
							predecessorJobsetStatus.put(checkString, "NotExists");
						}
						/*if(triggerList.contains(checkString))
						{
							logger.info("Jobset Pred: "+checkString+" exists");
							predecessorJobsetStatus.put(checkString, "Exists");
						}
						else
						{
							logger.error("Jobset Pred: "+checkString+" doesnt exist");
							logger.error(jmoLine);
							jobsetPredBuffer.write(jmoLine+" Predecessor Check Failed...\n");
							jobsetPredBuffer.write(checkString+"\n");
							jobsetPredBuffer.write("\n");
							predecessorJobsetStatus.put(jmoLine,"NotExists");
							predecessorJobsetStatus.put(checkString, "NotExists");
						}*/
					}
				}
					
			}
			jobsetPredBuffer.close();
			jobsetPredecessorWriter.close();
			jmoBuffer.close();
			jmoExtractReader.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		logger.info("Done with jobset predecessor checks");
	}
}
