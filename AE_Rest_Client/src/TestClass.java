import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public class TestClass 
{
	public static void main(String args[])
	{
		JerseyGetRequests jr = new JerseyGetRequests();
		try 
		{
			JerseyGetRequests.getJobDetails("LUMOS","job1");
			JerseyGetRequests.getAllJobs("LUMOS");
			JerseyGetRequests.getAllJobRunInfo("LUMOS");
		} 
		catch (KeyManagementException | NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
