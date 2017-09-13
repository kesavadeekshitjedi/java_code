package excelUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import jilFileUtils.ShredJilFile;
import mainUtils.JilUtilMain;
import workerUtilities.WorkerUtils;

public class ExcelReader 
{
	public static WorkerUtils wUtils;
	public static Map<String,String> jmo2AEJobsetMap = new HashMap<String, String>();
	
	
	public static void ExcelReader()
	{
		wUtils = new WorkerUtils();
	}
	static Logger logger = Logger.getRootLogger();
	public void readExcelToSetJobsInTopBox(String excelInput, String sheetName) throws EncryptedDocumentException, InvalidFormatException, IOException 
	{
		ShredJilFile sf = new ShredJilFile();
		FileWriter fw;
		BufferedWriter bw;
		Map<String, List<String>> topBoxJobMap = new HashMap<String, List<String>>();
		logger=Logger.getLogger("ConversionUtilities.ExcelUtils.readExcelToSetJobsInTopBox");
		logger.info("Reading Excel File: "+excelInput+" to create update statements for autosys jobs to be set into top boxes with start time and calendars");
		Workbook excelWorkbook = null;
		excelWorkbook=WorkbookFactory.create(new File(excelInput));
		Sheet myExcelSheet = excelWorkbook.getSheet(sheetName);
		logger.info("Excel worksheet" + sheetName+" open");
		String topBoxName="";
		String topLevelBoxCalendar="";
		String topLevelBoxStartTime="";
		List<String> jobsTopBox=new ArrayList<String>();
		String jobName="";
		Iterator<Row> rowIterator = myExcelSheet.iterator();
		File myFolder = new File("C:\\JMOFiles\\TopBox_T2\\"+JilUtilMain.myTime);
		myFolder.mkdir();
		fw = new FileWriter(myFolder+"\\T2_TopLevel.txt");
		bw = new BufferedWriter(fw);
		System.out.println("Folder created: "+myFolder);
		while(rowIterator.hasNext())
		{
			Row myRow=rowIterator.next();
			Iterator<Cell> cellIterator=myRow.cellIterator();
			while(cellIterator.hasNext())
			{
				Cell myCell = cellIterator.next();
				if(myRow.getRowNum()==0 ||  (myCell.getColumnIndex()<5) || (myCell.getColumnIndex()>10))
				{
					logger.info("Skipping row/column: {"+myRow.getRowNum()+","+myCell.getColumnIndex()+"}");
					
				}
				else
				{
					
					// Column 1 is not useful for us. Skip this one 
					
					if(((myRow.getRowNum()>=1)) && myCell.getColumnIndex()==6)
					{
						logger.debug("Reading Cell: {"+myRow.getRowNum()+","+myCell.getColumnIndex()+"}");
						// This is the top box name
						topLevelBoxStartTime=myCell.getStringCellValue();
						//logger.debug("Jobset "+jobsetName+" goes into Top Box: "+topBoxName);
						
						topLevelBoxStartTime=topLevelBoxStartTime.trim();
					}
					if(((myRow.getRowNum()>=1)) && myCell.getColumnIndex()==8)
					{
						logger.debug("Reading Cell: {"+myRow.getRowNum()+","+myCell.getColumnIndex()+"}");
						// This is the start time
						topLevelBoxCalendar= myCell.getStringCellValue();
						topLevelBoxCalendar=topLevelBoxCalendar.trim();
						logger.debug("Top Box: "+topBoxName+" starts at "+topLevelBoxStartTime);
					}
					if(((myRow.getRowNum()>=1)) && myCell.getColumnIndex()==9)
					{
						logger.debug("Reading Cell: {"+myRow.getRowNum()+","+myCell.getColumnIndex()+"}");
						// This is the calendar name
						topBoxName=myCell.getStringCellValue();
						topBoxName=topBoxName.trim();
						logger.debug("Top Box: "+topBoxName+" starts at "+topLevelBoxStartTime+" and uses calendar: "+topLevelBoxCalendar);
					}
					if((myRow.getRowNum()>=1) && myCell.getColumnIndex()==10)
					{
						logger.debug("Reading Cell: {"+myRow.getRowNum()+","+myCell.getColumnIndex()+"}");
						// This is the calendar name
						jobName=myCell.getStringCellValue().trim();
						logger.debug("Top Box: "+topBoxName+" starts at "+topLevelBoxStartTime+" and uses calendar: "+topLevelBoxCalendar+". Job "+jobName+" is part of "+topBoxName);
						System.out.println("update_job: "+jobName+"\n");
						System.out.println("date_conditions: 1"+"\n");
						System.out.println("run_calendar: "+topLevelBoxCalendar+"\n");
						System.out.println("box_name: "+topBoxName+"\n");
						System.out.println("start_times: \""+topLevelBoxStartTime+"\""+"\n");
						bw.write("update_job: "+jobName+"\n");
						bw.write("date_conditions: 1"+"\n");
						bw.write("run_calendar: "+topLevelBoxCalendar+"\n");
						bw.write("box_name: "+topBoxName+"\n");
						bw.write("start_times: \""+topLevelBoxStartTime+"\""+"\n");
						String jobCommand=sf.getCommandForJob("C:\\JMOFiles\\T2_CommandUpdates.jil", jobName);
						bw.write("command: "+jobCommand);
						bw.write("\n");
					}
					
				}
			}
		}
		bw.close();
		fw.close();
		logger.debug("Writers closed");
		
	}
	
