package com.rmt.utilities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	
	FileReader jilFileReader = null ;
	BufferedReader jilFileBuffer = null ;
	FileWriter outputFileWriter = null ;
	BufferedWriter outputBuffer = null ;
	Map<String, String> migrationStatusMap=new HashMap<String, String>();
	public void close() throws IOException
	{
		outputBuffer.close();
		outputFileWriter.close();
		jilFileBuffer.close();
		jilFileReader.close();
	}
	public void getJobsFromFile(String inputFile, String outputFile, List<String> jobList) throws IOException
	{
		logger=Logger.getLogger("JilUtilities.utilities.addStatusLine2JilFile");
		logger.info("Make a copy of the original jil file");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
		Date currentDate = new Date();
		String dateFormat = sdf.format(currentDate);
		String outputTarget=outputFile+"."+dateFormat+".definitions";
		FileReader inputFileReader = new FileReader(inputFile);
		BufferedReader inputFileBuffer = new BufferedReader(inputFileReader);
		FileWriter outputFileWriter = new FileWriter(outputFile);
		BufferedWriter outputFileBuffer = new BufferedWriter(outputFileWriter);
		
		/*
		 * Making a copy of the original file in the next few lines here.
		 */
		InputStream input=null;
		OutputStream output = null;
		String targetFile=inputFile+".backup."+dateFormat;
		input = new FileInputStream(new File(inputFile));
		output = new FileOutputStream(new File(targetFile));
		byte[] buff = new byte[1024];
		int bytesRead;
		while((bytesRead = input.read(buff))>0)
		{
			output.write(buff, 0, bytesRead);
		}
		output.close();
		input.close();
		logger.info("File copied successfully");
		// File back up operation complete.
		
		String jilFileLine=null;
		String jobName=null;
		boolean foundNewJob=false;
		String[] lineSplitter = null;
		String newLine=null;
		logger.info("attempting to read "+inputFile);
		while((jilFileLine=inputFileBuffer.readLine())!=null)
		{
			String currentJilLine=jilFileLine.trim();
			if(!currentJilLine.contains("#") && (!currentJilLine.isEmpty() && (!currentJilLine.contains("/*") && (!currentJilLine.contains("//")))))
			{
				logger.debug(jilFileLine);
				lineSplitter=currentJilLine.split(":");
				if(lineSplitter.length==3 && lineSplitter[0].contains("insert_job"))
				{
					
					if(lineSplitter.length==3 && lineSplitter[0].contains("insert_job"))
					{
						foundNewJob=true;
						String[] jobLine=lineSplitter[1].trim().split(" ");
						jobName=jobLine[0].trim();
						if(jobList.contains(jobName))
						{
							foundNewJob=true;
						}
						else
							foundNewJob=false;
					}
					
				}
				
			}
			if(foundNewJob==true && (!currentJilLine.contains("#")) && (!currentJilLine.contains("/*")))
			{
				outputFileBuffer.write(currentJilLine+" \n");
			}
		}
		outputFileBuffer.close();
		outputFileWriter.close();
		inputFileBuffer.close();
		inputFileReader.close();
		
	}
	public void addStatusLine2JilFile(String inputJil, String outputJil, Map<String, String> jobStatusMap) throws IOException
	{
		
		migrationStatusMap.put("OI", "ON_ICE");
		migrationStatusMap.put("OH", "ON_HOLD");
		migrationStatusMap.put("TE", "TERMINATED");
		migrationStatusMap.put("IN/NE", "NO_EXEC");
		migrationStatusMap.put("SU", "SUCCESS");
		migrationStatusMap.put("FA", "FAILURE");
		migrationStatusMap.put("IN", "INACTIVE");
		migrationStatusMap.put("PE", "PENDING_MACHINE_REVIEW");
		
		logger=Logger.getLogger("JilUtilities.utilities.addStatusLine2JilFile");
		logger.info("Make a copy of the original jil file");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
		Date currentDate = new Date();
		String dateFormat = sdf.format(currentDate);
		String outputTarget=outputJil+".backup."+dateFormat;
		FileReader inputJilReader = new FileReader(inputJil);
		FileWriter outputFileWriter = new FileWriter(outputTarget);
		BufferedReader inputJilBuffer = new BufferedReader(inputJilReader);
		BufferedWriter outputJilBuffer = new BufferedWriter(outputFileWriter);
		/*
		 * Making a copy of the original file in the next few lines here.
		 */
		InputStream input=null;
		OutputStream output = null;
		String targetFile=inputJil+".backup."+dateFormat;
		input = new FileInputStream(new File(inputJil));
		output = new FileOutputStream(new File(targetFile));
		byte[] buff = new byte[1024];
		int bytesRead;
		while((bytesRead = input.read(buff))>0)
		{
			output.write(buff, 0, bytesRead);
		}
		output.close();
		input.close();
		logger.info("File copied successfully");
		// File back up operation complete.
		
		String jilFileLine=null;
		String jobName=null;
		boolean foundNewJob=false;
		String[] lineSplitter = null;
		String newLine=null;
		
		logger.info("attempting to read "+inputJil);
		while((jilFileLine=inputJilBuffer.readLine())!=null)
		{
			String currentJilLine=jilFileLine.trim();
			if(!currentJilLine.contains("#") && (!currentJilLine.isEmpty() && (!currentJilLine.contains("/*") && (!currentJilLine.contains("//")))))
			{
				logger.debug(jilFileLine);
				lineSplitter=currentJilLine.split(":");
				if(lineSplitter.length==3 && lineSplitter[0].contains("insert_job"))
				{
					foundNewJob=true;
					if(lineSplitter.length==3 && lineSplitter[0].contains("insert_job"))
					{
						foundNewJob=true;
						String[] jobLine=lineSplitter[1].trim().split(" ");
						jobName=jobLine[0].trim();
					}
					
					String currentStatus=jobStatusMap.get(jobName);
					String migrationStatus=migrationStatusMap.get(currentStatus);
					if(migrationStatus.equals("") || migrationStatus==null)
					{
						migrationStatus="ON_ICE"; // defaulting to an ON_ICE status for jobs in case a weird status exists.
					}
					currentJilLine=currentJilLine+" \n"+"status: "+migrationStatus+" \n";
					logger.debug(currentJilLine);
				}
			}
			if(currentJilLine.contains("insert_job:"))
			{
			outputJilBuffer.write(currentJilLine);
			}
			else
			{
				outputJilBuffer.write(currentJilLine+" \n");
			}
		}
		
		outputJilBuffer.close();
		outputFileWriter.close();
		inputJilBuffer.close();
		inputJilReader.close();
		logger.info("All readers and writers closed");
	}
	public void replaceJobNamesWithSuffix(String inputJil, String outputJil, String jilSuffix) throws IOException
	{
		Map<String, String> modifiedJobNameList = new HashMap<String, String>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
		Date currentDate = new Date();
		String dateFormat = sdf.format(currentDate);
		InputStream input=null;
		OutputStream output = null;
		logger=Logger.getLogger("JilUtilities.utilities.replaceJobNamesWithSuffix");
		
		
		 jilFileReader = new FileReader(inputJil);
		 jilFileBuffer = new BufferedReader(jilFileReader);
		 outputFileWriter = new FileWriter(outputJil);
		 outputBuffer = new BufferedWriter(outputFileWriter);
		
		logger.info("All Writers and Readers open");
		
		/*
		 * Making a copy of the original file in the next few lines here.
		 */
		
		String targetFile=inputJil+".backup."+dateFormat;
		input = new FileInputStream(new File(inputJil));
		output = new FileOutputStream(new File(targetFile));
		byte[] buff = new byte[1024];
		int bytesRead;
		while((bytesRead = input.read(buff))>0)
		{
			output.write(buff, 0, bytesRead);
		}
		
		logger.info("File copied successfully");
		// File back up operation complete.
		
		// read the file quickly to gather all jobnames from the insert_job line and store the modifiedJobnames
		
		String jilFileLine=null;
		String jobName=null;
		String attributeName=null;
		String attributeValue=null;
		boolean foundNewJob=false;
		String[] lineSplitter = null;
		
		logger.info("attempting to read "+inputJil);
		while((jilFileLine=jilFileBuffer.readLine())!=null)
		{
			String currentJilLine=jilFileLine.trim();
			if(!currentJilLine.contains("#") && (!currentJilLine.isEmpty() && (!currentJilLine.contains("/*") && (!currentJilLine.contains("//")))))
			{
				logger.debug(jilFileLine);
				lineSplitter=currentJilLine.split(":");
				if(lineSplitter.length==3 && lineSplitter[0].contains("insert_job"))
				{
					foundNewJob=true;
					String[] jobLine=lineSplitter[1].trim().split(" ");
					jobName=jobLine[0].trim();
					modifiedJobNameList.put(jobName, jobName+jilSuffix);
				}
			}
			
		}
		logger.info("Done collecting job names");
		logger.debug(modifiedJobNameList);
		jilFileBuffer.close();
		jilFileReader.close();
		jobName=null;
		jilFileReader = new FileReader(inputJil);
		jilFileBuffer = new BufferedReader(jilFileReader);
		jilFileLine=null;
		foundNewJob=false;
		String modifiedJilLine=null;
		String conditionString=null;
		String originalConditionString=null;
		while((jilFileLine=jilFileBuffer.readLine())!=null)
		{
			String currentJilLine=jilFileLine.trim();
			lineSplitter=currentJilLine.split(":");
			if(lineSplitter.length==3 && lineSplitter[0].contains("insert_job"))
			{
				foundNewJob=true;
				String[] jobLine=lineSplitter[1].trim().split(" ");
				jobName=jobLine[0].trim();
			}
			if((currentJilLine.contains("condition:") || (currentJilLine.contains("box_success")) || (currentJilLine.contains("box_failure") || (currentJilLine.contains("box_name")))))
			{
				
				originalConditionString=currentJilLine;
				logger.debug("attribute to change found in "+currentJilLine);
				List<String> conditionList=parseConditionField(currentJilLine);
				
				for(String condition: conditionList)
				{
					String retrievedJobName=modifiedJobNameList.get(condition);
					if(retrievedJobName==null)
					{
						retrievedJobName=condition;
					}
					String newAttribute = currentJilLine.replace(condition, retrievedJobName);
					currentJilLine=newAttribute;
					logger.debug("Replaced string: "+currentJilLine);
				}
				modifiedJilLine=currentJilLine;
				
			}
			modifiedJilLine=currentJilLine;
			outputBuffer.write(modifiedJilLine+"\n");
		
		}
		logger.info("Done with the file");
		close();
			
			/*Map<String, List<String>> jobInformationMap = readJobInformation(inputJil);
			Iterator jobMapIterator = jobInformationMap.entrySet().iterator();
			Iterator jobAttributeIterator;
			
			 * 1. For each job in the hashmap (jobInformationMap) check if the attribute is condition or box_success or box_failure
			 * 2. If the condition attribute exists, check if the job has a changed name from the HashMap
			 * 3. If the job has a changed name, then replace it
			 *  > Parse the condition field to get everything minus the condition: keyword. 
			 *  > Split the condition on white space .split(" "). this will give us seperate entities for condition and operator between conditions
			 *  > For each element in the split array, check if there is a replacement. if there is, replace
			 *  > At the end, add the condition: keyword back
			 *  > String str = "condition: "+existingConditionString
			 *  > 
			 *  > 
			 
			String jobNameKey=null;
			String modifiedJobName=null;
			String conditionString=null;
			while(jobMapIterator.hasNext())
			{
				Map.Entry pair = (Map.Entry)jobMapIterator.next();
				jobNameKey=(String)pair.getKey();
				modifiedJobName=jobNameKey+jilSuffix;
				modifiedJobNameList.put(jobNameKey,modifiedJobName);
				logger.debug("Job name: "+jobNameKey+" will change to :"+modifiedJobName);
				logger.debug("Stored into Map");
				
				
			}
			for(List<String> attributeValues: jobInformationMap.values())
			{
				jobAttributeIterator = attributeValues.iterator(); // we have all the attributes for all jobs here.
				logger.debug(jobNameKey);
				while(jobAttributeIterator.hasNext())
				{
					//logger.debug(jobAttributeIterator.next());
					String attribute=(String) jobAttributeIterator.next();
					String origAttribute=attribute;
					conditionString=attribute;
					if((attribute.contains("condition:") || (attribute.contains("box_success:")) || (attribute.contains("box_failure:"))))
					{
						List<String> conditionList=parseConditionField(attribute);
						if(conditionList.size()>=2)
						{
							logger.info("test");
						}
						for(String condition: conditionList)
						{
							String retrievedJobName = modifiedJobNameList.get(condition);
							logger.debug(attribute+" needs modifications");
							String newAttrib=attribute.replace(condition, retrievedJobName);
							conditionString=newAttrib;
							attribute=conditionString;
							logger.debug("New Attribute: "+newAttrib);
						}
						// get list of attributes for key
						List<String> attributesForKey = jobInformationMap.get(jobNameKey);
						int valIdx=attributesForKey.indexOf(origAttribute);
						// add a # to the attribute
						attributesForKey.add("#"+origAttribute);
						attributesForKey.add(conditionString);
						attributesForKey.remove(valIdx);
						jobInformationMap.put(jobNameKey, attributesForKey);
					}
					
					
					
				}
			}*/
			
		
		
	}
	
	public List<String> parseConditionField(String conditionString)
	{
		List<String> listOfValues = new ArrayList<String>();
		String regex="\\(([^(]+?)\\)";
		
		Matcher matcher  = Pattern.compile(regex).matcher(conditionString);
		while(matcher.find())
		{
			listOfValues.add(matcher.group(1));
		}
		return listOfValues;
	}
	public void getDifferences(Map<String, List<String>> jobMap1, Map<String, List<String>> jobMap2) throws IOException
	{
		logger=Logger.getLogger("JilUtilities.utilities.getDifferences");
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
			
		}
		logger.debug(jobMap);
		return jobMap;
		
	}
	
	

}
