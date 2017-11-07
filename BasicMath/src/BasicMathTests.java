import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class BasicMathTests 
{

	static Logger logger = Logger.getRootLogger();
	public static void main(String[] args) 
	{
		String log4jLocation = "resources/log4j.properties";
		PropertyConfigurator.configure(log4jLocation);
		logger=Logger.getLogger("BasicMathTests.main");
		MathTest mt = new MathTest();
		mt.isPrime(15);
		// TODO Auto-generated method stub

	}

}
