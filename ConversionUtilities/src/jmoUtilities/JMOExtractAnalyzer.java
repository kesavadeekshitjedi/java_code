package jmoUtilities;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

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
	static List<String> jobList=new ArrayList<String>();
	static List<String> fullJobList = new ArrayList<String>();
	static List<String> machineList = new ArrayList<String>();
	static List<String> jobsetList = new ArrayList<String>();
	static List<String> triggerList = new ArrayList<String>();
	static List<String> calendarList = new ArrayList<String>();
	static Map<String, String> predecessorJobStatus = new HashMap<String, String>();
	static Map<String, String> predecessorJobsetStatus = new HashMap<String, String>();
	static Map<String, List<String>> jobsetJobMap = new HashMap<String, List<String>>();
	static Map<String, List<String>> calendarJobMap = new HashMap<String, List<String>>();
	static Map<String, List<String>> jobDependencyMap = new HashMap<String, List<String>>();
	static Map<String, String> machineMap = new HashMap<String,String>();
	
	public void readJMOExtractHighLevel(String jmoFile)
	{
		
		
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
					fullJobList.add(jmoLine);
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
				else if (jmoLine.contains(jobsetDefString))
				{
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
				else if(jmoLine.contains(machineDefString))
				{
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
				
			}
			logger.info("Parsing JMO Extract is complete.");
			checkJobPredecessors(jmoFile);
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
							predecessorJobStatus.put(predecessorJobName, "Exists");
						}
						else
						{
							logger.error("Job Pred: "+checkString+" doesnt exist");
							predecessorJobStatus.put(predecessorJobName, "NotExists");
						}
						
					}
					else if ((jmoLine.contains("PSET") && (!jmoLine.contains("PJOB"))))
					{
						logger.info("Job Depends on jobset only. Not on job.");
						endIndex=jmoLine.indexOf("PSET");
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
							endIndex=jmoLine.indexOf("TREV");
							
						}
						else if(jmoLine.contains("TREV"))
						{
							endIndex=jmoLine.indexOf("TREV");
							//String triggerType=jmoLine.substring(startIndex,endIndex).trim();
							String tempJobString=jmoLine.substring(startIndex, endIndex).trim();
							String[] tempJobTuple=tempJobString.split(",");
							String jobsetName=tempJobTuple[0].replace("(", "");
							String jobName=tempJobTuple[1].trim();
							String jobNumber=tempJobTuple[2].replace(")", "");
						}
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
