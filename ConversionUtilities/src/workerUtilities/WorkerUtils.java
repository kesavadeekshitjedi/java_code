package workerUtilities;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

public class WorkerUtils 
{
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

}
