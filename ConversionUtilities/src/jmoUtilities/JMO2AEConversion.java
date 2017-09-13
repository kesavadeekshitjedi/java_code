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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import mainUtils.JilUtilMain;

public class JMO2AEConversion 
{
	static String jobLenghtErrorMessage="JMO2AE_JOB_NAME_LENGTH_ERROR";
	static String infoLineMessage="JMO2AE_INFO_MESSAGE";
	static String warnLineMessage="JMO2AE_WARN_MESSAGE";
	
	static String jobSeperator="-";
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
	
	static Map<String, List<String>> box_job_map = new HashMap<String, List<String>>(); // This stores the jobset to job/jobnumber relationship.
	static Map<String, String> jobnameMap = new HashMap<String, String>(); // This contains the original jobname and the modified jobname. the jobname will only be modified if it is already a duplicate.
	static List<String> createdJobList = new ArrayList<String>();
	static String extractFolder;
	static String aeJobsFile;
	static String aeMachFile;
	static String aeResFile;
	static FileWriter fileWriter;
	static BufferedWriter writeBuffer;
	static FileReader jmoExtractReader;
	static BufferedReader jmoBuffer;
	
	String contentLine="";
	String jobContentLine="";
	
	/*
	 * 1. Read the file and create the box job to job+jobNumber map
	 * 2. For each box name in the Map, get the calendar/schedule information
	 *  If the calendar is the only key in the define jobset line, then the run_calendar is the calendar that is being used by the jobset
	 *  if Critkeys and calendar exist, then critkeys takes precedence.
	 *  CRITHACT Reference:
	 *  O - Include Holidays Only
	 *  S - Include the day whether it is a Holiday or not
	 *  N - Include Next day even when the next day is also a holiday
	 *  W - Include next workday
	 *  P - Include previous workday
	 *  
	 *  
	 *  blank Do not schedule if the day is a holiday.
		O  Schedule only if the day is a holiday
		S  Schedule regardless if the day is a holiday
		N  If the day is a holiday schedule on the next physical day (could be a holiday)
		W  If the day is a holiday schedule on the following workday
		P  If the day is a holiday schedule on the previous workday

	 * 3. Create the jobset once we have the schedule info and the jobset name info
	 * 	3a. get the 
	 * 4. For each job in the jobset map, do the following:
	 * 	4a. if the job already exists in the joblist, then add the .m1 identifier
	 *  4b. Store the original jobname and new jobname in a Map<String, String>
	 *  	4b.a the format will be jobset.job.jobnumber=[new jobset.job.m1.jobnumber]
	 * 5.for each job, get the following:
	 * 	5a. Unique Job Name
	 * 	5b. Command
	 * 	5c. Owner
	 * 	5d. resources
	 * 	5e. std_out/err
	 * 	5f. condition attribute
	 * 	5g. schedule info
	 * 
	 *  
	 * 
	 * 
	 * 
	 * 
	 */
	
