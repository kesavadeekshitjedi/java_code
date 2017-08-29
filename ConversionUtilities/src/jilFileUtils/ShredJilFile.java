package jilFileUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

import org.apache.log4j.Logger;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import com.ca.autosys.services.AsApi;
import com.ca.autosys.services.AsConstants;
import com.ca.autosys.services.request.cat2.GetJobsWithFilterReq;
import com.ca.autosys.services.request.filter.IJobFilterString;
import com.ca.autosys.services.request.filter.JobFilterString;
import com.ca.autosys.services.response.ApiResponseSet;
import com.ca.autosys.services.response.GetJobsWithFilterRsp;
import com.ca.autosys.services.response.IFilterRsp;

import excelUtils.ExcelReader;
import mainUtils.JilUtilMain;
import workerUtilities.WorkerUtils;

public class ShredJilFile 
{
	static Logger logger = Logger.getRootLogger();
	public static WorkerUtils wUtils;
	public static ExcelReader excelUtils;
	Map<String, List<String>> conditionMap = new HashMap<String, List<String>>(); // Stores CMD and FT Conditions
	Map<String, List<String>> bSuccessMap = new HashMap<String, List<String>>(); // Stores box success
	Map<String, List<String>> bFailureMap = new HashMap<String, List<String>>(); // Stores box failures
	Map<String, List<String>> bConditionMap = new HashMap<String, List<String>>(); // Stores box conditions
	
	List<String> jobConditionList=new ArrayList<String>();
	List<String> boxSuccessList=new ArrayList<String>();
	List<String> boxFailureList=new ArrayList<String>();
	List<String> jobList = new ArrayList<String>();
	List<String> boxList = new ArrayList<String>();
	
	FileWriter updateFileWriter;
	BufferedWriter updateBuffer;
	FileWriter badConditionFileWriter;
	BufferedWriter badConditionBuffer;
	public static void ShredJilFile()
	{
		wUtils=new WorkerUtils();
		excelUtils=new ExcelReader();
		
	}
	
