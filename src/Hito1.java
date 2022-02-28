import java.math.BigInteger;
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
        String list[] = {"7789","7793","7817","7823","7829","7841","7853","7867","7873","7877","65563","65579","65581","65587","65599","65609","65617","65629","65633","65647","637369","637379","637409","637421","637423","637447","637459","637463","637471","637489","6346211","6346213","6346231","6346237","6346243","6346297","6346303","6346313","6346337","6346339","63435457","63435473","63435479","63435503","63435511","63435523","63435527","63435539","63435599","63435607","634336421","634336429","634336457","634336471","634336519","634336523","634336529","634336537","634336561","634336567","6343333387","6343333411","6343333417","6343333457","6343333459","6343333487","6343333561","6343333571","6343333603","6343333631"};
        for (int i =0; i<list.length; i++){
            miclase.n = new BigInteger(list[i]);
            double startpot = System.currentTimeMillis();
            miclase.potenciaPerfecta();
            double timeelapsedpot = System.currentTimeMillis() - startpot;
            double startr = System.currentTimeMillis();
            BigInteger r = miclase.calculoR();
            double timeelapsedr = System.currentTimeMillis() - startr;
            double startMCD = System.currentTimeMillis();
            miclase.calculoMCD(r);
            double timeelapsedMCD = System.currentTimeMillis() - startMCD;
            System.out.println(miclase.n + ";" + timeelapsedpot +  ";" + timeelapsedr + ";"+ timeelapsedMCD );
        }
    }
}
