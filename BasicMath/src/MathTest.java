import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class MathTest 
{
	int sqRootOfN;
	List<Integer> subPrimeList = new ArrayList<Integer>();
	static Logger logger = Logger.getRootLogger();
	public boolean isPrime(int n)
	{
		logger=Logger.getLogger("MathTests.isPrime");
		boolean isPrime=false;
		
		// eliminate the number if it is divisible by 2 and the number itself is not 2
		// find the square root of the number.
		// if the mod of n is 0 by any of the primes less than the square root of n then it is not a prime
		if(n%2!=0)
		{
			sqRootOfN = (int) Math.sqrt(n);
			for(int i=2;i<=sqRootOfN;i++)
			{
				if(n%i==0)
				{
					isPrime=false;
					break;
				}
				else
				{
					isPrime=true;
					
				}
			}
			logger.debug(sqRootOfN);
		}
		else if((n!=2 || n!=1))
		{
			isPrime=false;
			logger.info("not prime");
		}
		if(n==2 || n==1 || n==3)
		{
			isPrime=true;
		}
		logger.info("Prime: ("+n+"): "+isPrime);
		return isPrime;
		
	}

}
