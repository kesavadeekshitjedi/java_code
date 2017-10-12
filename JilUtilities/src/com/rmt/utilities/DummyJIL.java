package com.rmt.utilities;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class DummyJIL 
{
	public void createDummyJil(int jilCount, String command, String machine) throws IOException
	{
		FileWriter jilWriter = new FileWriter("jil.txt");
		BufferedWriter jilBuffer = new BufferedWriter(jilWriter);
		
		
		for(int i=0;i<jilCount;i++)
		{
			if(i==0)
			{
				jilBuffer.write("insert_job: PERF_BOX"+i+ "\n");
				jilBuffer.write("job_type: BOX \n");
				jilBuffer.write("\n");
			}
			jilBuffer.write("insert_job: LD_job"+i+"\n");
			jilBuffer.write("command: "+command+"\n");
			jilBuffer.write("machine: "+machine+"\n");
			jilBuffer.write("group: PERF_TESTS_JIL_LOAD \n");
			jilBuffer.write("box_name: PERF_BOX \n");
			jilBuffer.write("\n");

			
		}
		jilBuffer.close();
		jilWriter.close();
		
		
	}

}
