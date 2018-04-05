package com.rmt.goldman.analysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.sun.javafx.binding.Logging;

public class AnalyzeJils
{
	static Logger logger = Logger.getRootLogger();
	int commandJobCount;
	int fileWatcherCount;
	int boxJobCount;
	static List<String> commandJobList = new ArrayList<String>();
	static List<String> fwJobList = new ArrayList<String>();
	static List<String> boxJobList = new ArrayList<String>();
	
	static FileReader inputReader;
	static BufferedReader inputBuffer;
	static FileWriter outputWriter;
	static BufferedWriter outputBuffer;
	
	static Map<String, List<String>> badJobList = new HashMap<String,List<String>>();
	static String inputJobDefFolder="D:\\OneDrive-Business\\OneDrive - Robert Mark Technologies\\Goldman\\gs_jobdef\\extracts";
	static String reportFolder = "D:\\OneDrive-Business\\OneDrive - Robert Mark Technologies\\Goldman\\gs_jobdef\\reports";
	
	static FileReader inputJilBoxReader;
	static BufferedReader inputBoxBuffer;
	static FileWriter outputBoxReportWriter;
	static BufferedWriter outputBoxBuffer;
	
	static BufferedWriter outputBoxReportWriters;
	static FileReader singleBoxReader;
	static BufferedReader singleBoxBufferReader;
	static FileWriter singleBoxWriter;
	static BufferedWriter singleBoxBufferWriter;
	public static void main(String[] args) throws IOException 
	{
		
		String log4jLocation = "resources/log4j.properties";
		PropertyConfigurator.configure(log4jLocation);
		logger=Logger.getLogger("JilUtilities.GS.AnalyzeJils.main");
		
		
		Map<String, List<String>> commandJobMap = new HashMap<String, List<String>>();
		Map<String, List<String>> fwJobMap = new HashMap<String, List<String>>();
		Map<String, List<String>> boxJobMap = new HashMap<String, List<String>>();
		
		
		String jobName,jobType;
		File folder = new File(inputJobDefFolder);
		File[] listOfInstanceFiles  = folder.listFiles();
		for(int i=0;i<listOfInstanceFiles.length;i++)
		{
			if(listOfInstanceFiles[i].isFile())
			{
				String currentFileName = listOfInstanceFiles[i].getName();
				logger.info("Reading "+currentFileName);
				String[] instanceNameArray=currentFileName.split("\\.");
				String instanceName=instanceNameArray[0].trim();
				String fileToRead = inputJobDefFolder+"\\"+currentFileName.trim();
				inputReader = new FileReader(fileToRead);
				inputBuffer = new BufferedReader(inputReader);
				outputWriter = new FileWriter(reportFolder+"\\CountInfo_"+instanceName+".txt");
				outputBuffer = new BufferedWriter(outputWriter);
				String currentJobLine = null;
				while((currentJobLine=inputBuffer.readLine())!=null)
				{
					String currentJilLine=currentJobLine.trim();
					if(!currentJilLine.contains("#") && (!currentJilLine.isEmpty() && (!currentJilLine.contains("/*") && (!currentJilLine.contains("//")))))
					{
						//logger.debug(currentJobLine);
						String[] lineSplitter=currentJilLine.split(":");
						if(lineSplitter.length==3 && lineSplitter[0].contains("insert_job"))
						{
							
							if(lineSplitter.length==3 && lineSplitter[0].contains("insert_job"))
							{
								String[] jobLine=lineSplitter[1].trim().split(" ");
								jobName=jobLine[0].trim();
								jobType= lineSplitter[2].trim();
								//System.out.println(jobName+":"+jobType);
								if(jobType.equalsIgnoreCase("c"))
								{
									if(!commandJobMap.containsKey(instanceName))
									{
										List<String> jobList = new ArrayList<String>();
										jobList.add(jobName);
										commandJobMap.put(instanceName, jobList);
									}
									else
									{
										List<String> jobList = new ArrayList<String>();
										jobList = commandJobMap.get(instanceName);
										jobList.add(jobName);
										commandJobMap.put(instanceName, jobList);
									}
								}
								if(jobType.equalsIgnoreCase("f"))
								{
									if(!fwJobMap.containsKey(instanceName))
									{
										List<String> fwJobList = new ArrayList<String>();
										fwJobList.add(jobName);
										fwJobMap.put(instanceName, fwJobList);
									}
									else
									{
										List<String> fwJobList = new ArrayList<String>();
										fwJobList = fwJobMap.get(instanceName);
										fwJobList.add(jobName);
										fwJobMap.put(instanceName, fwJobList);
									}
								}
								if(jobType.equalsIgnoreCase("b"))
								{
									if(!boxJobMap.containsKey(instanceName))
									{
										List<String> boxJobList = new ArrayList<String>();
										boxJobList.add(jobName);
										boxJobMap.put(instanceName, boxJobList);
									}
									else
									{
										List<String> boxJobList = new ArrayList<String>();
										boxJobList = boxJobMap.get(instanceName);
										boxJobList.add(jobName);
										boxJobMap.put(instanceName, boxJobList);
									}
								}

							}
						}
					}
					
				}
				
				Iterator commandIterator = commandJobMap.entrySet().iterator();
				while(commandIterator.hasNext())
				{
					Map.Entry instanceCommandPair = (Entry) commandIterator.next();
					String inst = (String) instanceCommandPair.getKey();
					List<String> instCmdList = (List<String>) instanceCommandPair.getValue();
					System.out.println("Total Command jobs for "+inst+" is: "+instCmdList.size());
					outputBuffer.write("Total number of CMD jobs for "+inst+" is : "+instCmdList.size());
						
				}
				
				Iterator boxIterator = boxJobMap.entrySet().iterator();
				while(boxIterator.hasNext())
				{
					Map.Entry instanceBoxPair = (Entry) boxIterator.next();
					String inst = (String) instanceBoxPair.getKey();
					List<String> instBoxList = (List<String>) instanceBoxPair.getValue();
					System.out.println("Total BOX jobs for "+inst+" is: "+instBoxList.size());
					outputBuffer.write("Total number of CMD jobs for "+inst+" is : "+instBoxList.size());
				}
				
				Iterator fwIterator = fwJobMap.entrySet().iterator();
				while(fwIterator.hasNext())
				{
					Map.Entry instanceFWPair = (Entry) fwIterator.next();
					String inst = (String) instanceFWPair.getKey();
					List<String> instFWList = (List<String>) instanceFWPair.getValue();
					System.out.println("Total FW jobs for "+inst+" is: "+instFWList.size());
					outputBuffer.write("Total number of CMD jobs for "+inst+" is : "+instFWList.size());
				}
				
				AnalyzeJils.getJobsWithOtherConditions(fileToRead, instanceName);
				AnalyzeJils.getBoxesWithSingleJob(fileToRead, instanceName);
				AnalyzeJils.getJobsNotInBoxes(fileToRead, instanceName);
				Iterator badListIterator = badJobList.entrySet().iterator();
				while(badListIterator.hasNext())
				{
					Map.Entry badPair = (Entry) badListIterator.next();
					String inst = (String) badPair.getKey();
					List<String> badListJobs = (List<String>) badPair.getValue();
					System.out.println("Number of bad jobs for "+inst+"is "+badListJobs.size());
					outputBuffer.write("Number of bad jobs (jobs with conditions other than success) for  "+inst+" is : "+badListJobs.size());
				}
				
				outputBuffer.close();
				outputWriter.close();
				
				
			}
		}
		AnalyzeJils.closeStuff();
	}
	
