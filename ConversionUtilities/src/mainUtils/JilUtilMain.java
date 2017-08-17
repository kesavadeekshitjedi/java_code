package mainUtils;

import java.io.BufferedWriter;
import java.io.Console;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
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
		System.out.println("4. Get ");
		AEAPIUtils aeApi = new AEAPIUtils();
		List<String> ftJobList=aeApi.getJobList("D:\\github\\java_code\\JilUtilities\\as_server.properties", "FT");
		logger.info(ftJobList);
		Scanner conScanner = new Scanner(System.in);
		System.out.println("Enter the full path to the JIL File");
		String jilInput=conScanner.nextLine();
		jilFileUtils.ShredJilFile shredder = new jilFileUtils.ShredJilFile();
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
		excelUtils.ExcelReader excelUtil = new ExcelReader();
		// OOM ERROR with large excel files
		excelUtil.readExcel("C:\\jmofiles\\Tranche4JobstoBeConverted-Final.xlsx","Manually Entered Data");
		try 
		{
			shredder.readJilToGetJobNames(jilInput);
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated method stub

	}

}
