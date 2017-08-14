package excelUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelReader 
{
	static Logger logger = Logger.getRootLogger();
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
