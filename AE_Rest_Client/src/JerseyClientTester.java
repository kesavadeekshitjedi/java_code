import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.codehaus.jettison.json.JSONException;

public class JerseyClientTester {

	public static void main(String[] args) throws JSONException 
	{
		JerseyGetRequests jr = new JerseyGetRequests();
		try 
		{
			List<String> aeJobInfo = JerseyGetRequests.getJobDetails("RHEL7-AE-1","job1");
			for (int i=0;i<aeJobInfo.size();i++)
			{
				System.out.println(aeJobInfo.get(i));
			}
			//JerseyGetRequests.getAllJobs("RHEL7-AE-1");
			//JerseyGetRequests.getAllJobRunInfo("RHEL7-AE-1");
		} 
		catch (KeyManagementException | NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
