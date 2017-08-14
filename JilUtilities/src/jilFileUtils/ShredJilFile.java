package jilFileUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

import org.apache.log4j.Logger;

import com.ca.autosys.services.AsApi;
import com.ca.autosys.services.AsConstants;
import com.ca.autosys.services.common.Tools;
import com.ca.autosys.services.request.cat2.GetJobsWithFilterReq;
import com.ca.autosys.services.request.filter.IJobFilterString;
import com.ca.autosys.services.request.filter.JobFilterString;
import com.ca.autosys.services.response.ApiResponseSet;
import com.ca.autosys.services.response.GetJobsWithFilterRsp;
import com.ca.autosys.services.response.IFilterRsp;

public class ShredJilFile 
{
	static Logger logger = Logger.getRootLogger();
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
