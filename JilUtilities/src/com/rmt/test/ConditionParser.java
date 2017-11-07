package com.rmt.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ConditionParser {

	public static void main(String[] args) 
	{
		Map<String, String> jobNameMap = new HashMap<String,String>();
		jobNameMap.put("ps_matching_GRP1_fs9_tst", "ps_matching_GRP1_fs9_tst-PS");
		jobNameMap.put("ps_matching_GRP2_fs9_tst", "ps_matching_GRP2_fs9_tst-PS");
		jobNameMap.put("ps_matching_GRP3_fs9_tst", "ps_matching_GRP3_fs9_tst-PS");
		jobNameMap.put("ps_matching_USA_fs9_tst", "ps_matching_USA_fs9_tst-PS");
		jobNameMap.put("job1", "job1-PS");
		jobNameMap.put("job2", "job2-PS");
		jobNameMap.put("job3", "job3-PS");
		jobNameMap.put("job4", "job4-PS");
		
		String conditionString="condition: s(ps_matching_GRP1_fs9_tst) & s(ps_matching_GRP2_fs9_tst) & s(ps_matching_GRP3_fs9_tst) & s(ps_matching_USA_fs9_tst)";
		String conditionString2 = "condition:(s(job1) & s(job2)) | (s(job3) and f(job4))";
		
		
		List<String> conditionList1=new ArrayList<String>();
		List<String> conditionList2 = new ArrayList<String>();
		
		
		String mod1 = conditionString.replace("condition: ", "");
		String mod2 = conditionString2.replace("condition:", "");
		System.out.println(mod1+" \n"+mod2+"\n");
		String[] condT = mod1.split(" ");
		String condString=null;
		String condOperator=null;
		for(int i=0;i<condT.length;i++)
		{
			if((!condT[i].contains("&")) || (!condT[i].contains("|")))
			{
				condString=condT[i].trim();
			}
			//System.out.println(condString);
			Iterator it = jobNameMap.entrySet().iterator();
			String key,val;
			while(it.hasNext())
			{
				Map.Entry pair = (Map.Entry)it.next();
				key = (String) pair.getKey();
				val = (String) pair.getValue();
				if(condString.contains(key))
				{
					String modString=condString.replace(key, val);
					System.out.println(modString);
					continue;
				}
			}
		}

	}

}
