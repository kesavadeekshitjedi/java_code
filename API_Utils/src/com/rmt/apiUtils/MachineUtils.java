package com.rmt.apiUtils;

import java.util.ArrayList;
import java.util.List;

import com.ca.autosys.services.AsApi;
import com.ca.autosys.services.request.cat2.GetObjectNamesReq;
import com.ca.autosys.services.response.ApiResponseSet;
import com.ca.autosys.services.response.GetObjectNamesRsp;

public class MachineUtils 
{
	public List<String> getMachines(AsApi appServer)
	{
		List<String> machineList = new ArrayList<String>();
		GetObjectNamesReq req = new GetObjectNamesReq();
		req.setRequest("%", GetObjectNamesReq.MACHINE);
		try
		{
			ApiResponseSet rspSet = (ApiResponseSet) req.execute(appServer);
			while(rspSet.hasNext())
			{
				GetObjectNamesRsp rsp = (GetObjectNamesRsp)rspSet.next();
				System.out.println(rsp.getName());
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
		return machineList;
	}

}
