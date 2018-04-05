package com.rmt.apiUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ca.autosys.services.AsApi;
import com.ca.autosys.services.request.cat2.GetResourcesWithFilterReq;
import com.ca.autosys.services.request.filter.IResourceFilterString;
import com.ca.autosys.services.request.filter.ResourceFilterString;
import com.ca.autosys.services.response.ApiResponseSet;
import com.ca.autosys.services.response.GetResourcesWithFilterRsp;
import com.ca.autosys.services.response.IFilterRsp;

public class ResourceUtils 
{
	public Map<String, List<String>> getResourceDefinitions(AsApi appServer, String resourceFilter)
	{
		String resName, resType,resMachine;
		int resAmount;
		Map<String, List<String>> machineDefMap = new HashMap<String, List<String>>();
		
		
		return machineDefMap;
	}
	public List<String> getResourceInfo(AsApi appServer)
	{
		String name,machine,output;
		String resourceName=null;
		String resourceMachine=null;
		List<String> resList=new ArrayList<String>();
		try
		{
			GetResourcesWithFilterReq resReq = new GetResourcesWithFilterReq();
			ResourceFilterString resFilter = new ResourceFilterString(IResourceFilterString.FLT_VIRTRES_NAME,"%");
			
			
			int[] resourceAttributes = {
					GetResourcesWithFilterReq.ATTR_VIRTRES_NAME,
					GetResourcesWithFilterReq.ATTR_VIRTRES_MACH
					};
			resReq.setRequest(resFilter, resourceAttributes);
			ApiResponseSet rspSet = (ApiResponseSet)resReq.execute(appServer);
			while(rspSet.hasNext())
			{
				GetResourcesWithFilterRsp resRsp = (GetResourcesWithFilterRsp)rspSet.next();
				int[] rspAttributes = resRsp.getAttributes();
				
				for(int i=0;i<rspAttributes.length;i++)
				{
					int type=resRsp.getAttributeType(rspAttributes[i]);
					switch(type)
					{
					case IFilterRsp.TYPE_STRING:
						name = resRsp.getAttributeName(rspAttributes[i]);
                        output = name + " = " + resRsp.getString(rspAttributes[i]);
                        System.out.println(output);
                        if(output.contains("resname"))
                        {
                        	resourceName=output.split("=")[1].trim();
                        }
                        else if(output.contains("machine"))
                        {
                        	resourceMachine=output.split("=")[1].trim();
                        	if(resourceMachine.equals(null) || resourceMachine.equals(""))
                        	{
                        		resourceMachine="";
                        	}
                        	resList.add(resourceName+":"+resourceMachine);
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
		return resList;
					
	}

}
