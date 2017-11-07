package com.rmt.autosys.model;

import java.util.ArrayList;
import java.util.List;

import com.ca.autosys.services.AsApi;
import com.ca.autosys.services.AsConstants;
import com.ca.autosys.services.AsException;
import com.ca.autosys.services.request.cat2.GetJobsWithFilterReq;
import com.ca.autosys.services.request.filter.IJobFilterString;
import com.ca.autosys.services.request.filter.JobFilterString;
import com.ca.autosys.services.response.ApiResponseSet;
import com.ca.autosys.services.response.GetJobsWithFilterRsp;
import com.ca.autosys.services.response.IFilterRsp;
import com.rmt.autosys.dao.AEJobInfo;
public class RetrieveJobsModel 
{
	
	public List<AEJobInfo> getJobNames()
	{
		AsApi api = new AsApi("rhel-ae-dev",9000,AsConstants.ENCRYPTION_TYPE_DEFAULT);
		List<AEJobInfo> aeJobNameList= new ArrayList<>();
		if(api.authenticateWithPassword("daddepalli", "rhel-ae-dev.rmt.com", "Deek5581")==true)
		{
			int jobType = 0;
			String jobName="";
			System.out.println("in the getJobNames methods");
			
			GetJobsWithFilterReq getRequest = new GetJobsWithFilterReq();
			
			JobFilterString filter = new JobFilterString(IJobFilterString.ATTR_JOB_NAME,"%");
			int[] attributes = {
					GetJobsWithFilterReq.ATTR_JOB_NAME,
					GetJobsWithFilterReq.ATTR_JOB_TYPE
			};
			
			getRequest.setRequest(filter, attributes);
			try
			{
				ApiResponseSet rspSet = (ApiResponseSet) getRequest.execute(api);
				while(rspSet.hasNext())
				{
					 GetJobsWithFilterRsp rsp = (GetJobsWithFilterRsp)rspSet.next();
		             int[] rspAttributes = rsp.getAttributes();
		             String name, output;
		             
		             for(int i=0; i<rspAttributes.length; i++) 
		             {
		                 int type = rsp.getAttributeType(rspAttributes[i]);
		                 switch(type) 
		                 {
		                     case IFilterRsp.TYPE_INT:
		                         name = rsp.getAttributeName(rspAttributes[i]);
		                         output = name + " = " + rsp.getInt(rspAttributes[i]);
		                         jobType=rsp.getInt(rspAttributes[i]);
		                         System.out.println(output);
		                         break;
		                     case IFilterRsp.TYPE_STRING:
		                         name = rsp.getAttributeName(rspAttributes[i]);
		                         output = name + " = " + rsp.getString(rspAttributes[i]);
		                         jobName=rsp.getString(rspAttributes[i]);
		                         System.out.println(output);
		                         //Student myTempStudent = new Student(firstName,lastName,emailAddress);
		                         
		                         //aeJobNameList.add(rsp.getString(rspAttributes[i]));
		                         break;
		                                       
		                 }
		                 //Student myTempStudent = new Student(firstName,lastName,emailAddress);
		                 
		                 AEJobInfo myTempJobInfo = new AEJobInfo(jobName, jobType);
		                 aeJobNameList.add(myTempJobInfo);
		             }
	
				}
			}
		
		catch(AsException ae)
		{
			ae.printStackTrace();
		}
		}
		return aeJobNameList;
		
	}

}