	public void createOnlyTopBoxFromExcel_JPMC(String excelInput, String sheetName) throws EncryptedDocumentException, InvalidFormatException, IOException
	{
		Map<String,String> boxCreationMap = new HashMap<String,String>();
		logger=Logger.getLogger("JilUtilities.ExcelUtils.createOnlyTopBoxFromExcel_JPMC");
		logger.info("Reading Excel File: "+excelInput+" to create just Top Level JIL");
		Workbook excelWorkbook = null;
		excelWorkbook=WorkbookFactory.create(new File(excelInput));
		Sheet myExcelSheet = excelWorkbook.getSheet(sheetName);
		logger.info("Excel worksheet" + sheetName+" open");
		String topBoxName="";
		String topLevelBoxCalendar="";
		String topLevelBoxStartTime="";
		String jobsetInTopBox="";
		Iterator<Row> rowIterator = myExcelSheet.iterator();
		List<String> jobsetsinTopBox=new ArrayList<String>();
		File myFolder = new File("C:\\JMOFiles\\TopBox\\"+JilUtilMain.myTime);
		myFolder.mkdir();
		while(rowIterator.hasNext())
		{
			Row myRow=rowIterator.next();
			Iterator<Cell> cellIterator=myRow.cellIterator();
			while(cellIterator.hasNext())
			{
				Cell myCell = cellIterator.next();
				if(myRow.getRowNum()==1 ||  (myCell.getColumnIndex()>=5))
				{
					logger.info("Skipping row/column: {"+myRow.getRowNum()+","+myCell.getColumnIndex()+"}");
					
				}
				else
				{
					
					// Column 1 is not useful for us. Skip this one 
					
					if(((myRow.getRowNum()>1)) && myCell.getColumnIndex()==0)
					{
						logger.debug("Reading Cell: {"+myRow.getRowNum()+","+myCell.getColumnIndex()+"}");
						// This is the top box name
						topBoxName=myCell.getStringCellValue();
						//logger.debug("Jobset "+jobsetName+" goes into Top Box: "+topBoxName);
						if(topBoxName.contains(" "))
						{
							topBoxName=topBoxName.replace(" ","-");
						}
						topBoxName=topBoxName.trim();
					}
					if(((myRow.getRowNum()>1)) && myCell.getColumnIndex()==1)
					{
						logger.debug("Reading Cell: {"+myRow.getRowNum()+","+myCell.getColumnIndex()+"}");
						// This is the start time
						topLevelBoxStartTime= myCell.getStringCellValue();
						if(topLevelBoxStartTime=="")
						{
							topLevelBoxStartTime="18:45";
						}
						logger.debug("Top Box: "+topBoxName+" starts at "+topLevelBoxStartTime);
					}
					if(((myRow.getRowNum()>1)) && myCell.getColumnIndex()==2)
					{
						logger.debug("Reading Cell: {"+myRow.getRowNum()+","+myCell.getColumnIndex()+"}");
						// This is the calendar name
						topLevelBoxCalendar=myCell.getStringCellValue();
						logger.debug("Top Box: "+topBoxName+" starts at "+topLevelBoxStartTime+" and uses calendar: "+topLevelBoxCalendar);
					}
					if((myRow.getRowNum()>1) && myCell.getColumnIndex()==3)
					{
						logger.debug("Reading Cell: {"+myRow.getRowNum()+","+myCell.getColumnIndex()+"}");
						// This is the calendar name
						jobsetInTopBox=myCell.getStringCellValue().trim();
						logger.debug("Top Box: "+topBoxName+" starts at "+topLevelBoxStartTime+" and uses calendar: "+topLevelBoxCalendar+". Jobset "+jobsetInTopBox+" is part of "+topBoxName);
						if(!jmo2AEJobsetMap.containsKey(topBoxName))
						{
							jobsetsinTopBox=new ArrayList<String>();
							jobsetsinTopBox.add(jobsetInTopBox);
							jmo2AEJobsetMap.put(jobsetInTopBox,topBoxName);
							//jmo2AEJobsetMap.put(topBoxName, jobsetInTopBox); - This was the original. Swapped it to make it a longer Map, but definitely unique??
						}
						else
						{
							//jobsetsinTopBox=new ArrayList<String>();
							//jobsetsinTopBox=jmo2AEJobsetMap.get(topBoxName);
							//jobsetsinTopBox.add(jobsetInTopBox);
							jmo2AEJobsetMap.put(jobsetInTopBox,topBoxName);
							//jmo2AEJobsetMap.put(topBoxName, jobsetInTopBox);
						}
					}
				}
			}
			logger.info("Top Box: "+topBoxName+" TopBoxCalendar: "+topLevelBoxCalendar+" TopBox Start Time: "+topLevelBoxStartTime);
			
			if(!boxCreationMap.containsKey(topBoxName))
			{
				
				boxCreationMap.put(topBoxName, "False");
				
			}
			String boxCreated = boxCreationMap.get(topBoxName);
			if(!(topBoxName=="") && ((boxCreated.equals("False"))))
			{
				String topLevelBoxFileName=myFolder+"\\"+"TopBoxDefinitions.jil";
				
				logger.info("Creating "+topLevelBoxFileName);
				writeToFile(topLevelBoxFileName,"#------"+topBoxName+"------#");
				writeToFile(topLevelBoxFileName,"\n");
				writeToFile(topLevelBoxFileName,"insert_job: "+topBoxName.trim()+"\n");
				writeToFile(topLevelBoxFileName,"job_type: BOX"+"\n");
				writeToFile(topLevelBoxFileName,"date_conditions:1"+"\n");
				writeToFile(topLevelBoxFileName,"start_times: \""+topLevelBoxStartTime+"\"\n");
				writeToFile(topLevelBoxFileName,"run_calendar: "+topLevelBoxCalendar+"\n");
				writeToFile(topLevelBoxFileName,"\n");
				boxCreationMap.put(topBoxName, "True");
			}
		}
	}
	public void writeToFile(String fileName, String content) throws IOException
	{
		FileWriter myFile=null;
		BufferedWriter fileWriter=null;
		try
		{
		myFile = new FileWriter(fileName,true);
		fileWriter = new BufferedWriter(myFile);
		fileWriter.write(content);
		}
		catch(FileNotFoundException fe)
		{
			fe.printStackTrace();
		}
		finally
		{
			fileWriter.close();
			myFile.close();
		}
	}
	public String getCrossReferencedJPMName(String excelFile, String sheetName, String jobName) throws EncryptedDocumentException, InvalidFormatException, IOException
	{
		
		
		// This method reads the excel sheet for the unmodified job name and gets the JPMC'ed or cross referenced job name 
		// and passes it back to the ShredJilFile class to update the missing dependencies
		String jmoCrossReferencedName="";
		
		Workbook excelWorkbook = null;
		String fileExtension=excelFile.substring(excelFile.indexOf("."));
		
		excelWorkbook=WorkbookFactory.create(new File(excelFile));
		logger.info("Excel workbook open");
		Sheet myExcelSheet = excelWorkbook.getSheet(sheetName);
		logger.info("Excel worksheet open");
		Iterator<Row> rowIterator = myExcelSheet.iterator();
		logger.info("Reading rows...");
		boolean foundConditionJob=false;
		while(rowIterator.hasNext())
		{
			foundConditionJob=false;
			Row myRow=rowIterator.next();
			Iterator<Cell> cellIterator=myRow.cellIterator();
			while(cellIterator.hasNext())
			{
				Cell myCell = cellIterator.next();
				if(myRow.getRowNum()<1 || ((myCell.getColumnIndex()==0) || (myCell.getColumnIndex()>=4)))
				{
					logger.info("Skipping row/column: {"+myRow.getRowNum()+","+myCell.getColumnIndex()+"}");
					
				}
				else
				{
					logger.info("row/column: {"+myRow.getRowNum()+","+myCell.getColumnIndex()+"}");
					//2 is the 3rd column.
					System.out.println(myCell.getStringCellValue().trim());
					if(((myRow.getRowNum()>=1)) && myCell.getColumnIndex()==2)
					{
						logger.debug("Reading Cell: {"+myRow.getRowNum()+","+myCell.getColumnIndex()+"}");
						// This is the jobset name that goes into the top box.
						String jmoQuestionJob=myCell.getStringCellValue().trim();
						if(jmoQuestionJob.equals(jobName))
						{
							foundConditionJob=true;
							logger.debug("Job in question Identified :"+jmoQuestionJob);
						}
						
					}
					// Column 1 is not useful for us. Skip this one 
					if(((myRow.getRowNum()>=1)) && myCell.getColumnIndex()==3 && foundConditionJob==true)
					{
						logger.debug("Reading Cell: {"+myRow.getRowNum()+","+myCell.getColumnIndex()+"}");
						// This is the jobset name that goes into the top box.
						jmoCrossReferencedName=myCell.getStringCellValue().trim();
						logger.debug("Cross Referenced Job Identified :"+jmoCrossReferencedName);
						
					}
					
				}
				
			}
			
		}
		
		
		
		return jmoCrossReferencedName;
	}
	public void readExcel(String excelInput,String sheetName) throws IOException, EncryptedDocumentException, InvalidFormatException
	{
		logger=Logger.getLogger("JilUtilities.ExcelUtils.readExcel");
		logger.info("Reading Excel File: "+excelInput);
		//File file = new File(excelInput);
		//FileInputStream excelInputStream = new FileInputStream(file);
		Workbook excelWorkbook = null;
		String fileExtension=excelInput.substring(excelInput.indexOf("."));
		
		excelWorkbook=WorkbookFactory.create(new File(excelInput));
		
		Sheet myExcelSheet = excelWorkbook.getSheet(sheetName);
		String jobsetName="";
		String topBoxName="";
		String startTime="";
		String topBoxCalendar="";
		// Read each row and column
		Iterator<Row> rowIterator = myExcelSheet.iterator();
		while(rowIterator.hasNext())
		{
			Row myRow=rowIterator.next();
			Iterator<Cell> cellIterator=myRow.cellIterator();
			while(cellIterator.hasNext())
			{
				Cell myCell = cellIterator.next();
				if(myRow.getRowNum()==1 || (myCell.getColumnIndex()==0) || (myCell.getColumnIndex()>=5))
				{
					logger.info("Skipping row/column: {"+myRow.getRowNum()+","+myCell.getColumnIndex()+"}");
					
				}
				else
				{
					
					// Column 1 is not useful for us. Skip this one 
					if(((myRow.getRowNum()>1)) && myCell.getColumnIndex()==1)
					{
						logger.debug("Reading Cell: {"+myRow.getRowNum()+","+myCell.getColumnIndex()+"}");
						// This is the jobset name that goes into the top box.
						jobsetName=myCell.getStringCellValue().trim();
						logger.debug("Jobset Identified :"+jobsetName);
						
					}
					if(((myRow.getRowNum()>1)) && myCell.getColumnIndex()==2)
					{
						logger.debug("Reading Cell: {"+myRow.getRowNum()+","+myCell.getColumnIndex()+"}");
						// This is the top box name
						topBoxName=myCell.getStringCellValue();
						logger.debug("Jobset "+jobsetName+" goes into Top Box: "+topBoxName);
					}
					if(((myRow.getRowNum()>1)) && myCell.getColumnIndex()==3)
					{
						logger.debug("Reading Cell: {"+myRow.getRowNum()+","+myCell.getColumnIndex()+"}");
						// This is the start time
						startTime=Double.toString((int) myCell.getNumericCellValue());
						logger.debug("Top Box: "+topBoxName+" starts at "+startTime);
					}
					if(((myRow.getRowNum()>1)) && myCell.getColumnIndex()==4)
					{
						logger.debug("Reading Cell: {"+myRow.getRowNum()+","+myCell.getColumnIndex()+"}");
						// This is the calendar name
						topBoxCalendar=myCell.getStringCellValue();
						logger.debug("Top Box: "+topBoxName+" starts at "+startTime+" and uses calendar: "+topBoxCalendar);
					}
				}
				/*logger.info("Row Number: "+myRow.getRowNum());
				logger.info("Col Number: "+myCell.getColumnIndex());
				logger.info("Cell Value: "+myCell.getStringCellValue());*/
				
				
			}
		}
	}

}
