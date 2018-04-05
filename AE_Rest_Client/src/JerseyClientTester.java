import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public class JerseyClientTester {

	public static void main(String[] args) 
	{
		JerseyGetRequests jr = new JerseyGetRequests();
		try 
		{
			JerseyGetRequests.getJobDetails("RHEL7-AE-1","job1");
			JerseyGetRequests.getAllJobs("RHEL7-AE-1");
			JerseyGetRequests.getAllJobRunInfo("RHEL7-AE-1");
		} 
		catch (KeyManagementException | NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
