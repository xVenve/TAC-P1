import java.math.BigInteger;
import java.security.SecureRandom;
import java.lang.Thread;

public class Hito2G extends Thread {

    BigInteger n;
    boolean n_isprime;
    BigInteger factor;
    double timeelapsed;

    public BigInteger calculoR() {
        double log = this.log();
        double logSquared = log * log;
        BigInteger k = BigInteger.ONE;
        BigInteger r = BigInteger.ONE;
        do {
            r = r.add(BigInteger.ONE);
            k = multiplicativeOrder(r);
        } while (k.doubleValue() < logSquared);
        return r;
    }

    public boolean paso5(BigInteger r, int limit) {
        Poly modPoly = new Poly(BigInteger.ONE, r.intValue()).minus(new Poly(BigInteger.ONE, 0));
        Poly partialOutcome = new Poly(BigInteger.ONE, 1).modPow(n, modPoly, n);

        for (int i = 1; i <= limit; i++) {
            Poly polyI = new Poly(BigInteger.valueOf(i), 0);
            Poly outcome = partialOutcome.plus(polyI);
            Poly p = new Poly(BigInteger.ONE, 1).plus(polyI).modPow(n, modPoly, n);

            if (!outcome.equals(p)) {
                factor = BigInteger.valueOf(i);
                n_isprime = false;
                return n_isprime;
            }
        }

        n_isprime = true;
        return n_isprime;
    }

    public int paso5test(BigInteger r, int limit) {
        Poly modPoly = new Poly(BigInteger.ONE, r.intValue()).minus(new Poly(BigInteger.ONE, 0));
        Poly partialOutcome = new Poly(BigInteger.ONE, 1).modPow(n, modPoly, n);
        int t = 0;
        for (int i = 1; i <= limit; i++) {
            Poly polyI = new Poly(BigInteger.valueOf(i), 0);
            Poly outcome = partialOutcome.plus(polyI);
            Poly p = new Poly(BigInteger.ONE, 1).plus(polyI).modPow(n, modPoly, n);
            t++;
            if (!outcome.equals(p)) {
                factor = BigInteger.valueOf(i);
                n_isprime = false;
                return t;
            }
        }

        n_isprime = true;
        return t;
    }

    double logSave = -1;

    double log() {
        if (logSave != -1)
            return logSave;

        BigInteger b;

        int temp = n.bitLength() - 1000;
        if (temp > 0) {
            b = n.shiftRight(temp);
            logSave = (Math.log(b.doubleValue()) + temp) * Math.log(2);
        } else
            logSave = (Math.log(n.doubleValue())) * Math.log(2);

        return logSave;
    }

    BigInteger multiplicativeOrder(BigInteger r) {
        BigInteger k = BigInteger.ZERO;
        BigInteger result;

        do {
            k = k.add(BigInteger.ONE);
            result = this.n.modPow(k, r);
        } while (result.compareTo(BigInteger.ONE) != 0 && r.compareTo(k) > 0);

        if (r.compareTo(k) <= 0)
            return BigInteger.ONE.negate();
        else {
            return k;
        }
    }

    BigInteger totient(BigInteger n) {
        BigInteger result = n;

        for (BigInteger i = BigInteger.valueOf(2); n.compareTo(i.multiply(i)) > 0; i = i.add(BigInteger.ONE)) {
            if (n.mod(i).compareTo(BigInteger.ZERO) == 0)
                result = result.subtract(result.divide(i));

            while (n.mod(i).compareTo(BigInteger.ZERO) == 0)
                n = n.divide(i);
        }

        if (n.compareTo(BigInteger.ONE) > 0)
            result = result.subtract(result.divide(n));

        return result;

    }

    int totientest(BigInteger n) {
        BigInteger result = n;
        int t = 0;
        for (BigInteger i = BigInteger.valueOf(2); n.compareTo(i.multiply(i)) > 0; i = i.add(BigInteger.ONE)) {
            if (n.mod(i).compareTo(BigInteger.ZERO) == 0)
                result = result.subtract(result.divide(i));

            while (n.mod(i).compareTo(BigInteger.ZERO) == 0) {
                n = n.divide(i);
                t++;
            }

        }

        if (n.compareTo(BigInteger.ONE) > 0)
            result = result.subtract(result.divide(n));

        return t;

    }

    double log(BigInteger x) {
        // from http://world.std.com/~reinhold/BigNumCalcSource/BigNumCalc.java
        BigInteger b;

        int temp = x.bitLength() - 1000;
        if (temp > 0) {
            b = x.shiftRight(temp);
            return (Math.log(b.doubleValue()) + temp) * Math.log(2);
        } else
            return (Math.log(x.doubleValue()) * Math.log(2));
    }

