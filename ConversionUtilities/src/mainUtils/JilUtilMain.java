package mainUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import aeUtilities.AEAPIUtils;
import excelUtils.ExcelReader;
import jmoUtilities.JMOExtractAnalyzer;

public class JilUtilMain 
{
	List<String> jobNameList=new ArrayList<String>();
	List<String> jobsetNameList=new ArrayList<String>();
	Map<String, List<String>> jobsetMap = new LinkedHashMap<String, List<String>>(); 
	static Logger logger = Logger.getRootLogger();

	public static void writeToFile(String outputFile, String line) throws IOException
	{
		BufferedWriter jilWriter = new BufferedWriter(new FileWriter(outputFile));
		jilWriter.write(line+"\n");
	}
	public static void main(String[] args) throws IOException, EncryptedDocumentException, InvalidFormatException 
	{
		String log4jLocation = "resources/log4j.properties";
		PropertyConfigurator.configure(log4jLocation);
		logger=Logger.getLogger("JilUtilities.main");
		System.out.println("1. Get FT Jobs from instance");
		System.out.println("2. Read Excel for Top Level Boxes");
		System.out.println("3. Create Missing objects from jil (Resources and Machines)");
		System.out.println("4. JMO Extract Analyzer");
		
		Scanner conScanner = new Scanner(System.in);
		System.out.println("Select an option:");
		int user_Choice = Integer.parseInt(conScanner.nextLine());
		switch(user_Choice)
		{
			case 1:
				AEAPIUtils aeApi = new AEAPIUtils();
				List<String> ftJobList=aeApi.getJobList("D:\\github\\java_code\\JilUtilities\\as_server.properties", "FT");
				logger.info(ftJobList);
				break;
			case 2:
				excelUtils.ExcelReader excelUtil = new ExcelReader();
				// OOM ERROR with large excel files
				System.out.println("Enter the full path to the excel sheet");
				Scanner excelScanner = new Scanner(System.in);
				String excelPath=excelScanner.nextLine();
				System.out.println("Enter the sheet name to read");
				String excelSheetName=excelScanner.nextLine();
				excelUtil.readExcel(excelPath,excelSheetName);
				break;
			case 3:
				jilFileUtils.ShredJilFile shredder = new jilFileUtils.ShredJilFile();
				System.out.println("Enter the full path to the JIL File");
				String jilInput=conScanner.nextLine();
				try 
				{
					shredder.readJilToGetJobNames(jilInput);
				} 
				catch (IOException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			case 4:
				JMOExtractAnalyzer jmo = new JMOExtractAnalyzer();
				System.out.println("1. Check predecessors");
				System.out.println("2. Create JMO Object Report only");
				Scanner jmoScanner = new Scanner(System.in);
				String jmoOption = jmoScanner.nextLine();
				System.out.println("Enter the full path to the JMO Extract");
				String jmoPath = jmoScanner.nextLine();
				File jmoFile = new File(jmoPath);
				boolean fileExists = jmoFile.exists();
				if(!fileExists)
				{
					System.out.println("File does not exist. Exiting...");
					System.exit(4);
				}
				else
				{
					jmo.readJMOExtractHighLevel(jmoPath);
				}
				if(jmoOption.equals("1"))
				{
					jmo.checkJobPredecessors(jmoPath);
				}
				
				
		}
		
		
		/*Map<String, List<String>> calendarMap=shredder.putFileTriggersIntoBox("D:\\github\\java_code\\JilUtilities\\as_server.properties");
		Iterator calendarIterator = calendarMap.entrySet().iterator();
		while(calendarIterator.hasNext())
		{
			Map.Entry kvPair = (Map.Entry)calendarIterator.next();
			String runCalendar = (String) kvPair.getKey();
			List<String> jobsPerCalendar=calendarMap.get(runCalendar);
			writeToFile("D:\\TopLevelBox-FT.jil", "insert_job: "+runCalendar);
			writeToFile("D:\\TopLevelBox-FT.jil","job_type: BOX");
			writeToFile("D:\\TopLevelBox-FT.jil","\n");
			for(int i=0;i<jobsPerCalendar.size();i++)
			{
				logger.info(jobsPerCalendar.get(i));
				writeToFile("D:\\TopLevelBox-FT.jil", "update_job: "+jobsPerCalendar.get(i));
				writeToFile("D:\\TopLevelBox-FT.jil","date_conditions: n");
				writeToFile("D:\\TopLevelBox-FT.jil","\n");
			}
			
		}*/
		
		
		
		
		// TODO Auto-generated method stub

	}

}
