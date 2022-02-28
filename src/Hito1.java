import java.math.BigInteger;
import java.util.Random;
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
        String []list = {"99999995279", "99999995299", "99999995371", "99999995377", "99999995437", "99999995467", "99999995471", "99999995477", "99999995519", "99999995527", "99999995531", "99999995551", "99999995587", "99999995593", "99999995609", "99999995617", "99999995629", "99999995639", "99999995657", "99999995681", "99999995731", "99999995737", "99999995783", "99999995807", "99999995813", "99999995831", "99999995833", "99999995881", "99999995891", "99999995897", "99999995911", "99999995923", "99999996001", "99999996023", "99999996097", "99999996101", "99999996127", "99999996133", "99999996157", "99999996181", "99999996197", "99999996241", "99999996251", "99999996277", "99999996289", "99999996293", "99999996337", "99999996361", "99999996377", "99999996379", "99999996391", "99999996401", "99999996407", "99999996419", "99999996443", "99999996451", "99999996497", "99999996581", "99999996619", "99999996637", "99999996653", "99999996713", "99999996727", "99999996773", "99999996793", "99999996799", "99999996839", "99999996847", "99999996857", "99999996961", "99999996973", "99999996983", "99999997033", "99999997049", "99999997091", "99999997093", "99999997133", "99999997189", "99999997211", "99999997259", "99999997297", "99999997303", "99999997309", "99999997327", "99999997351", "99999997369", "99999997381", "99999997397", "99999997451", "99999997457", "99999997477", "99999997511", "99999997523", "99999997541", "99999997561", "99999997633", "99999997637", "99999997661", "99999997697", "99999997733", "99999997771", "99999997819", "99999997831", "99999997841", "99999997849", "99999997859", "99999997883", "99999997891", "99999997897", "99999997907", "99999997913", "99999997919", "99999998003", "99999998017", "99999998071", "99999998081", "99999998089", "99999998099", "99999998129", "99999998149", "99999998167", "99999998237", "99999998269", "99999998279", "99999998291", "99999998317", "99999998321", "99999998339", "99999998341", "99999998347", "99999998377", "99999998401", "99999998471", "99999998491", "99999998509", "99999998513", "99999998573", "99999998591", "99999998627", "99999998639", "99999998641", "99999998647", "99999998687", "99999998743", "99999998767", "99999998821", "99999998881", "99999998893", "99999998921", "99999998933", "99999998939", "99999998951", "99999998957", "99999998993", "99999999019", "99999999023", "99999999097", "99999999103", "99999999119", "99999999133", "99999999139", "99999999149", "99999999193", "99999999227", "99999999247", "99999999269", "99999999277", "99999999289", "99999999353", "99999999391", "99999999409", "99999999431", "99999999439", "99999999527", "99999999529", "99999999551", "99999999557", "99999999559", "99999999571", "99999999581", "99999999599", "99999999647", "99999999653", "99999999667", "99999999689", "99999999709", "99999999713","99999999731", "99999999761", "99999999763", "99999999769", "99999999821", "99999999829", "99999999833", "99999999851", "99999999871", "99999999907", "99999999943", "99999999947", "99999999977"}; 
        String generatedString = list[0];
        int min = Integer.parseInt(generatedString);
        for (int i =0; i<list.length; i++){
        	Random random = new Random();
        	int generated = random.nextInt(min*2 - min) + min;
            miclase.n = BigInteger.valueOf(generated);
            min = generated;
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
