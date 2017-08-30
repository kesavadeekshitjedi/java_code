package jmoUtilities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
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
					startIndex=jmoLine.indexOf("NODE")+"NODE".length();
					endIndex=jmoLine.indexOf("NODETYPE");
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
					endIndex=jmoLine.indexOf("WEIGHT");
					resAmt=jmoLine.substring(startIndex,endIndex).trim();
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
			jmoReportWriter.write("------------------------JMO Object Report created @ "+JilUtilMain.myTime+"------------------------"+"\n");
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
			jmoReportWriter.write("------------------------Job Predecessor Report------------------------"+"\n");
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
				if(predCheckValue.equalsIgnoreCase("NotExist")|| predCheckValue.equalsIgnoreCase("PREDCHECKFAIL"))
				{
					jmoReportWriter.write(jobName+"   "+predCheckValue+"\n");
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
				if(predCheckValue.equalsIgnoreCase("NotExists") || predCheckValue.equalsIgnoreCase("PREDCHECKFAIL"))
				{
					jmoReportWriter.write(jobsetName+"   "+predCheckValue+"\n");
				}
			}
			jmoReportWriter.write("------------------------ End Jobset Predecessor Report------------------------"+"\n");
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

	public void createReport(String jmoFile) throws IOException
	{
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
	public void checkJobPredecessors(String jmoFile)
	{

		logger=Logger.getLogger("ConversionUtilities.jmoUtilities.checkJobPredecessors");
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
				if(jmoLine.contains(jobPredDefString))
				{
					logger.info("Job Predecessor Found");
					logger.debug(jmoLine);
					startIndex=jmoLine.indexOf(jobPredDefString)+jobPredDefString.length();
					if(jmoLine.contains("PJOB"))
					{
						endIndex=jmoLine.indexOf("PJOB");
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
						logger.info("Checking for "+checkString+" in jobs List");
						if(fullJobList.contains(checkString))
						{
							logger.info("Job Pred: "+checkString+" exists");
							predecessorJobStatus.put(checkString, "Exists");
						}
						else
						{
							logger.error("Job Pred: "+checkString+" doesnt exist");
							logger.error(jmoLine);
							predecessorJobStatus.put(overallJobName,"PREDCHECKFAIL");
							predecessorJobStatus.put(checkString, "NotExists");
						}
						
					}
					else if ((jmoLine.contains("PSET") && (!jmoLine.contains("PJOB"))))
					{
						logger.info("Job Depends on jobset only. Not on job.");
						
						endIndex=jmoLine.indexOf("PSET");
						String tempJobString=jmoLine.substring(startIndex, endIndex).trim();
						String[] tempJobTuple=tempJobString.split(",");
						String jobsetName=tempJobTuple[0].replace("(", "");
						String jobName=tempJobTuple[1].trim();
						String jobNumber=tempJobTuple[2].replace(")", "");
						String overallJobName="("+jobsetName+","+jobName+","+jobNumber+")";
						startIndex=jmoLine.indexOf("PSET=")+"PSET=".length();
						endIndex=jmoLine.indexOf("WORKDAY");
						String tempPSETString=jmoLine.substring(startIndex, endIndex).trim();
						String checkString=tempPSETString.trim();
						logger.info("Checking for "+checkString+" in the jobset list");
						if(jobsetList.contains(checkString))
						{
							logger.info("Job Pred: "+checkString+" exists");
							predecessorJobStatus.put(checkString, "Exists");
						}
						else
						{
							logger.error("Job Pred: "+checkString+" doesnt exist");
							logger.error(jmoLine);
							predecessorJobStatus.put(overallJobName,"PREDCHECKFAIL");
							predecessorJobStatus.put(checkString, "NotExists");
						}
						
					}
					else if (jmoLine.contains("TRID"))
					{
						logger.info("Job Depends on Trigger.");
						startIndex=jmoLine.indexOf(jobPredDefString)+jobPredDefString.length();
						if(jmoLine.contains("WORKDAY"))
						{
							endIndex=jmoLine.indexOf("WORKDAY");
							String tempJobString=jmoLine.substring(startIndex, endIndex).trim();
							String[] tempJobTuple=tempJobString.split(",");
							String jobsetName=tempJobTuple[0].replace("(", "");
							String jobName=tempJobTuple[1].trim();
							String jobNumber=tempJobTuple[2].replace(")", "");
							String overallJobName="("+jobsetName+","+jobName+","+jobNumber+")";
							startIndex=jmoLine.indexOf("TREV")+"TREV=".length();
							endIndex=jmoLine.indexOf("TRID");
							String predTriggerType=jmoLine.substring(startIndex,endIndex).trim();
							startIndex=jmoLine.indexOf("TRID")+"TRID=".length();
							String predTriggerName=jmoLine.substring(startIndex).trim();
							String checkString=predTriggerName;
							logger.info("Checking for Trigger: "+checkString+" in Trigger List");
							if(triggerList.contains(checkString))
							{
								logger.info("Job Pred: "+checkString+" exists");
								predecessorJobStatus.put(checkString, "Exists");
							}
							else
							{
								logger.error("Job Pred: "+checkString+" doesnt exist");
								logger.error(jmoLine);
								predecessorJobStatus.put(overallJobName,"PREDCHECKFAIL");
								predecessorJobStatus.put(checkString, "NotExists");
							}
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
		}
		
		
		catch(Exception e)
		{
			e.printStackTrace();
		}
		logger.info("Done with job predecessor checks");
		
	}
	
	public void checkJobsetPredecessors(String jmoFile)
	{
		logger=Logger.getLogger("ConversionUtilities.jmoUtilities.checkJobsetPredecessors");
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
				if(jmoLine.contains(jobsetPredDefString))
				{
					logger.info("Jobset Predecessor found");
					logger.debug(jmoLine);
					if(jmoLine.contains("PSET") && !jmoLine.contains("PJOB")) 
					{
						
						startIndex=jmoLine.indexOf(jobsetPredDefString)+jobsetPredDefString.length();
						endIndex=jmoLine.indexOf("PSET");
						String jobsetName=jmoLine.substring(startIndex, endIndex).trim();
						startIndex=jmoLine.indexOf("PSET")+"PSET=".length();
						endIndex=jmoLine.indexOf("WORKDAY");
						String predecessorJobset=jmoLine.substring(startIndex, endIndex).trim();
						String checkString=predecessorJobset;
						logger.info("Checking for "+checkString+" in jobsets list");
						if(jobsetList.contains(checkString))
						{
							logger.info("Jobset Pred: "+checkString+" exists");
							predecessorJobsetStatus.put(checkString, "Exists");
						}
						else
						{
							logger.error("Jobset Pred: "+checkString+" doesnt exist");
							logger.error(jmoLine);
							predecessorJobsetStatus.put(jobsetName,"PREDCHECKFAIL");
							predecessorJobsetStatus.put(checkString, "NotExists");
						}
						
						
						
					}
					else if(jmoLine.contains("PJOB"))
					{
						startIndex=jmoLine.indexOf(jobsetPredDefString)+jobsetPredDefString.length();
						endIndex=jmoLine.indexOf("PJOB");
						String jobsetName=jmoLine.substring(startIndex, endIndex).trim();
						startIndex=jmoLine.indexOf("PJOB")+"PJOB=".length();
						endIndex=jmoLine.indexOf("PSET");
						String predecessorJobName=jmoLine.substring(startIndex,endIndex).trim();
						startIndex=jmoLine.indexOf("PSET")+"PSET=".length();
						endIndex=jmoLine.indexOf("PJNO");
						String predecessorJobsetName=jmoLine.substring(startIndex,endIndex).trim();
						startIndex=jmoLine.indexOf("PJNO")+"PJNO=".length();
						endIndex=jmoLine.indexOf("WORKDAY");
						String predecessorJobNumber=jmoLine.substring(startIndex, endIndex).trim();
						String checkString="("+predecessorJobsetName+","+predecessorJobName+","+predecessorJobNumber+")";
						logger.info("Checking for "+checkString+" in jobs list");
						if(jobList.contains(checkString))
						{
							logger.info("Jobset Pred: "+checkString+" exists");
							predecessorJobsetStatus.put(checkString, "Exists");
						}
						else
						{
							logger.error("Jobset Pred: "+checkString+" doesnt exist");
							logger.error(jmoLine);
							predecessorJobsetStatus.put(jobsetName,"PREDCHECKFAIL");
							predecessorJobsetStatus.put(checkString, "NotExists");
						}
						
					}
					else if(jmoLine.contains("TRID"))
					{
						startIndex=jmoLine.indexOf(jobsetPredDefString)+jobsetPredDefString.length();
						endIndex=jmoLine.indexOf("WORKDAY");
						String jobsetName=jmoLine.substring(startIndex, endIndex).trim();
						startIndex=jmoLine.indexOf("TREV")+"TREV=".length();
						endIndex=jmoLine.indexOf("TRID");
						String predecessorTriggerType=jmoLine.substring(startIndex, endIndex).trim();
						startIndex=jmoLine.indexOf("TRID")+"TRID=".length();
						String predeceesorTriggerName=jmoLine.substring(startIndex).trim();
						String checkString=predeceesorTriggerName;
						logger.info("Checking for "+checkString+" in Trigger List");
						if(triggerList.contains(checkString))
						{
							logger.info("Jobset Pred: "+checkString+" exists");
							predecessorJobsetStatus.put(checkString, "Exists");
						}
						else
						{
							logger.error("Jobset Pred: "+checkString+" doesnt exist");
							logger.error(jmoLine);
							predecessorJobsetStatus.put(jobsetName,"PREDCHECKFAIL");
							predecessorJobsetStatus.put(checkString, "NotExists");
						}
					}
				}
					
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		logger.info("Done with jobset predecessor checks");
	}
}
