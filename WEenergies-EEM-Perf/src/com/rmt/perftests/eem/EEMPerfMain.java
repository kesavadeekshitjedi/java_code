package com.rmt.perftests.eem;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.ca.eiam.SafeAuthorizationResult;
import com.ca.eiam.SafeContext;
import com.ca.eiam.SafeException;
import com.ca.eiam.SafeSession;

public class EEMPerfMain 
{
	static Logger logger = Logger.getRootLogger();

	static SafeSession safeSession;
	static SafeContext safeContext;
	static SafeAuthorizationResult safeResult;
	static String resultFile=null;
	static FileWriter resultWriter;
	static BufferedWriter resultBuffer;
	Date now;
	static SimpleDateFormat sdf = new SimpleDateFormat("hh-mm-ss.SSS");
	
	static String eemHostName;
	static String eemUser;
	static String eemPassword;
	static String eemApplication;

	public void readEEMProperties() throws FileNotFoundException, IOException 
	{
		String eemPropertiesFile = "resources/eem.properties";
		Properties eemProperties = new Properties();
		eemProperties.load(new FileInputStream(eemPropertiesFile));
		eemHostName=eemProperties.getProperty("EEM_HOST");
		eemUser=eemProperties.getProperty("EEM_ADMIN_USERID");
		eemPassword=eemProperties.getProperty("EEM_PASS");
		eemApplication=eemProperties.getProperty("WorkloadAutomationAE");
		
		
		
	}
	
	public SafeSession createEEMSession(String eemHost, String eemID, String eemPassword, String eemApplication)
	{
		now = new Date();
		resultFile="EEM-Results"+sdf.format(now)+".txt";
		String log4jLocation = "resources/log4j.properties";
		PropertyConfigurator.configure(log4jLocation);
		System.out.println("logging initialized...");
		logger.info("Attempting to connect to "+eemHost);
		Instant start=Instant.now(); 
		try
		{
			safeContext=new SafeContext();
			safeContext.setBackend(eemHost);
			safeSession=safeContext.authenticateWithPassword(eemID, eemPassword);
			safeContext.attach(eemApplication, safeSession);
			
			
		}
		catch(SafeException se)
		{
			se.printStackTrace();
		}
		
		
		Instant end=Instant.now();
		Duration elapsedTime = Duration.between(start, end);
		System.out.println("Time taken to establish connection: "+elapsedTime.toMillis()+" milliseconds");
		return safeSession;
	}
	public void writeResultFile(String resultMessage,String resultFile)
	{

		
		try
		
		{
			resultWriter = new FileWriter(resultFile,true);
			resultBuffer = new BufferedWriter(resultWriter);
			resultBuffer.write(sdf.format(now)+" "+resultMessage);
			resultBuffer.write("\n");
					
		}
		catch(Exception e)
		{
			logger.error(e);
		}
		
	}
	public void runPolicyTest(String appName, String aeInstanceName, String user, String action, String policy, String policyResource, int numRuns) throws IOException
	{
		
		//SafeAuthorizationResult policyResult;
		long testStartTime;
		long testEndTime;
		String policyString = null;
		
		boolean policyResult=false;
		//logger.info("Policy Check tests started @ "+startTime);
		for(int cdX=0;cdX<=numRuns;cdX++)
		{
			logger.info("Running test iteration: "+cdX);
			Instant startTime=Instant.now();
			try
			{
				
				if(!policy.contentEquals("as-sendevent"))
				{
					policyResource=aeInstanceName+"."+policyResource;
				}
				policyString="Checking Policy: "+policy+",User: "+user+",Permission: "+action+",Resource: "+policyResource;
				safeResult=safeContext.authorizeWithIdentity(user,action,policy,policyResource, null, null);
				Instant endTime=Instant.now();
				//policyResult=safeResult.getResult();
				writeResultFile(policyString+" \tTime taken for evalutation: "+Duration.between(startTime, endTime).toMillis()+" milliseconds", resultFile);
				
				System.out.println("Time taken to run test: "+Duration.between(startTime, endTime).toMillis()+" milliseconds");
				
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		
		
		//logger.debug("Policy Result: "+policyResult);
		
		resultBuffer.close();
		
		
	}
	public static void main(String[] args) throws FileNotFoundException, IOException 
	{
		EEMPerfMain eem = new EEMPerfMain();
		eem.readEEMProperties();
		//eem.createEEMSession(args[0], args[1], args[2], args[3]);
		eem.createEEMSession(eemHostName,eemUser,eemPassword,eemApplication);
		eem.runPolicyTest(eemApplication, args[0], args[1], args[2], args[3], args[4],Integer.parseInt(args[5]));

	}

}