	public static void getJobsWithSuccessOrFailConditions(String fileName, String instanceName) throws IOException
	{
		/*
		 * This method reads the instance job definition files to get the list of jobs per instance that use the success_codes and failure_codes attributes.
		 * These attributes are not supported in procmon. Procmon only treats 0 as a success and there is no easy way to treat a non-zero value as a success.
		 * There are currently some workarounds employed to get past this limitation.
		 */
		logger=Logger.getLogger("JilUtiliies.GS.AnalyzeJils.getJobsWithSuccessOrFailConditions");
		
	}

	public static void getJobsWithCrossInstanceDependencies(String fileName, String instanceName) throws IOException
	{
		/*
		 * This method reads the instance jil files to get the information about jobs that have a cross instance dependency condition. 
		 */
		logger=Logger.getLogger("JilUtilities.GS.AnalyzeJils.getJobsWithCrossInstanceDependencies");
	}
	public static void getBoxesWithSingleJob(String fileName, String instanceName) throws IOException
	{
		/*
		 * This method goes through the jil file and provides a count of boxes (per instance) that have only one job within them.
		 */
		
		
		inputJilBoxReader = new FileReader(fileName);
		inputBoxBuffer = new BufferedReader(inputJilBoxReader);
		outputBoxReportWriter = new FileWriter(reportFolder+"\\BoxesSingleJob.txt");
		
		outputBoxBuffer = new BufferedWriter(outputBoxReportWriter);
		String jilLine=null;
		String boxName=null;
		String jobName=null;
		Map<String, List<String>> boxRelationMap = new HashMap<String, List<String>>();
		List<String> childJobsinBox;
		boolean foundNewJob=false;
		while((jilLine=inputBoxBuffer.readLine())!=null)
		{
			
			if(jilLine.contains("insert_job:"))
			{
				String[] jobLineTuple=jilLine.split(":");
				jobName=jobLineTuple[1].split(" ")[0].trim();
				foundNewJob=true;
			}
			if(jilLine.contains("box_name") && foundNewJob==true)
			{
				String[] boxLineTuple = jilLine.split(":");
				boxName=boxLineTuple[1].trim();
				foundNewJob=false;
			}
			childJobsinBox=new ArrayList<String>();
			childJobsinBox.add(jobName);
			if(!boxRelationMap.containsKey(boxName))
			{
				boxRelationMap.put(boxName, childJobsinBox);
			}
			else
			{
				childJobsinBox=new ArrayList<String>();
				childJobsinBox=boxRelationMap.get(boxName);
				childJobsinBox.add(jobName);
				boxRelationMap.put(boxName, childJobsinBox);
			}
		}
		Iterator boxRelIterator = boxRelationMap.entrySet().iterator();
		Map.Entry boxPair = (Map.Entry)boxRelIterator.next();
		String box=(String)boxPair.getKey();
		List<String> boxVals = (List<String>)boxPair.getValue();
		outputBoxBuffer.write("BOX: "+box+" in instance: "+instanceName+" has "+boxVals.size()+" jobs");
		
	}
	public static void getJobsNotInBoxes(String fileName, String instanceName) throws IOException
	{
		singleBoxReader = new FileReader(fileName);
		singleBoxBufferReader = new BufferedReader(singleBoxReader);
		
		singleBoxWriter = new FileWriter(reportFolder+"\\SingleJobsReport_"+instanceName+".txt");
		singleBoxBufferWriter = new BufferedWriter(singleBoxWriter);
		
		String jilLine=null;
		String jobName=null;
		List<String> singleJobs = new ArrayList<String>();
		boolean foundNewJob=false;
		while((jilLine=singleBoxBufferReader.readLine())!=null)
		{
			if(jilLine.contains("insert_job"))
			{
				String[] jobLineTuple=jilLine.split(":");
				String tjobName=jobLineTuple[1].trim();
				jobName=tjobName.split(" ")[0];
				foundNewJob=true;
			}
			if(jilLine.contains("box_name") && foundNewJob==true)
			{
				logger.info(jobName+" is part of box");
			}
			else
			{
				singleBoxBufferWriter.write(jobName+" is single");
			}
		}
	}
	public static void closeStuff() throws IOException
	{
		
		outputBoxBuffer.close();
		outputBoxReportWriter.close();
		singleBoxBufferWriter.close();
		singleBoxWriter.close();
		singleBoxBufferReader.close();
	}
	public static void getJobsWithOtherConditions(String fileName, String instanceName) throws IOException
	{
		
		/*
		 * This method reads the instance job definition files and checks to see if the jobs have a condition other than success. since these are not supported
		 * in procmon, the job names that have these conditions as well as the counts (for the purpose of the gap analysis) need to be pointed out.
		 * This method is called from the main method. the first part of the main method always has the file to read and with that, we get the instance name.
		 * The getJobsWithOtherConditions method reads the file and instance name and stores a hashmap of job information as values with the key being the instance name
		 */
		FileReader jilFileReader = new FileReader(fileName);
		BufferedReader jilBuffer = new BufferedReader(jilFileReader);
		
		logger = Logger.getLogger("JilUtilities.GS.AnalyzeJils.getJobsWithOtherConditions");
		String jobName = null;
		String conditionLine;
		String jobType=null;
		String jilLine=null;
		while((jilLine=jilBuffer.readLine())!=null)
		{
			if(jilLine.contains("insert_job:"))
			{
				String[] jilLineTuple=jilLine.split(":");
				jobName=jilLineTuple[1].trim().split(" ")[0].trim();
				jobType=jilLineTuple[2].trim();
				logger.debug("JobName: "+jobName);
			}
			if(jilLine.contains("condition:") && (jobType.equals("c")))
			{
				
				if(jilLine.contains("F(") || jilLine.contains("T(") ||  jilLine.contains("N(") ||  jilLine.contains("E(")||  jilLine.contains("D(")||  jilLine.contains("f(")||  jilLine.contains("t(")||  jilLine.contains("n(")||  jilLine.contains("e(")||  jilLine.contains("d(")|| jilLine.contains("FAILURE(")|| jilLine.contains("failure(")|| jilLine.contains("TERMINATED(")|| jilLine.contains("terminated(")|| jilLine.contains("NOTRUNNING(")|| jilLine.contains("notrunning(")|| jilLine.contains("EXITCODE(")|| jilLine.contains("exitcode(")|| jilLine.contains("DONE(")|| jilLine.contains("done("))
				{
					logger.debug("Bad job");
					if(!badJobList.containsKey(instanceName))
					{
						List<String> badList = new ArrayList<String>();
						badList.add(jobName);
						badJobList.put(instanceName, badList);
					}
					else
					{
						List<String> badList = new ArrayList<String>();
						badList = badJobList.get(instanceName);
						badList.add(jobName);
						badJobList.put(instanceName, badList);
					}
				}
			}
		}
		
	}
}
