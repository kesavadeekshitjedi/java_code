package com.rmt.main;

import java.util.Iterator;
import java.util.List;

import com.ca.autosys.services.AsApi;
import com.ca.autosys.services.AsConstants;
import com.rmt.apiUtils.MachineUtils;
import com.rmt.apiUtils.ResourceUtils;

public class APIUtilMain {

	public static void main(String[] args) 
	{
		AsApi appServer = new AsApi("LUMOS",9000,AsConstants.ENCRYPTION_TYPE_DEFAULT);
		ResourceUtils resUtils = new ResourceUtils();
		List<String> resList=resUtils.getResourceInfo(appServer);
		// TODO Auto-generated method stub
		//System.out.println(resList);
		Iterator resIterator = resList.iterator();
		while(resIterator.hasNext())
		{
			String resObject=(String)resIterator.next();
			String[] resObjectTuple=resObject.split(":");
			System.out.println("delete_resource: "+resObjectTuple[0]);
			
			if((resObjectTuple.length==2 ))
			{
				if(resObjectTuple[1]!=null || (resObjectTuple[1]!=""))
				{
					System.out.println("machine: "+resObjectTuple[1]);
				}
			}
		}
		MachineUtils mUtils = new MachineUtils();
		List<String> myMachines=mUtils.getMachines(appServer);
		System.out.println(myMachines);
	}

}
