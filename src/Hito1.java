import java.math.BigInteger;
import java.security.SecureRandom;
import java.lang.Thread;

public class Hito1 extends Thread{

    static boolean verbose = false;
	BigInteger n;
	boolean n_isprime;
	BigInteger factor;
	double timeelapsed;

    public boolean potenciaPerfecta(){
        BigInteger base = BigInteger.valueOf(2);
		BigInteger aSquared;
        do
		{
			BigInteger result;
			// Se elige power entre el máximo entre el logaritmo en base 2 de (n-base)-2 o 1
			int power = Math.max((int) (log() / log(base) - 2), 1);
			int comparison;

			do {
				power++;
				result = base.pow(power);
				comparison = n.compareTo(result); // n ? base^power
			}
			// Se van probando potencias de la base hasta que se tenga un número igual o mayor que n.
			while (comparison > 0 && power < Integer.MAX_VALUE);  //nº iteraciones ceil(log_b(n))

			// En el caso de que nuestra potencia sea igual a n, tenemos una potencia perfecta de n, por tanto n NO es primo.
			if (comparison == 0) {
				if (verbose)
					System.out.println(n + " is a perfect power of " + base);
				factor = base;
				n_isprime = false;
				return n_isprime;
			}

			// En caso contrario, no es potencia perfecta, podemos seguir buscando potencias perfectas con otras bases.
			if (verbose)
				System.out.println(n + " is not a perfect power of " + base);

			// Incrementamos la base en 1
			base = base.add(BigInteger.ONE);
			aSquared = base.pow(2);
		}
		// Continuacmos si la base al cuadrado no supera al numero que nos planteamos
		while (aSquared.compareTo(this.n) <= 0); // nº iteraciones sqrt(n)
		return false;


    }
    
    public BigInteger calculoR(){
        double log = this.log();
		double logSquared = log*log;
		BigInteger k = BigInteger.ONE;
		BigInteger r = BigInteger.ONE;
		do
		{
			r = r.add(BigInteger.ONE);
			if (verbose) System.out.println("trying r = " + r);
			k = multiplicativeOrder(r);
		}
		while( k.doubleValue() < logSquared );
		return r;
    }

    public boolean calculoMCD(BigInteger r){
        for( BigInteger i = BigInteger.valueOf(2); i.compareTo(r) <= 0; i = i.add(BigInteger.ONE) )
		{
			BigInteger gcd = n.gcd(i);
			if (verbose) System.out.println("gcd(" + n + "," + i + ") = " + gcd);
			if ( gcd.compareTo(BigInteger.ONE) > 0 && gcd.compareTo(n) < 0 )
			{
				factor = i;
				n_isprime = false;
				return false;
			}
		}
        return true;
    }

    double logSave = -1;

    double log()
	{
		if ( logSave != -1 )
			return logSave;
		
		// from http://world.std.com/~reinhold/BigNumCalcSource/BigNumCalc.java
		BigInteger b;
		
	    int temp = n.bitLength() - 1000;
	    if (temp > 0) 
	    {
	    	b=n.shiftRight(temp); 
	        logSave = (Math.log(b.doubleValue()) + temp)*Math.log(2);
	    }
	    else 
	    	logSave = (Math.log(n.doubleValue()))*Math.log(2);

	    return logSave;
	}

	
	/**
	 * log base 2 method that takes a parameter
	 * @param x
	 * @return
	 */
	double log(BigInteger x)
	{
		// from http://world.std.com/~reinhold/BigNumCalcSource/BigNumCalc.java
		BigInteger b;
		
	    int temp = x.bitLength() - 1000;
	    if (temp > 0) 
	    {
	    	b=x.shiftRight(temp); 
	        return (Math.log(b.doubleValue()) + temp)*Math.log(2);
	    }
	    else 
	    	return (Math.log(x.doubleValue())*Math.log(2));
	}
    BigInteger multiplicativeOrder(BigInteger r)
	{
		// TODO Consider implementing an alternative algorithm http://rosettacode.org/wiki/Multiplicative_order
		BigInteger k = BigInteger.ZERO;
		BigInteger result;
		
		do
		{
			k = k.add(BigInteger.ONE);
			result = this.n.modPow(k,r);
		}
		while( result.compareTo(BigInteger.ONE) != 0 && r.compareTo(k) > 0);
		
		if (r.compareTo(k) <= 0)
			return BigInteger.ONE.negate();
		else
		{
			if (verbose) System.out.println(n + "^" + k + " mod " + r + " = " + result);
			return k;
		}
	}

    public static void main(String[] args) {
        Hito1 miclase = new Hito1();
        SecureRandom rand = new SecureRandom();

        for (int bits = 2; bits <= 64; bits += 1) {
            for (int i = 0; i < 10; i++) {
                miclase.n = BigInteger.probablePrime(bits, rand);

                double startpot = System.nanoTime();
                miclase.potenciaPerfecta();
                double timeelapsedpot = System.nanoTime() - startpot;

                double startr = System.nanoTime();
                BigInteger r = miclase.calculoR();
                double timeelapsedr = System.nanoTime() - startr;

                double startMCD = System.nanoTime();
                miclase.calculoMCD(r);
                double timeelapsedMCD = System.nanoTime() - startMCD;

                System.out.println(miclase.n + ";" + timeelapsedpot + ";" + timeelapsedr + ";" + timeelapsedMCD);
            }
        }
        
    }
}