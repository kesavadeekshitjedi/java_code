import com.ca.autosys.services.AsApi;
import com.ca.autosys.services.AsConstants;

public class ConnectionTest 
{
	public static void main(String args[])
	{
		AsApi appServer = new AsApi("rhel-ae-prd",9000,AsConstants.ENCRYPTION_TYPE_DEFAULT);
		System.out.println(appServer.authenticateWithPassword("mgodavarthi", "rhel-ae-prd", "Ejmswat1"));
		
	}

}