    public boolean isPrime() {
        // If ( n = a^b for a in natural numbers and b > 1), output COMPOSITE
        BigInteger base = BigInteger.valueOf(2);
        BigInteger aSquared;

        do {
            BigInteger result;
            // Se elige power entre el máximo entre el logaritmo en base 2 de (n-base)-2 o 1
            int power = Math.max((int) (log() / log(base) - 2), 1);
            int comparison;

            do {
                power++;
                result = base.pow(power);
                comparison = n.compareTo(result); // n ? base^power
            }
            // Se van probando potencias de la base hasta que se tenga un número igual o
            // mayor que n.
            while (comparison > 0 && power < Integer.MAX_VALUE); // nº iteraciones ceil(log_b(n))

            // En el caso de que nuestra potencia sea igual a n, tenemos una potencia
            // perfecta de n, por tanto n NO es primo.
            if (comparison == 0) {
                factor = base;
                n_isprime = false;
                return n_isprime;
            }

            // En caso contrario, no es potencia perfecta, podemos seguir buscando potencias
            // perfectas con otras bases.

            // Incrementamos la base en 1
            base = base.add(BigInteger.ONE);
            aSquared = base.pow(2);
        }
        // Continuacmos si la base al cuadrado no supera al numero que nos planteamos
        while (aSquared.compareTo(this.n) <= 0); // nº iteraciones sqrt(n)

        // Find the smallest r such that o_r(n) > log^2 n
        // o_r(n) is the multiplicative order of n modulo r
        // the multiplicative order of n modulo r is the
        // smallest positive integer k with n^k = 1 (mod r).
        double log = this.log();
        double logSquared = log * log;
        BigInteger k = BigInteger.ONE;
        BigInteger r = BigInteger.ONE;
        do {
            r = r.add(BigInteger.ONE);
            k = multiplicativeOrder(r);
        } while (k.doubleValue() < logSquared);

        // If 1 < gcd(a,n) < n for some a <= r, output COMPOSITE
        for (BigInteger i = BigInteger.valueOf(2); i.compareTo(r) <= 0; i = i.add(BigInteger.ONE)) {
            BigInteger gcd = n.gcd(i);
            if (gcd.compareTo(BigInteger.ONE) > 0 && gcd.compareTo(n) < 0) {
                factor = i;
                n_isprime = false;
                return false;
            }
        }

        // If n <= r, output PRIME
        if (n.compareTo(r) <= 0) {
            n_isprime = true;
            return true;
        }

        // For i = 1 to sqrt(totient)log(n) do
        // if (X+i)^n <>�X^n + i (mod X^r - 1,n), output composite;

        // sqrt(totient)log(n)
        int limit = (int) (Math.sqrt(totient(r).doubleValue()) * this.log());
        // X^r - 1
        Poly modPoly = new Poly(BigInteger.ONE, r.intValue()).minus(new Poly(BigInteger.ONE, 0));
        // X^n (mod X^r - 1, n)
        Poly partialOutcome = new Poly(BigInteger.ONE, 1).modPow(n, modPoly, n);
        for (int i = 1; i <= limit; i++) {
            Poly polyI = new Poly(BigInteger.valueOf(i), 0);
            // X^n + i (mod X^r - 1, n)
            Poly outcome = partialOutcome.plus(polyI);
            Poly p = new Poly(BigInteger.ONE, 1).plus(polyI).modPow(n, modPoly, n);
            if (!outcome.equals(p)) {
                // (mod x^" + r + " - 1, " + n + ") failed");
                factor = BigInteger.valueOf(i);
                n_isprime = false;
                return n_isprime;
            }
        }

        n_isprime = true;
        return n_isprime;
    }

    public static void main(String[] args) {
        Hito2G miclase = new Hito2G();
        SecureRandom rand = new SecureRandom();
        double startr, timeelapsedr, startMCD, timeelapsedMCD;

        for (int bits = 2; bits <= 64; bits += 1) {
            timeelapsedr = 0;
            timeelapsedMCD = 0;
            for (int i = 0; i < 1000; i++) {
                miclase.n = BigInteger.probablePrime(bits, rand);
                for (int j = 0; j < 1000; j++) {
                    BigInteger r = miclase.calculoR();
                    startr = System.nanoTime();
                    BigInteger tot = miclase.totient(r);
                    timeelapsedr += System.nanoTime() - startr;
                    int limit = (int) (Math.sqrt(tot.doubleValue()) * miclase.log());
                    startMCD = System.nanoTime();
                    miclase.paso5(r, limit);
                    timeelapsedMCD += System.nanoTime() - startMCD;

                }

            }
            System.out.println(miclase.n + ";" + timeelapsedr / 1000000 + ";" + timeelapsedMCD / 1000000);
        }
    }
}