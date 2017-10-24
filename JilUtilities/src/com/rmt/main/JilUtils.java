package com.rmt.main;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

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
	
	static int jilCountParameter1;
	static int jilCountParameter2;
	static Map<String, List<String>> jobMap1 = new HashMap<String, List<String>>();
	static Map<String, List<String>> jobMap2 = new HashMap<String, List<String>>();
	static List<String> attributeList1 = new ArrayList<String>();
	static List<String> attributeList2 = new ArrayList<String>();
	
	public static void main(String[] args) throws IOException 
	{
		
		String log4jLocation = "resources/log4j.properties";
		PropertyConfigurator.configure(log4jLocation);
		logger=Logger.getLogger("JilUtilities.main");
		now = new Date();
		sdf = new SimpleDateFormat("YYYY-MM-d-hh-mm-ss");
		myTime = sdf.format(now);
		logger.info(myTime);
		
		JilFileUtils jf = new JilFileUtils();
		jobMap1=(HashMap<String, List<String>>) jf.readJobInformation("D:\\jildiff\\file1.txt");
		jobMap2=jf.readJobInformation("D:\\jildiff\\file2.txt");
		
		logger.info("done reading both files");
		
		
		
		/*JilUtils jUtils = new JilUtils();
		jUtils.getCommandLineArgs(args);*/
		/*DummyJIL dj = new DummyJIL();
		try 
		{
			dj.createDummyJil(15000, "cmd", "localhost");
			logger.info("Done creating jil");
		} catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/

	}
	public void getCommandLineArgs(String[] args)
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

}
