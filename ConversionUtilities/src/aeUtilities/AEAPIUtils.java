package aeUtilities;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import org.apache.log4j.Logger;

import com.ca.autosys.services.AsApi;
import com.ca.autosys.services.AsConstants;
import com.ca.autosys.services.AsException;
import com.ca.autosys.services.request.cat2.GetJobsWithFilterReq;
import com.ca.autosys.services.request.filter.IJobFilterString;
import com.ca.autosys.services.request.filter.JobFilterString;
import com.ca.autosys.services.response.ApiResponseSet;
import com.ca.autosys.services.response.GetJobsWithFilterRsp;
import com.ca.autosys.services.response.IFilterRsp;

public class AEAPIUtils 
{
	static Logger logger = Logger.getRootLogger();
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
