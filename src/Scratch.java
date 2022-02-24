import java.math.BigInteger;
import java.security.SecureRandom;


public class Scratch {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		BigInteger n = BigInteger.probablePrime(64, new SecureRandom());
		System.out.println(n);
		AKS.verbose = true;
		System.out.println(new AKS(n).isPrime());
	}

}
