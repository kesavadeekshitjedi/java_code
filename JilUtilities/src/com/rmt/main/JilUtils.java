package com.rmt.main;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.rmt.utilities.DBStatusParser;
import com.rmt.utilities.JilFileUtils;

public class JilUtils 
{
	public static Date now;
	public static SimpleDateFormat sdf;
	public static String myTime;
	static Logger logger = Logger.getRootLogger();

	String argTypeParameter;
	String inputJilParameter1;
	String inputJilParameter2;
	String outputJilParameter;
	String reportParameter;
	String replaceSuffixParameter;
	
	static int jilCountParameter1;
	static int jilCountParameter2;
	static Map<String, List<String>> jobMap1 = new HashMap<String, List<String>>();
	static Map<String, List<String>> jobMap2 = new HashMap<String, List<String>>();
	static List<String> attributeList1 = new ArrayList<String>();
	static List<String> attributeList2 = new ArrayList<String>();
	
	public static void main(String[] args) throws Exception 
	{
		
		String log4jLocation = "resources/log4j.properties";
		PropertyConfigurator.configure(log4jLocation);
		logger=Logger.getLogger("JilUtilities.main");
		now = new Date();
		sdf = new SimpleDateFormat("YYYY-MM-d-hh-mm-ss");
		myTime = sdf.format(now);
		logger.info(myTime);
		
		/*JilFileUtils jf = new JilFileUtils();
		jobMap1=jf.readJobInformation("D:\\OneDrive-Business\\OneDrive - Robert Mark Technologies\\JPMC-JMO-Conversion\\JMO_Extracts\\Phase4\\From_Hank\\2017.08.19\\JOBS_____.Tranche4.jil");
		jobMap2=jf.readJobInformation("D:\\OneDrive-Business\\OneDrive - Robert Mark Technologies\\JPMC-JMO-Conversion\\JMO_Extracts\\Phase4\\From_Hank\\2017.09.06\\__Tranche4_20170906_1\\JOBS_____.Tranche4.jil");
		
		logger.info("done reading both files");
		jf.getDifferences(jobMap1, jobMap2);*/
		
		Properties dbProps = new Properties();
		try 
		{
			dbProps.load(new FileInputStream("resources/DB.properties"));
		} 
		catch (IOException e1) 
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String dbHostName=dbProps.getProperty("AEDB_HOST");
		String dbPort=dbProps.getProperty("AEDB_DB_PORT");
		String dbType=dbProps.getProperty("AEDB_DB_TYPE");
		String dbName=dbProps.getProperty("AEDB_DB_SID");
		String dbUser=dbProps.getProperty("AEDB_DB_USER");
		String dbPass = dbProps.getProperty("AEDB_DB_PASS");
		DBStatusParser db = new DBStatusParser();
		Connection conn=db.connect2Oracle(dbHostName, dbPort, dbUser, dbPass, dbName);
		Map<String, String> jobMap=db.getJobStatusr110(conn, "ORA");
		
		JilFileUtils jfu = new JilFileUtils();
		String inputJil="D:\\JilUtilities\\JOBS_____.Geneva_Out.jil";
		String outputJil="D:\\JilUtilities\\MyJobs.txt";
		
		//jfu.addStatusLine2JilFile(inputJil, outputJil, jobMap);
		// enable the above line to get the status and create the migration jil.
		List<String> jobList = new ArrayList<String>();
		FileReader jobListReader = new FileReader("D:\\JilUtilities\\badjobs.txt");
		BufferedReader jobListBuffer = new BufferedReader(jobListReader);
		String currentLine;
		while((currentLine=jobListBuffer.readLine())!=null)
		{
			jobList.add(currentLine);
		}
		jfu.getJobsFromFile(inputJil, outputJil, jobList);
		
		
		
	}
	public void getCommandLineArgs(String[] args) throws IOException
	{
		logger=Logger.getLogger("JilUtils.getCommandLineArgs");
		if(args.length==0)
		{
			showHelp();
		}
		for(int cliArgCnt=0;cliArgCnt<args.length;cliArgCnt++)
		{
			if(args[cliArgCnt].equalsIgnoreCase("--type"))
			{
				argTypeParameter=args[cliArgCnt+1].trim(); 
				continue;
			}
			
			if(argTypeParameter.equalsIgnoreCase("SUFFIX"))
			{
				logger.info(argTypeParameter);
				if(args.length<4)
				{
					showSuffixHelp();
				}
				if(args[cliArgCnt+1].equalsIgnoreCase("--inputFile"))
				{
					inputJilParameter1=args[cliArgCnt+2].trim();
					continue;
				}
				if(args[cliArgCnt+2].equalsIgnoreCase("--outputFile"))
				{
					outputJilParameter=args[cliArgCnt+3].trim();
					continue;
				}
				if(args[args.length-2].equalsIgnoreCase("--replaceSuffix"))
				{
					replaceSuffixParameter=args[cliArgCnt+4].trim();
					
				}
				JilFileUtils jf = new JilFileUtils();
				jf.replaceJobNamesWithSuffix(inputJilParameter1, outputJilParameter, replaceSuffixParameter);
				
			}
			
			if(args[cliArgCnt].equalsIgnoreCase("--inputFile1"))
			{
				inputJilParameter1=args[cliArgCnt+1].trim();
				continue;
			}
			if(args[cliArgCnt].equalsIgnoreCase("--inputFile2"))
			{
				inputJilParameter2=args[cliArgCnt+1].trim();
				continue;
			}
			if(args[cliArgCnt].equalsIgnoreCase("--outputFile"))
			{
				outputJilParameter=args[cliArgCnt+1].trim();
				continue;
			}
			if(args[cliArgCnt].equalsIgnoreCase("--report"))
			{
				reportParameter=args[cliArgCnt+1].trim();
				continue;
			}
			
		}
		logger.debug(argTypeParameter+":"+inputJilParameter1+":"+inputJilParameter2+":"+outputJilParameter+":"+reportParameter);
	}
	private void showHelp() 
	{
		logger=Logger.getLogger("JilUtils.showHelp");
		System.out.println("Usage: java -jar JilUtilities.jar --type <program type> --inputFile1 <path_to_jil> --inputFile2 <path_to_jil> --outputFile <path_to_file> --report <path_to_file>");
		logger.error(("Usage: java -jar JilUtilities.jar --type <program type> --inputFile1 <path_to_jil> --inputFile2 <path_to_jil> --outputFile <path_to_file> --report <path_to_file>"));
		System.exit(5);
	}
	private void showSuffixHelp() 
	{
		logger=Logger.getLogger("JilUtils.showSuffixHelp");
		System.out.println("Usage: java -jar JilUtilities.jar --type SUFFIX --inputFile <path_to_jil> --outputFile <path_to_file> --replaceSuffix <SUFFIX_TO_APPEND>");
		logger.error(("Usage: java -jar JilUtilities.jar --type SUFFIX --inputFile <path_to_jil> --outputFile <path_to_file> --replaceSuffix <SUFFIX_TO_APPEND>"));
		System.exit(6);
	}


}
