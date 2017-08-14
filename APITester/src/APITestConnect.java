import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.ca.autosys.services.AsApi;
import com.ca.autosys.services.AsConstants;

public class APITestConnect {

	static Logger logger = Logger.getRootLogger();
	public static void main(String[] args) 
	{
		String log4jLocation = "resources/log4j.properties";
		PropertyConfigurator.configure(log4jLocation);
		logger=Logger.getLogger("APITester.main");
		AsApi appServer = new AsApi("LUMOS",9000,AsConstants.ENCRYPTION_TYPE_DEFAULT);
		logger.info(appServer.authenticateWithPassword("daddepalli","LUMOS.RMT.com" , "Deek5581"));
		// TODO Auto-generated method stub

	}

}
