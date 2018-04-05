package MOGAClustering;

import java.util.Random;

/** Generate random integers and doubles in a certain range. */
public final class IntRandomRange 
{
	static Random m_Random;

	public IntRandomRange() 
	{
		m_Random = new Random();
	}
	
	public static final void main(String... aArgs)
	{
	  log("Generating random integers in the range 1..10.");
    
	  int START = 2;
	  int END = 10;
	  m_Random = new Random();
	  for (int idx = 1; idx <= 10; ++idx){
      generateRandomInteger(START, END);
    }
    log("Done.");
  }
	
	public static int generateRandomInteger(int aStart, int aEnd)
	{
	  if ( aStart > aEnd ) 
	  {
		  throw new IllegalArgumentException("Start cannot exceed End.");
	  }
	  //get the range, casting to long to avoid overflow problems
	  long range = (long)aEnd - (long)aStart + 1;
	  // compute a fraction of the range, 0 <= frac < range
	  long fraction = (long)(range * m_Random.nextDouble());
	  int randomNumber =  (int)(fraction + aStart);    
	  //log("Generated : " + randomNumber);
	  return randomNumber;
    }
	 
	public static double generateRandomDouble(double aStart, double aEnd)
	{
	  if ( aStart > aEnd ) 
	  {
		  throw new IllegalArgumentException("Start cannot exceed End.");
	  }
	  //get the range, casting to long to avoid overflow problems
	  long range = (long)aEnd - (long)aStart + 1;
	  // compute a fraction of the range, 0 <= frac < range
	  long fraction = (long)(range * m_Random.nextDouble());
	  double randomNumber =  fraction + aStart;    
	  //log("Generated : " + randomNumber);
	  return randomNumber;
    }

	private static void log(String aMessage)
	{
		System.out.println(aMessage);
	}
}