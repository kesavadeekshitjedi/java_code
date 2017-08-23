package mainUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import com.ca.eiam.SafeBackendServerException;
import com.ca.eiam.SafeException;
import com.ca.eiam.SafePasswordException;

import aeUtilities.AEAPIUtils;
import aeUtilities.AEDatabaseUtils;
import eemUtils.Connect2EEM;
import excelUtils.ExcelReader;
import jilFileUtils.ShredJilFile;
import jmoUtilities.JMOExtractAnalyzer;

public class JilUtilMain 
{
	List<String> jobNameList=new ArrayList<String>();
	List<String> jobsetNameList=new ArrayList<String>();
	Map<String, List<String>> jobsetMap = new LinkedHashMap<String, List<String>>(); 
	public static Date now;
	public static SimpleDateFormat sdf;
	public static String myTime;
	static Logger logger = Logger.getRootLogger();

	public static void writeToFile(String outputFile, String line) throws IOException
	{
		BufferedWriter jilWriter = new BufferedWriter(new FileWriter(outputFile));
		jilWriter.write(line+"\n");
	}
	public static void main(String[] args) throws IOException, EncryptedDocumentException, InvalidFormatException, ClassNotFoundException, SQLException, SafePasswordException, SafeBackendServerException, SafeException 
	{
		now = new Date();
		sdf = new SimpleDateFormat("YYYY-MM-d-hh-mm-ss");
		myTime = sdf.format(now);
		
		String log4jLocation = "resources/log4j.properties";
		PropertyConfigurator.configure(log4jLocation);
		logger=Logger.getLogger("JilUtilities.main");
		System.out.println("1. Get FT Jobs from instance");
		System.out.println("2. Read Excel for Top Level Boxes");
		System.out.println("3. Create Missing objects from jil (Resources and Machines)");
		System.out.println("4. JMO Extract Analyzer");
		System.out.println("5. Read JIL to put jobs in TopBoxes.");
		System.out.println("6. Get Job Status from DB");
		Scanner conScanner = new Scanner(System.in);
		System.out.println("Select an option:");
		int user_Choice = Integer.parseInt(conScanner.nextLine());
		switch(user_Choice)
		{
			case 1:
				AEAPIUtils aeApi = new AEAPIUtils();
				List<String> ftJobList=aeApi.getJobList("resources/as_server.properties", "FT");
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
				excelUtil.createOnlyTopBoxFromExcel_JPMC(excelPath,excelSheetName);
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
			case 5:
				excelUtils.ExcelReader excelUtil1 = new ExcelReader();
				System.out.println("This option will need to read the excel sheet for top boxes first");
				System.out.println("Enter the full path to the excel sheet");
				Scanner excelScanner1 = new Scanner(System.in);
				String excelPath1=excelScanner1.nextLine();
				System.out.println("Enter the sheet name to read");
				
				String excelSheetName1=excelScanner1.nextLine();
				System.out.println("Enter the full path to the jil file to read");
				String jilInputFile=excelScanner1.nextLine();
				excelUtil1.createOnlyTopBoxFromExcel_JPMC(excelPath1,excelSheetName1);
				ShredJilFile jilShred = new ShredJilFile();
				Map<String, String> excelMap = ExcelReader.jmo2AEJobsetMap;
				Iterator excelMapIterator = excelMap.entrySet().iterator();
				while(excelMapIterator.hasNext())
				{
					Map.Entry topBoxPair = (Map.Entry) excelMapIterator.next();
					String jobInTopBox=(String) topBoxPair.getKey();
					String topBoxName=excelMap.get(jobInTopBox);
					
					jilShred.readJilToUpdateJobNames(jilInputFile, jobInTopBox);
					
				}
			case 6:
				logger.info("Reading AutoSys DB Information from DB.properties file");
				AEDatabaseUtils aedbUtils = new AEDatabaseUtils();
				aedbUtils.getDBProperties("resources/DB.properties");
			case 7:
				Connect2EEM eem = new Connect2EEM();
				eem.connectToEEM("EEM-POC", "EiamAdmin", "ejmswat1", "WorkloadAutomationAE");
				
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
