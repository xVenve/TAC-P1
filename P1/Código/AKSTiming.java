import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * 
 */

/**
 * Time our AKS implementation
 * 
 * @author Vincent
 *
 */
public class AKSTiming 
{

	static int maxBits = 64;
	static int iterations = 32;
	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		
		SecureRandom r = new SecureRandom();
		
		for( int bits = 8; bits <= maxBits; bits += 2 )
		{
			long cumulativeSum;

			cumulativeSum = 0;
			System.out.print("Detecting primes....");
			for( int i = 0; i < iterations; i++ )
			{
				BigInteger n = BigInteger.probablePrime(bits, r);
				AKS a = new AKS(n);
				System.out.print(".");
				long start = System.currentTimeMillis();
				if( !a.isPrime() )
					System.out.print("Wrong answer! " + n);
				long end = System.currentTimeMillis();
				
				cumulativeSum += ((end-start)/1000);
			}
			System.out.println("Bits: " + bits + ", time = " + cumulativeSum/iterations + "s");
			
			cumulativeSum = 0;
			System.out.print("Detecting composites");
			for( int i = 0; i < iterations; i++ )
			{
				BigInteger n;
				do
				{
					n = new BigInteger(bits/2, r).multiply(new BigInteger(bits/2,r));
				} while( n.compareTo(BigInteger.ZERO) <= 0 );
				
				AKS a = new AKS(n);
				System.out.print(".");
				long start = System.currentTimeMillis();
				if( a.isPrime() )
					System.out.print("Wrong answer! " + n);
				long end = System.currentTimeMillis();
				
				cumulativeSum += ((end-start)/1000);
			}
			System.out.println("Bits: " + bits + ", time = " + cumulativeSum/iterations + "s");
		}

	}
	
}
