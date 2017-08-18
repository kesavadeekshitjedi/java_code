package jilFileUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.poi.ss.formula.functions.T;

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
	public static void ShredJilFile()
	{
		wUtils=new WorkerUtils();
		excelUtils=new ExcelReader();
		
	}
	public List<String> readJilToGetJobNames(String jilFile) throws IOException
	{
		List<String> jobList=new ArrayList<String>();
		
		Scanner conScanner = new Scanner(System.in);
		
		logger=Logger.getLogger("JilUtilities.ShredJilFile.readJilToGetJobNames");
		logger.info("Helo!");
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
