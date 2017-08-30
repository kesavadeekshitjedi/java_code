package aeUtilities;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

import org.apache.log4j.Logger;

import com.ca.autosys.services.AsApi;
import com.ca.autosys.services.AsConstants;
import com.ca.autosys.services.AsException;
import com.ca.autosys.services.request.cat1.PingApiReq;
import com.ca.autosys.services.request.cat2.GetJobsWithFilterReq;
import com.ca.autosys.services.request.filter.IJobFilterString;
import com.ca.autosys.services.request.filter.JobFilterString;
import com.ca.autosys.services.response.ApiResponseSet;
import com.ca.autosys.services.response.GetJobsWithFilterRsp;
import com.ca.autosys.services.response.IFilterRsp;
import com.ca.autosys.services.response.PingApiRsp;

public class AEAPIUtils 
{
	static Logger logger = Logger.getRootLogger();
	Map<Integer,String> statusMap=new HashMap<Integer,String>();
	public List<String> getJobStatus(String propertiesFile) throws FileNotFoundException, IOException
	{
		
		List<String> jobStatusList=new ArrayList<String>();
		logger=Logger.getLogger("JilUtilities.AEAPIUtils.getJobStatuses");
		Properties props = new Properties();
		props.load(new FileInputStream(propertiesFile));
		String asServerHost=props.getProperty("AS_SERVERHOST");
		int asServerPort=Integer.parseInt(props.getProperty("AS_SERVERPORT"));
		String asAgentHost=props.getProperty("AS_AGENTHOST");
		String asEncType=props.getProperty("AS_ENCRYPTION_TYPE");
		Scanner encScanner = new Scanner(System.in);
		AsApi appServer=null;
		
		if(asEncType.equalsIgnoreCase("DEFAULT"))
		{
			appServer = new AsApi(asServerHost,asServerPort,AsConstants.ENCRYPTION_TYPE_DEFAULT); // Just assuming default encrption for now. Change later.
		}
		else if(asEncType.equalsIgnoreCase("CUSTOM") || asEncType.equalsIgnoreCase("AES"))
		{
			System.out.println("Enter the custom encryption key");
			String encKey=encScanner.nextLine();
			appServer = new AsApi(asServerHost,asServerPort,AsConstants.ENCRYPTION_TYPE_AES,encKey);
		}
		PingApiReq req = new PingApiReq();
		PingApiRsp rsp = null;
		 
	     try 
	     {
	         rsp = (PingApiRsp)req.execute(appServer);
	         logger.debug("Server version: " + rsp.getServerVersion());
	     } catch (AsException e) 
	     {
	         System.out.println("AsException: " + e.getMessage());
	     }
	     System.out.println("Enter the username to authenticate");
	     String username=encScanner.nextLine();
	     System.out.println("Enter the password for the user ("+username+") :");
	     String password = encScanner.nextLine();
	     if(appServer.authenticateWithPassword(username, asAgentHost, password)==true)
		 {
	    	 GetJobsWithFilterReq getFTJobsReq = new GetJobsWithFilterReq();
	    	 JobFilterString ftFilter = new JobFilterString(IJobFilterString.FLT_JOB_NAME,"%");
	    	 int[] jobAttributes = { GetJobsWithFilterReq.ATTR_JOB_NAME,GetJobsWithFilterReq.ATTR_STATUS};
	    	 getFTJobsReq.setRequest(ftFilter, jobAttributes);
				try
				{
					
					ApiResponseSet ftResp = (ApiResponseSet)getFTJobsReq.execute(appServer);
					while(ftResp.hasNext())
					{
						GetJobsWithFilterRsp getFTJobsRsp = (GetJobsWithFilterRsp) ftResp.next();
						int[] ftRspAttributes = getFTJobsRsp.getAttributes();
						String name;
						int value;
						String jobName="";
		            	int jobType=0;
		            	for(int i=0; i<ftRspAttributes.length; i++) 
		            	{
		            		String jobStatus="";
		                    int type = getFTJobsRsp.getAttributeType(ftRspAttributes[i]);
		                    switch(type) 
		                    {
		                        case IFilterRsp.TYPE_INT:
		                            name = getFTJobsRsp.getAttributeName(ftRspAttributes[i]);
		                            value = getFTJobsRsp.getInt(ftRspAttributes[i]);
		                            jobStatus=jobName+">"+value;
		                            if(!jobStatusList.contains(jobStatus))
		                            {
		                            	jobStatusList.add(jobStatus);
		                            }
		                            //System.out.println(value);
		                            break;
		                        case IFilterRsp.TYPE_STRING:
		                            name = getFTJobsRsp.getAttributeName(ftRspAttributes[i]);
		                            String value2 = name + " = " + getFTJobsRsp.getString(ftRspAttributes[i]);
		                            //System.out.println(value);
		                            jobName=getFTJobsRsp.getString(ftRspAttributes[i]);
		                            logger.info("Job Name is : "+getFTJobsRsp.getString(ftRspAttributes[i]));
		                            
		                            break;
		                    }
		            	}
		                            
	                	 
					}
				}
				catch(AsException ae)
				{
					ae.printStackTrace();
				}
		 }
	     
		return jobStatusList;
	}
	public List<String> getJobList(String propertiesFile, String job_type) throws FileNotFoundException, IOException
	{
		Properties props = new Properties();
		props.load(new FileInputStream(propertiesFile));
		String asServerHost=props.getProperty("AS_SERVERHOST");
		int asServerPort=Integer.parseInt(props.getProperty("AS_SERVERPORT"));
		String asAgentHost=props.getProperty("AS_AGENTHOST");
		AsApi appServer = new AsApi(asServerHost,asServerPort,AsConstants.ENCRYPTION_TYPE_DEFAULT); // Just assuming default encrption for now. Change later.
		
		logger=Logger.getLogger("JilUtilities.AEAPIUtils.getJobList");
		logger.info("Retreiving "+job_type+" jobs");
		List<String> fileTriggerList=new ArrayList<String>();
		Scanner conScanner = new Scanner(System.in);
		System.out.println("Enter the username to authenticate");
		String username=conScanner.nextLine();
		System.out.println("Enter the password for the user ("+username+") :");
		String password = conScanner.nextLine();
		if(appServer.authenticateWithPassword(username, asAgentHost, password)==true)
		{
			GetJobsWithFilterReq getFTJobsReq = new GetJobsWithFilterReq();
			JobFilterString ftFilter = new JobFilterString(IJobFilterString.FLT_JOB_NAME,"%");
			int[] jobAttributes = { GetJobsWithFilterReq.ATTR_JOB_NAME,GetJobsWithFilterReq.ATTR_JOB_TYPE};
			getFTJobsReq.setRequest(ftFilter, jobAttributes);
			try
			{
				
				ApiResponseSet ftResp = (ApiResponseSet)getFTJobsReq.execute(appServer);
				while(ftResp.hasNext())
				{
					GetJobsWithFilterRsp getFTJobsRsp = (GetJobsWithFilterRsp) ftResp.next();
					int[] ftRspAttributes = getFTJobsRsp.getAttributes();
					String name,value;
					String jobName="";
	            	int jobType=0;
	            	for(int i=0; i<ftRspAttributes.length; i++) 
	            	{
	                    int type = getFTJobsRsp.getAttributeType(ftRspAttributes[i]);
	                    switch(type) 
	                    {
	                        case IFilterRsp.TYPE_INT:
	                            name = getFTJobsRsp.getAttributeName(ftRspAttributes[i]);
	                            value = name + " = " + getFTJobsRsp.getInt(ftRspAttributes[i]);
	                            if(getFTJobsRsp.getInt(ftRspAttributes[i])==255)
	                            {
	                            	logger.info("Found File Trigger job. Adding to list");
	                            	fileTriggerList.add(jobName);
	                            }
	                            //System.out.println(value);
	                            break;
	                        case IFilterRsp.TYPE_STRING:
	                            name = getFTJobsRsp.getAttributeName(ftRspAttributes[i]);
	                            value = name + " = " + getFTJobsRsp.getString(ftRspAttributes[i]);
	                            //System.out.println(value);
	                            jobName=getFTJobsRsp.getString(ftRspAttributes[i]);
	                            logger.info("Job Name is : "+getFTJobsRsp.getString(ftRspAttributes[i]));
	                            break;
	                    }
	            	}
	                            
                	 
				}
			}
			catch(AsException ae)
			{
				ae.printStackTrace();
			}
		}
		return fileTriggerList;
		
	}

}