	public void getConditionsOnJob(String jilFile) throws IOException, EncryptedDocumentException, InvalidFormatException
	{
		
		Path p = Paths.get(jilFile);
		Path folderPath = p.getParent();
		System.out.println("File is at this location: "+folderPath);
		String extractFolder=folderPath+"\\"+JilUtilMain.myTime;
		System.out.println("Creating new folder: "+extractFolder);
		File myextractFolder=new File(extractFolder);
		myextractFolder.mkdir();
		String updateJobJil=extractFolder+"\\JobConditionUpdates.jil";
		updateFileWriter=new FileWriter(updateJobJil,true);
		updateBuffer=new BufferedWriter(updateFileWriter);
		badConditionFileWriter=new FileWriter(extractFolder+"\\MissingConditions.txt",true);
		badConditionBuffer = new BufferedWriter(badConditionFileWriter);
		ExcelReader ex = new ExcelReader();
		logger=Logger.getLogger("JilUtilities.ShredJilFile.getConditionsOnJob");
		FileReader jilFileReader=new FileReader(jilFile);
		BufferedReader jilBuffer = new BufferedReader(jilFileReader);
		String currentJilLine="";
		String jilLine="";
		boolean jobFound=false;
		String jobName = "",jobType = "";
		String box_success;
		String box_failure;
		String jobCondition;
		while((currentJilLine=jilBuffer.readLine())!=null)
		{
			jobConditionList = new ArrayList<String>();
			jilLine=currentJilLine.trim();
			if(jilLine.contains("insert_job"))
			{
				logger.info("Job Line found..");
				jobName=jilLine.substring("insert_job:".length(),jilLine.indexOf("job_type:")).trim();
				logger.info("Job Name: "+jobName.trim());
				jobType=jilLine.substring(jilLine.indexOf("job_type:")).trim();
				logger.info("Job Type: "+jobType);
				jobFound=true;
			}
			if((jilLine.contains("condition:") && jobFound==true) && (jobType.equalsIgnoreCase("job_type: CMD") || (jobType.equalsIgnoreCase("job_type: FT"))))
			{
				if(!jobList.contains(jobName))
				{
					jobList.add(jobName);
				}
				logger.info("Found condition");
				String conditionJobs = jilLine.substring("condition:".length()).trim();
				String[] condJobTuple=conditionJobs.split("&");
				for(int j=0;j<condJobTuple.length;j++)
				{
					jobCondition=condJobTuple[j].trim();
					logger.debug("Current Condition to check: "+jobCondition);
					if(!conditionMap.containsKey(jobName))
					{
						if(!jobCondition.contains("d68.am"))
						{
							logger.info("Job needs a cross reference update:"+jobCondition);
							String tempJobName=jobCondition.replace("s(", "");
							String tempJobName2=tempJobName.replace(",24.00)", "");
							logger.debug("Checking in the cross ref file for: "+tempJobName2);
							String crossName=ex.getCrossReferencedJPMName("C:\\JMOFiles\\T4_CrossRef.xlsx", "Sheet1", tempJobName2);
							if(crossName=="")
							{
								badConditionBuffer.write("JOB: "+jobName+" Condition: "+jobCondition+" does not exist in the cross reference file \n");
							}
							else
							{
							logger.info("Cross Reference Name identified: "+crossName);
							jobCondition="s("+crossName+")";
							}
						}
						jobConditionList.add(jobCondition);
						conditionMap.put(jobName, jobConditionList);
						
					}
					else
					{
						jobConditionList = new ArrayList<String>();
						if(!jobCondition.contains("d68.am"))
						{
							logger.info("Job needs a cross reference update:"+jobCondition);
							String tempJobName=jobCondition.replace("s(", "");
							String tempJobName2=tempJobName.replace(",24.00)", "");
							logger.debug("Checking in the cross ref file for: "+tempJobName2);
							String crossName=ex.getCrossReferencedJPMName("C:\\JMOFiles\\T4_CrossRef.xlsx", "Sheet1", tempJobName2);
							if(crossName=="")
							{
								badConditionBuffer.write("JOB: "+jobName+" Condition: "+jobCondition+" does not exist in the cross reference file \n");
							}
							else
							{
							logger.info("Cross Reference Name identified: "+crossName);
							jobCondition="s("+crossName+")";
							}
							
						}
						jobConditionList = conditionMap.get(jobName);
						jobConditionList.add(jobCondition);
						conditionMap.put(jobName, jobConditionList);
						
						
					}
					
				}
				String job="";
				for(int j=0;j<jobList.size();j++)
				{
					job=jobList.get(j);
					String tempCondition="";
					List<String> conditionsForJob = conditionMap.get(jobList.get(j));
					for(int k=0;k<conditionsForJob.size();k++)
					{
						tempCondition+=conditionsForJob.get(k)+" & ";
					}
					logger.debug("Condition statement is: "+tempCondition);
					String condString=tempCondition.substring(0, tempCondition.lastIndexOf("&")-1);
					updateBuffer.write("update_job: "+job+"\n");
					updateBuffer.write("condition: "+ condString+"\n");
					updateBuffer.write("\n");
					jobList.remove(job);	
				}
				/*Iterator it = conditionMap.entrySet().iterator();
				while(it.hasNext())
				{
					Map.Entry kvPair = (Map.Entry)it.next();
					String key=(String) kvPair.getKey();
					List<String> vals = (List<String>) kvPair.getValue();
					String cString="";
					for(int c=0;c<vals.size();c++)
					{
						
					
						cString+=vals.get(c)+" & ";
						
						logger.debug("Condition String: "+cString);
						
						
						
					}
					String condString=cString.substring(0, cString.lastIndexOf("&")-1);
					updateBuffer.write("update_job: "+key+"\n");
					updateBuffer.write("condition: "+ condString+"\n");
				}*/
			}
			if(jilLine.contains("condition:") && jobFound==true && jobType.equals("BOX"))
			{
				if(!boxList.contains(jobName))
				{
					boxList.add(jobName);
				}
				String conditionJobs=jilLine.substring("condition:".length()).trim();
				String[] boxCondTuple = conditionJobs.split("&");
			}
			if(jilLine.contains("box_success:") && jobFound==true && jobType.equalsIgnoreCase("job_type: BOX"))
			{
				boxSuccessList = new ArrayList<String>();
				if(!boxList.contains(jobName))
				{
					boxList.add(jobName);
				}
				String conditionJobs=jilLine.substring("box_success:".length()).trim();
				String[] condJobTuple=conditionJobs.split("\\|");
				for(int j=0;j<condJobTuple.length;j++)
				{
					box_success=condJobTuple[j].trim();
					logger.debug(box_success);
					if(!bSuccessMap.containsKey(jobName))
					{
						boxSuccessList.add(box_success);
						bSuccessMap.put(jobName, boxSuccessList);
						if(!box_success.contains("d68.am"))
						{
							logger.info("Job needs a cross reference update:"+box_success);
							String crossName=ex.getCrossReferencedJPMName("C:\\JMOFiles\\T4_CrossRef.csv", "Sheet1", box_success);
							if(crossName=="")
							{
								badConditionBuffer.write("JOB: "+jobName+" Condition: "+box_success+" does not exist in the cross reference file \n");
							}
							else
							{
							logger.info("Cross Reference Name identified: "+crossName);
							box_success="s("+crossName+")";
							}
						}
					}
					else
					{
						if(!box_success.contains("d68.am"))
						{
							logger.info("Job needs a cross reference update:"+box_success);
							String crossName=ex.getCrossReferencedJPMName("C:\\JMOFiles\\T4_CrossRef.csv", "Sheet1", box_success);
							if(crossName=="")
							{
								badConditionBuffer.write("JOB: "+jobName+" Condition: "+box_success+" does not exist in the cross reference file \n");
							}
							else
							{
							logger.info("Cross Reference Name identified: "+crossName);
							box_success="f("+crossName+")";
							logger.info("Adding "+box_success+" to list");
							}
						}
						
						boxSuccessList = bSuccessMap.get(jobName);
						boxSuccessList.add(box_success);
						bSuccessMap.put(jobName, boxSuccessList);
						
						
					}
					
				}
				String job="";
				for(int j=0;j<boxList.size();j++)
				{
					job=boxList.get(j);
					String tempCondition="";
					List<String> conditionsForJob = bSuccessMap.get(job);
					for(int k=0;k<conditionsForJob.size();k++)
					{
						tempCondition+=conditionsForJob.get(k)+" | ";
					}
					logger.debug("Condition statement is: "+tempCondition);
					String condString=tempCondition.substring(0, tempCondition.lastIndexOf("|")-1);
					updateBuffer.write("update_job: "+job+"\n");
					updateBuffer.write("box_success: "+ condString+"\n");
					updateBuffer.write("\n");
					boxList.remove(job);
				}
			}
			if(jilLine.contains("box_failure:") && jobFound==true && jobType.equalsIgnoreCase("job_type: BOX"))
			{
				boxFailureList = new ArrayList<String>();
				if(!boxList.contains(jobName))
				{
					boxList.add(jobName);
				}
				String conditionJobs=jilLine.substring("box_failure:".length()).trim();
				String[] condJobTuple=conditionJobs.split("\\|");
				for(int j=0;j<condJobTuple.length;j++)
				{
					box_failure=condJobTuple[j].trim();
					logger.debug(box_failure);
					if(!bFailureMap.containsKey(jobName))
					{
						if(!box_failure.contains("d68.am"))
						{
							logger.info("Job needs a cross reference update:"+box_failure);
							String crossName=ex.getCrossReferencedJPMName("C:\\JMOFiles\\T4_CrossRef.csv", "Sheet1", box_failure);
							if(crossName=="")
							{
								badConditionBuffer.write("JOB: "+jobName+" Condition: "+box_failure+" does not exist in the cross reference file \n");
							}
							else
							{
							logger.info("Cross Reference Name identified: "+crossName);
							box_failure="f("+crossName+")";
							logger.info("Adding "+box_failure+" to list");
							}
						}
						boxFailureList.add(box_failure);
						bFailureMap.put(jobName, boxFailureList);
						
					}
					else
					{
						if(!box_failure.contains("d68.am"))
						{
							logger.info("Job needs a cross reference update:"+box_failure);
							String crossName=ex.getCrossReferencedJPMName("C:\\JMOFiles\\T4_CrossRef.csv", "Sheet1", box_failure);
							if(crossName=="")
							{
								badConditionBuffer.write("JOB: "+jobName+" Condition: "+box_failure+" does not exist in the cross reference file \n");
							}
							else
							{
							logger.info("Cross Reference Name identified: "+crossName);
							box_failure="f("+crossName+")";
							logger.info("Adding "+box_failure+" to list");
							}
						}
						boxFailureList = new ArrayList<String>();
						boxFailureList = bFailureMap.get(jobName);
						boxFailureList.add(box_failure);
						bFailureMap.put(jobName, boxFailureList);
						
						
					}
					
				}
				String job="";
				for(int j=0;j<boxList.size();j++)
				{
					job=boxList.get(j);
					String tempCondition2="";
					List<String> conditionsForJob = bFailureMap.get(job);
					for(int k=0;k<conditionsForJob.size();k++)
					{
						tempCondition2+=conditionsForJob.get(k)+" | ";
					}
					logger.debug("Condition statement is: "+tempCondition2);
					String condString=tempCondition2.substring(0, tempCondition2.lastIndexOf("|")-1);
					updateBuffer.write("update_job: "+job+"\n");
					updateBuffer.write("box_failure: "+ condString+"\n");
					updateBuffer.write("\n");
					boxList.remove(job);
				}
			}
			
			
		}
		badConditionBuffer.close();
		badConditionFileWriter.close();
		updateBuffer.close();
		updateFileWriter.close();
		jobFound=false;
		logger.info("test");
	}
	public List<String> readJilToGetJobNames(String jilFile) throws IOException
	{
		List<String> jobList=new ArrayList<String>();
		
		Scanner conScanner = new Scanner(System.in);
		
		logger=Logger.getLogger("JilUtilities.ShredJilFile.readJilToGetJobNames");
		
		FileReader jilFileReader;
		String currentLine=null;
		try 
		{
			jilFileReader = new FileReader(jilFile);
			BufferedReader jilFileBuffer = new BufferedReader(jilFileReader);
			while((currentLine=jilFileBuffer.readLine())!=null)
			{
				String jilLine=currentLine.trim();
				if(currentLine.contains("insert_job"))
				{
					logger.debug("Job Definition found");
					String myJobName=jilLine.substring("insert_job:".length(),jilLine.indexOf("job_type:"));
					logger.info("Job Name: "+myJobName.trim());
					String jobType=jilLine.substring(jilLine.indexOf("job_type:"));
					logger.info("Job Type: "+jobType);
				}
			}
		} 
		catch (FileNotFoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return jobList;
		
	}
	public void readJilToUpdateJobNames(String jilFile, String jmoJobsetName) throws IOException
	{
		Map<String, List<String>> jmo2AEMap = new HashMap<String, List<String>>();
		List<String> jobsInJMOJobset = new ArrayList<String>();
		List<String> jobList=new ArrayList<String>();
		String jobName="";
		String jobType="";
		String applicationName="";
		String groupName="";
		
		
		logger=Logger.getLogger("JilUtilities.ShredJilFile.readJilToUpdateJobNames");
		
		FileReader jilFileReader;
		String currentLine=null;
		try 
		{
			jilFileReader = new FileReader(jilFile);
			BufferedReader jilFileBuffer = new BufferedReader(jilFileReader);
			while((currentLine=jilFileBuffer.readLine())!=null)
			{
				String jilLine=currentLine.trim();
				if(currentLine.contains("insert_job"))
				{
					logger.debug("Job Definition found");
					jobName=jilLine.substring("insert_job:".length(),jilLine.indexOf("job_type:"));
					logger.info("Job Name: "+jobName.trim());
					jobType=jilLine.substring(jilLine.indexOf("job_type:"));
					logger.info("Job Type: "+jobType);
					
				}
				if(jilLine.contains("application:"))
				{
					applicationName=jilLine.substring(currentLine.indexOf("application:")+"application:".length());
					logger.info("Application Name: "+applicationName);
					if(applicationName.contains(jmoJobsetName))
					{
						if(!jmo2AEMap.containsKey(jmoJobsetName))
						{
							jobsInJMOJobset.add(jobName.trim());
							jmo2AEMap.put(jmoJobsetName, jobsInJMOJobset);
						}
						else
						{
							jobsInJMOJobset=jmo2AEMap.get(jmoJobsetName);
							jobsInJMOJobset.add(jobName);
							jmo2AEMap.put(jmoJobsetName, jobsInJMOJobset);
						}
					}
					
				}
				
			}
			logger.info("Reading jil file complete");
			String updateJobsFileName="C:\\JMOFiles\\TopBox\\"+JilUtilMain.myTime+"\\JobUpdates.jil";
			Iterator mapIterator = jmo2AEMap.entrySet().iterator();
			FileWriter myFile = null;
			BufferedWriter myBuffer = null;
			while(mapIterator.hasNext())
			{
				String myTopBox="";
				Map.Entry jobsetJobPair =(Map.Entry) mapIterator.next();
				String jmoJobset = (String) jobsetJobPair.getKey();
				List<String> jobs = jmo2AEMap.get(jmoJobset);
				// This loop here gets the key (TopBoxName) for the value (jobset)
				for(int i=0;i<jobs.size();i++)
				{
					/*Set<T> keys = new HashSet<T>();
					for(Entry<String, String> entry: excelUtils.jmo2AEJobsetMap.entrySet())
					{
						if(Objects.equals(jobs.get(i), entry.getValue()))
						{
							myTopBox=entry.getKey();
						}
					}*/
					myTopBox=excelUtils.jmo2AEJobsetMap.get(jmoJobset);
					myFile=new FileWriter(updateJobsFileName,true);
					myBuffer = new BufferedWriter(myFile);
					
					
					myBuffer.write("update_job: "+jobs.get(i)+"\n");
					myBuffer.write( "box_name: "+myTopBox+"\n");
					myBuffer.write( "date_conditions:0"+"\n");
					myBuffer.write("\n");
					myBuffer.close();
					myFile.close();
				}
			}
			
		} 
		catch (FileNotFoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("Done reading jil file completely");
		//return jobList;
		
	}
	public void writeToFile(String fileName, String content) throws IOException
	{
		FileWriter myFile=null;
		BufferedWriter fileWriter=null;
		try
		{
		myFile = new FileWriter(fileName,true);
		fileWriter = new BufferedWriter(myFile);
		fileWriter.write(content);
		}
		catch(FileNotFoundException fe)
		{
			fe.printStackTrace();
		}
		finally
		{
			fileWriter.close();
			myFile.close();
		}
	}
	public Map<String,List<String>> putFileTriggersIntoBox(String propertiesFile) throws FileNotFoundException, IOException
	{
		Map<String, List<String>> calendarJobMap = new LinkedHashMap<String, List<String>>(); 
		
		logger=Logger.getLogger("JilUtilities.ShredJilFile.putFileTriggersIntoBox_API");
		
		logger.info("Getting the list of File Triggers from the AutoSys instance specified in the properties file");
		Properties props = new Properties();
		props.load(new FileInputStream(propertiesFile));
		String asServerHost=props.getProperty("AS_SERVERHOST");
		int asServerPort=Integer.parseInt(props.getProperty("AS_SERVERPORT"));
		String asAgentHost=props.getProperty("AS_AGENTHOST");
		AsApi appServer = new AsApi(asServerHost,asServerPort,AsConstants.ENCRYPTION_TYPE_DEFAULT);
		if(appServer.authenticateWithPassword("daddepalli", asAgentHost, "Deek5581")==true)
		{
			GetJobsWithFilterReq getFTJobsReq = new GetJobsWithFilterReq();
			JobFilterString ftFilter = new JobFilterString(IJobFilterString.FLT_JOB_NAME,"%.ft.%");
			int[] jobAttributes = { GetJobsWithFilterReq.ATTR_JOB_NAME, GetJobsWithFilterReq.ATTR_RUN_CALENDAR, GetJobsWithFilterReq.ATTR_DAYS_OF_WEEK_STRING};
			
			getFTJobsReq.setRequest(ftFilter, jobAttributes);
			try
			{
				List<String> myjobList = new ArrayList<String>();
				List<String> myjobLis2t = new ArrayList<String>();
				ApiResponseSet ftResp = (ApiResponseSet)getFTJobsReq.execute(appServer);
				while(ftResp.hasNext())
				{
					GetJobsWithFilterRsp getFTJobsRsp = (GetJobsWithFilterRsp) ftResp.next();
					int[] ftRspAttributes = getFTJobsRsp.getAttributes();
					String name,value;
					 String jobName="";
                	 String jobCalendar="";
                	 String jobDays="";
					for(int i=0;i<ftRspAttributes.length;i++)
					{
						
						int type = getFTJobsRsp.getAttributeType(ftRspAttributes[i]);
		                 switch(type) 
		                 {
		                     
		                     case IFilterRsp.TYPE_STRING:
		                    	
		                         name = getFTJobsRsp.getAttributeName(ftRspAttributes[i]);
		                         value = name + " = " + getFTJobsRsp.getString(ftRspAttributes[i]);
		                         //System.out.println(value);
		                         logger.info(value);
		                         if(value.contains("job_name"))
		                         {
		                        	 if(!myjobLis2t.contains(getFTJobsRsp.getString(ftRspAttributes[i])))
		                        	 {
		                        		 if((!getFTJobsRsp.getString(ftRspAttributes[i]).equals(null)))
		                        		 {
			                        		 jobName=getFTJobsRsp.getString(ftRspAttributes[i]);
			                        		 if(jobName.equals("xaminsb.ft.TXT"))
			                        		 {
			                        			 logger.info("days of week job");
			                        		 }
			                        		 myjobLis2t.add(getFTJobsRsp.getString(ftRspAttributes[i]));
		                        		 }
		                        	 }
		                        	 
		                         }
		                         if(value.contains("run_calendar") || (value.contains("days_of_week")))
		                         {
		                        	 if(value.contains("days_of_week"))
		                        	 {
		                        		 logger.info("Days of week");
		                        	 }
		                        	 if((!getFTJobsRsp.getString(ftRspAttributes[i]).equals("")))
	                        		 {
			                        	 String calName=getFTJobsRsp.getString(ftRspAttributes[i]);
			                        	 if(calendarJobMap.containsKey(calName))
			                        	 {
			                        		 myjobList=new ArrayList<String>();
				                        	 myjobList=calendarJobMap.get(calName);
				                        	 logger.info("Adding job: "+jobName);
				                        	 myjobList.add(jobName);
				                        	 calendarJobMap.put(calName, myjobList);
			                        	 }
			                        	 else
			                        	 {
			                        		 logger.info("Adding job: "+jobName);
				                        	 myjobList.add(jobName);
			                        		 calendarJobMap.put(calName, myjobList);
			                        	 }
	                        		 }
		                         }
		                         break;
		                                    
		                 }
					}
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		logger.debug(calendarJobMap);
		logger.info("Done");
		return calendarJobMap;
	}
	public List<String> getResourceNamesForJob(String jilInput)
	{
		List<String> resourceNames=new ArrayList<String>();
		Map<String, List<String>> resourceJobMap = new LinkedHashMap<String, List<String>>(); 
		
		
		return resourceNames;
	}
	

}
