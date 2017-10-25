package com.rmt.utilities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class JilFileUtils 
{
	static Logger logger = Logger.getRootLogger();
	
	static String inputJil1;
	static String inputJil2;
	static String differenceJil;
	static String differenceReport;
	
	static String file1JobName="";
	static String file1JobType="";
	
	static String file2JobName="";
	static String file2JobType="";
	
	FileReader inputFileReader;
	BufferedReader inputFileBuffer;
	
	FileReader sec_inputFileReader;
	BufferedReader sec_inputFileBuffer;
	
	FileWriter differenceJilWriter;
	BufferedWriter differenceJilBuffer;
	
	FileWriter differenceReportWriter;
	BufferedWriter differenceReportBuffer;
	
	
	
	public void getDifferences(Map<String, List<String>> jobMap1, Map<String, List<String>> jobMap2) throws IOException
	{
		differenceJilWriter=new FileWriter("RightUpdate.jil");
		differenceJilBuffer = new BufferedWriter(differenceJilWriter);
		// read job map1 and for each key do the following
		/*
		 * 1. Check if the key (job) also exists in jobMap2
		 * If key exists, compare the rest of the definition.
		 * If full definition matches between jobMap1 and jobMap2, do nothing
		 * if definition changes from jobMap1 to jobMap2,
		 * 	> add the update_job statement
		 *  > write the original attribute value from jobMap1 with a #
		 *  > write the new attribute value from jobMap2 without a #
		 * else
		 * if Key does not exist
		 *  > add a delete_job statement
		 *  > add the original job definition from jobMap1 with a #
		 *  > prepend the commented job definition with /* ---- original job definition <jobname> ---\*\/
		 
		 */
		
		Iterator it1 = jobMap1.entrySet().iterator();
		Iterator it2 = jobMap2.entrySet().iterator();
		
		
		while(it1.hasNext())
		{
			Map.Entry pair = (Map.Entry)it1.next();
			String jobKey1 = (String) pair.getKey();
			String jobKey2=null;
			List<String> jobKeyAttributes1 = (List<String>) pair.getValue();
			List<String> jobKeyAttributes2=null;
			List<String> differenceList=new ArrayList<String>();
			if(jobMap2.containsKey(jobKey1))
			{
				logger.info("checking for Job: "+jobKey1);
				while(it2.hasNext())
				{
					Map.Entry pair2 = (Map.Entry) it2.next();
					jobKey2 = (String) pair2.getKey();
					jobKeyAttributes2 = (List<String>) pair2.getValue();
					logger.info(jobKey2);
					if(jobKey1.equalsIgnoreCase(jobKey2))
					{
						logger.info("Job names are equal. may not be same case. checking case...");
						if(jobKey1.equals(jobKey2))
						{
							logger.debug("equal and same case. Check attribute differences");
							if(jobKeyAttributes1.containsAll(jobKeyAttributes2) && jobKeyAttributes1.size()==jobKeyAttributes2.size())
							{
								logger.info("Job Definition exact match. Nothing to do");
								break;
							}
							else
							{
								// attributes are different
								logger.info("definition delta detected in job: "+jobKey1);
								jobKeyAttributes2.removeAll(jobKeyAttributes1);
								System.out.println(jobKeyAttributes2);
								System.out.println("update_job: "+jobKey1);
								differenceJilBuffer.write("/* ----- Difference found in job: "+jobKey1+" --------- */ \n");
								differenceJilBuffer.write("update_job: "+jobKey1+" \n");
								for(int attrKey=0;attrKey<jobKeyAttributes2.size();attrKey++)
								{
									System.out.println(jobKeyAttributes2.get(attrKey));
									differenceJilBuffer.write(jobKeyAttributes2.get(attrKey));
									
								}
								differenceJilBuffer.write(" \n");
								break;
							}
							
						}
					}
				}
				
			}
			else
			{
				logger.info(jobKey1+" does not exist. generate delete_job statement here.");
				differenceJilBuffer.write("/* ----- Difference found in job: "+jobKey1+". Job not found in second jil file --------- */ \n");
				differenceJilBuffer.write("delete_job: "+jobKey1+" \n");
				differenceJilBuffer.write(" \n");
				differenceJilBuffer.write("/* ----- ORIGINAL DEFINITION for JOB: "+jobKey1+" ---------*/"+" \n");
				
				differenceJilBuffer.write("#insert_job: "+jobKey1+" \n");
				for(int j=0;j<jobKeyAttributes1.size();j++)
				{
					differenceJilBuffer.write("#"+jobKeyAttributes1.get(j));
				}
				differenceJilBuffer.write(" \n");
			}
			
		}
		differenceJilBuffer.close();
		differenceJilWriter.close();
	}
	
	public Map<String, List<String>> readJobInformation(String jilFile) throws IOException
	{
		Map<String, List<String>> jobMap = new HashMap<String, List<String>>();
		List<String> attributeList=new ArrayList<String>();
		FileReader jilFileReader = new FileReader(jilFile);
		BufferedReader jilFileBuffer = new BufferedReader(jilFileReader);
		String jilFileLine=null;
		String jobName=null;
		String attributeName=null;
		String attributeValue=null;
		String attribute=null;
		String[] lineSplitter;
		boolean foundNewJob=false;
		logger=Logger.getLogger("JilUtilities.utilities.readJobInformation");
		logger.info("attempting to read "+jilFile);
		while((jilFileLine=jilFileBuffer.readLine())!=null)
		{
			
			String currentJilLine=jilFileLine.trim();
			if(!currentJilLine.contains("#") && (!currentJilLine.isEmpty() && (!currentJilLine.contains("/*") && (!currentJilLine.contains("//")))))
			{
				logger.debug(jilFileLine);
				/*
				 * Step 1. Extract the job name from the line
				 * Step 2. Extract job type and all other attributes
				 * Step 3. Store all this information in the HashMap ( <job, "job_type: CMD","command: abc" etc)
				 * 
				 */
				lineSplitter=currentJilLine.split(":");
				if(lineSplitter.length==3 && lineSplitter[0].contains("insert_job"))
				{
					foundNewJob=true;
					jobName=null;
					attributeList=new ArrayList<String>();
					if(jobName!=null &&  foundNewJob==true)
					{
						if(!jobMap.containsKey(jobName))
						{
							jobMap.put(jobName, attributeList);
							attributeList=new ArrayList<String>();
						}
						else
						{
							System.out.println("Duplicate job found in jil file");
							System.out.println(jobName);
							System.exit(15);
						}
					}

					String[] jobLine=lineSplitter[1].trim().split(" ");
					jobName=jobLine[0].trim();
					attributeName=jobLine[jobLine.length-1].trim()+":"; // getting job type here if it is available
					attributeValue=lineSplitter[2].trim()+" \n";
					attribute=attributeName+attributeValue;
					logger.debug(attributeName);
					logger.debug(attributeValue);
					logger.debug(attribute);
					attributeList.add(attribute);
					if(!jobMap.containsKey(jobName))
					{
						jobMap.put(jobName, attributeList);
						attributeList=new ArrayList<String>();
					}
				}
				else if(lineSplitter[0].contains("insert_job"))
				{
					jobName=lineSplitter[1].trim();
					
				}
				else
				{
					attribute=currentJilLine.trim()+" \n";
					attributeList.add(attribute);
					if(!jobMap.containsKey(jobName))
					{
						jobMap.put(jobName, attributeList);
						attributeList=new ArrayList<String>();
					}
					else
					{
						List<String> attrList = jobMap.get(jobName);
						attrList.add(attribute);
						jobMap.put(jobName, attrList);
					}
				}
				
			}
			logger.debug(jobMap);
		}
		
		return jobMap;
		
	}
	
	

}
