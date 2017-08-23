package eemUtils;

import org.apache.log4j.Logger;

import com.ca.eiam.SafeBackendServerException;
import com.ca.eiam.SafeContext;
import com.ca.eiam.SafeException;
import com.ca.eiam.SafePasswordException;
import com.ca.eiam.SafeSession;
import com.ca.eiam.SafeUser;

public class Connect2EEM 
{
	static Logger logger = Logger.getRootLogger();
	public void connectToEEM(String eemHost, String eemUser, String eemPass, String applicationName) throws SafePasswordException, SafeBackendServerException, SafeException
	{
		logger=Logger.getLogger("ConversionUtilities.eemUtilities.connectToEEM");
		SafeContext sc = new SafeContext();
		sc.setBackend(eemHost);
		SafeSession ss = sc.authenticateWithPassword(eemUser, eemPass);
		sc.attach(applicationName,ss);
		SafeUser user = new SafeUser();
		
		user.setContext(sc);
		
		user.soRetrieveByName("daddepalli");
		System.out.println("Group Details for user: "+user.getGroupQ());
		logger.info(user.getGroupQ());
	}

}