	public void setFilePaths(String jmoFileName)
	{
		Path p = Paths.get(jmoFileName);
		Path folderPath = p.getParent();
		System.out.println("File is at this location: "+folderPath);
		String extractFolder=folderPath+"\\"+JilUtilMain.myTime+"_JMO2AE_Definitions";
		File myextractFolder=new File(extractFolder);
		myextractFolder.mkdir();
		System.out.println("Folder "+extractFolder+" created...");
		aeMachFile=extractFolder+"\\MACHINES___FILE.jil";
		aeJobsFile=extractFolder+"\\JOBS___FILE.jil";
		aeResFile=extractFolder+"\\RESOURCES___FILE.jil";
		try 
		{
			readExtractForJobs(jmoFileName);
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void readExtractForJobs(String jmoFile) throws IOException, FileNotFoundException
	{
		logger=Logger.getLogger("ConversionUtilities.jmoUtilities.JMO2AEConversion.getBoxJobMap");
		jmoExtractReader = new FileReader(jmoFile);
		jmoBuffer = new BufferedReader(jmoExtractReader);
		String currentJMOLine="";
		int startIndex=0;
		int endIndex=0;
		String tempString="";
		int stationIndex=0;
		int failcondIndex=0;
		int descIndex=0;
		int tempIndex=0;
		
		
		while((currentJMOLine=jmoBuffer.readLine())!=null)
		{
			
			String jmoLine=currentJMOLine.trim();
			if((jmoLine.contains(jobDefString)))
			{
				startIndex=jmoLine.indexOf(jobDefString)+jobDefString.length();
				if(jmoLine.contains("DESCRIPTION") && !(jmoLine.contains("FAILCOND")))
				{
					descIndex=jmoLine.indexOf("DESCRIPTION");
				}
				else if(jmoLine.contains("FAILCOND"))
				{
					failcondIndex=jmoLine.indexOf("FAILCOND");
				}
				else if(jmoLine.contains("STATION"))
				{
					stationIndex=jmoLine.indexOf("STATION");
				}
				if(descIndex< failcondIndex)
				{
					tempIndex=descIndex;
				}
				else
				{
					tempIndex=failcondIndex;
				}
				if(tempIndex<stationIndex)
				{
					endIndex=tempIndex;
				}
				else
				{
					endIndex=stationIndex;
				}
				tempString=jmoLine.substring(startIndex, endIndex).trim();
				String[] jobtuple=tempString.split(",");
				String jobsetName=jobtuple[0].replace("(", "").trim();
				String jobName=jobtuple[1].trim();
				String jobNumber=jobtuple[2].replace(")", "").trim();
				String aeJobName=jobsetName+jobSeperator+jobName+jobSeperator+jobNumber.trim();
				if(aeJobName.length()>64)
				{
					contentLine+=jobLenghtErrorMessage+" JobName exceeds 64 characters. Job: "+aeJobName+" is "+aeJobName.length()+" long";
				}
				else
				{
					contentLine+=infoLineMessage+"Job name obtained: "+aeJobName;
					
				}
				contentLine+=infoLineMessage+"job_type identified as CMD";
				contentLine+=infoLineMessage+"box_name identified: "+jobsetName;
				String boxCondition=getJobCondition(jmoFile, jobsetName, "&");
				
				
				
			}
		}
	}
	
	
	public void writeToFile(String fileName, String content) throws IOException
	{
		fileWriter= new FileWriter(fileName);
		writeBuffer = new BufferedWriter(fileWriter);
		try
		{
			writeBuffer.write(content+"\n");
			
		}
		finally
		{
			System.out.println("Closing all writers and buffers for file: "+fileName);
			writeBuffer.close();
			fileWriter.close();
			System.out.println("Closed all writers and buffers for file: "+fileName);
		}
		
	}
	public void readExtractToCreateJobStructure(String jmoFile) throws IOException
	{


		logger=Logger.getLogger("ConversionUtilities.jmoUtilities.readExtractToCreateJobStructure");
		
		String jobType="";
		String conditionSeperator="";
		FileWriter aeJobDefWriter;
		BufferedWriter aeJobDefBuffer;
		FileWriter aeMachineDefWriter;
		BufferedWriter aeMachineDefBuffer;
		FileWriter aeResDefWriter;
		BufferedWriter aeResDefBuffer;
		FileReader jmoReader;
		BufferedReader jmoBuffer;
		
		
		/*aeJobDefWriter=new FileWriter(aeJobsFile);
		aeMachineDefWriter = new FileWriter(aeMachinesFile);
		aeResDefWriter = new FileWriter(aeResFile);
		aeJobDefBuffer = new BufferedWriter(aeJobDefWriter);
		aeMachineDefBuffer=new BufferedWriter(aeMachineDefWriter);
		aeResDefBuffer=new BufferedWriter(aeResDefWriter);*/
		jmoReader = new FileReader(jmoFile);
		jmoBuffer = new BufferedReader(jmoReader);
		String currentJMOLine="";
		int startIndex=0;
		int endIndex=0;
		String tempString="";
		while((currentJMOLine=jmoBuffer.readLine())!=null)
		{
			
			String jmoLine=currentJMOLine.trim();
			if((jmoLine.contains(jobDefString)))
			{
				
				
				if(jmoLine.contains(jobDefString))
				{
					conditionSeperator=" AND ";
					jobType="CMD";
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
					String jmoJobName=tempString;
					// At this point, the code has the tuple. Example: (ASETransfer_CDG,ASETransfer_CDG,0001). So the code knows the following:
					// jobname = jobsetname+"."+jmojobname+"."+jobNumber
					// box_name = jobsetname
					// We are not modifying anything to be lower case or uppercase or trimming the job number to remove the leading zeros.
					// If the jobname key is > 64 characters, write a comment in the aejobdef file
					// If the jobname key is > 59 characters, write a comment in the aejobdef file for JPM.
					String[] jobInfoTuple=tempString.split(",");
					String jmojobsetName=jobInfoTuple[0].replace("(", "").trim();
					String jmojobName=jobInfoTuple[1].trim();
					String jmojobNumber=jobInfoTuple[2].replace(")", "").trim();
					String aeJobName=jmojobsetName+jobSeperator+jmojobName+jobSeperator+jmojobNumber.trim();
					logger.debug("insert_job: "+aeJobName);
					logger.debug("job_type: "+jobType); // This is default. In the jobset section and Trigger section, the jobType variable needs to be modified.
					String jobOwner=getJobOwner(jmoFile, jmoJobName);
					logger.debug("owner: "+jobOwner);
					String jobCondition=getJobCondition(jmoFile, jmoJobName, conditionSeperator);
					logger.debug("condition: "+jobCondition);
					if((jmoLine.contains("AUTOSEL=Yes") || (jmoLine.contains("AUTOSEL=YES"))))
					{
						logger.debug("Job has autosel=yes");
						logger.debug("date_conditions: 1");
					}
					else
						if((jmoLine.contains("AUTOSEL=NO") || (jmoLine.contains("AUTOSEL=No"))))
						{
							logger.debug("AUTOSEL=NO");
							logger.debug("date_conditions: 0");
						}
					
					
							
					
				}
			}
		}
		
	}
	public void createBoxJobs(String jmoFile) throws FileNotFoundException
	{
		String jobType="BOX";
		Path p = Paths.get(jmoFile);
		Path folderPath = p.getParent();
		
		String extractFolder=folderPath+"\\"+JilUtilMain.myTime+"_AE_Definitions";
		File myextractFolder=new File(extractFolder);
		myextractFolder.mkdir();
		System.out.println("Folder "+extractFolder+" created...");
		String aeMachinesFile=extractFolder+"\\MACHINES___FILE.jil";
		String aeJobsFile=extractFolder+"\\JOBS___FILE.jil";
		logger=Logger.getLogger("ConversionUtilities.jmoUtilities.createBoxJobs");
		String boxName="";
		FileReader jmoExtractReader = new FileReader(jmoFile);
		BufferedReader jmoBuffer = new BufferedReader(jmoExtractReader);
		String currentJMOLine="";
		int startIndex=0;
		int endIndex=0;
		int failCondIndex=0;
		int descIndex=0;
		String tempString="";
		try
		{
			jmoExtractReader = new FileReader(jmoFile);
			jmoBuffer = new BufferedReader(jmoExtractReader);
			while((currentJMOLine=jmoBuffer.readLine())!=null)
			{
				
				String jmoLine=currentJMOLine.trim();
				if(jmoLine.contains("DEFINE JOBSET ID="))
				{
					logger.debug("Jobset Definition found. Creating AE Jobset");
					startIndex=jmoLine.indexOf(jobsetDefString)+jobsetDefString.length();
					if(jmoLine.contains("FAILCOND"))
					{
						failCondIndex=jmoLine.indexOf("FAILCOND");
						
					}
					if(jmoLine.contains("DESCRIPTION"))
					{
						descIndex=jmoLine.indexOf("DESCRIPTION");
					}
					// check if failcond is before description and whichever is before is the endIndex.
					
					if(failCondIndex<descIndex)
					{
						endIndex=failCondIndex;
					}
					else
					{
						endIndex=descIndex;
					}
					tempString=jmoLine.substring(startIndex,endIndex).trim();
					logger.debug("Jobset Name: "+tempString);
					logger.debug("insert_job: "+tempString);
					logger.debug("job_type: "+jobType);
					if(jmoLine.contains("DESCRIPTION"))
					{
						descIndex=jmoLine.indexOf("DESCRIPTION");
						startIndex=descIndex+"DESCRIPTION=".length();
					}
					if(jmoLine.contains("STATION"))
					{
						int stationIndex=jmoLine.indexOf("STATION");
						
					}
				}
			}
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
	}
	
	public String getResourceDependency(String jmoFile, String jmoJobName) throws IOException
	{
		logger=Logger.getLogger("ConversionUtilities.jmoUtilities.getResourceDependency");
		String resDependency="";
		FileReader jmoExtractReader = new FileReader(jmoFile);
		BufferedReader jmoBuffer = new BufferedReader(jmoExtractReader);
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
				if(jmoLine.contains("DEFINE JOBRES ID=("+jmoJobName))
				{
					logger.debug(jmoLine);
					startIndex=jmoLine.indexOf("DEFINE JOBRES ID=(")+"DEFINE JOBRES ID=(".length();
					endIndex=jmoLine.indexOf("AMOUNT");
					tempString=jmoLine.substring(startIndex, endIndex).trim();
					String[] resTuple=tempString.split(",");
					String resName=resTuple[2].replaceAll(")", "").trim();
					startIndex=jmoLine.indexOf("AMOUNT")+"AMOUNT=".length();
					endIndex=jmoLine.indexOf("WEIGHT");
					if(endIndex< startIndex)
					{
						logger.debug("reversing");
						int tempIndex=endIndex;
						endIndex=startIndex;
						startIndex=tempIndex;
						
					}
					String amount=jmoLine.substring(startIndex,endIndex).trim();
					resDependency="("+resName+","+amount+"FREE=Y)";
				}
			}
		}
		finally
		{
			jmoBuffer.close();
			jmoExtractReader.close();
		}
		
		return resDependency;
	}
	public String getJobCondition(String jmoFile, String jmoJobName, String conditionSeperator) throws IOException
	{
		logger=Logger.getLogger("ConversionUtilities.jmoUtilities.getJobCondition");
		String jobCondition="";
		String jobOwner="";
		FileReader jmoExtractReader = new FileReader(jmoFile);
		BufferedReader jmoBuffer = new BufferedReader(jmoExtractReader);
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
				if(jmoLine.contains("DEFINE JOBPRED ID="+jmoJobName))
				{
					logger.info("Job Predecessor info found");
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
						String aejobName="s("+predecessorJobsetName+jobSeperator+predecessorJobName+jobSeperator+predecessorJobNumber.trim()+")";
						jobCondition+=aejobName;
								
						
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
						}
					}
					
				}
			}
		}
		
		finally
		{
			jmoBuffer.close();
			jmoExtractReader.close();
		}
		
		return jobCondition;
	}
	public String getJobOwner(String jmoFile, String jmoJobName) throws IOException
	{
		logger=Logger.getLogger("ConversionUtilities.jmoUtilities.getJobOwner");
		String jobOwner="";
		FileReader jmoExtractReader = new FileReader(jmoFile);
		BufferedReader jmoBuffer = new BufferedReader(jmoExtractReader);
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
				if(jmoLine.contains("DEFINE JOBPARM ID="+jmoJobName))
				{
					logger.info("job parameter found. Extracting owner");
					startIndex=jmoLine.indexOf("SUBUSER")+"SUBUSER=".length();
					tempString=jmoLine.substring(startIndex).trim();
					jobOwner=tempString;
					break;
				}
			}
		}
		
		finally
		{
			jmoBuffer.close();
			jmoExtractReader.close();
		}
		return jobOwner;
	}
	
	public String getJobCommand(String jmoFile, String jmoJobName)
	{
		logger=Logger.getLogger("ConversionUtilities.jmoUtilities.getJobCommand");
		String jobCommand="";
		
		return jobCommand;
	}
	

}
