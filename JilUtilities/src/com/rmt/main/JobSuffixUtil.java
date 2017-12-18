package com.rmt.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
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
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class JobSuffixUtil 
{
	static Logger logger = Logger.getRootLogger();
	FileReader jilFileReader = null ;
	BufferedReader jilFileBuffer = null ;
	FileWriter outputFileWriter = null ;
	BufferedWriter outputBuffer = null ;
	
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
			if(currentJilLine.contains("insert_job"))
			{
				String retrievedJobName = modifiedJobNameList.get(jobName);
				logger.debug("attribute to change found in "+currentJilLine);
				String newAttribute = currentJilLine.replace(jobName, retrievedJobName);
				currentJilLine=newAttribute;
				logger.debug("Replaced string: "+currentJilLine);
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
	public void close() throws IOException
	{
		outputBuffer.close();
		outputFileWriter.close();
		jilFileBuffer.close();
		jilFileReader.close();
	}
	public static void main(String args[]) throws IOException
	{
		String log4jLocation = "resources/log4j.properties";
		PropertyConfigurator.configure(log4jLocation);
		JobSuffixUtil jsu = new JobSuffixUtil();
		jsu.replaceJobNamesWithSuffix("D:\\JilUtilities\\ALLPeopleSoftConvertedJobs.jil", "D:\\JilUtilities\\PS-Suffix.txt", "-PS");
	}
	

}
