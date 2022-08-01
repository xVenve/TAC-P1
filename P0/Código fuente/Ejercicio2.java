import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;

// Determinar si un número es primo o no

public class Ejercicio2 {

    public static void main(String[] args) {
        Ejercicio2 ejercicio2 = new Ejercicio2();
        // BigInteger a = new BigInteger("479001599");

        long start = System.nanoTime();
        // System.out.println(a + " " + ejercicio2.isPrime(a));
        long end = System.nanoTime();
        System.out.println("Time: " + (end - start));

        new File("outputEj2.txt");

        FileWriter write;
        try {
            // BigInteger max = new BigInteger("9223372036854775807");
            write = new FileWriter("outputEj2.txt");
            Random rand = new Random();
            for (int i = 1; i < 64; i++) {
                for (int j = 0; j < 10; j++) {
                    BigInteger x = new BigInteger(i, rand);
                    while (x.compareTo(BigInteger.valueOf((long) Math.pow(2, i - 1))) == -1) {
                        x = new BigInteger(i, rand);
                    }
                    start = System.nanoTime();
                    ejercicio2.isPrime(x);
                    end = System.nanoTime();

                    System.out.println(i + "; " + (end - start) + "; " + x);
                    write.write(x + "; " + (end - start) + "\n");
                }

            }

            write.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isPrime(BigInteger n) {
        if (n.compareTo(BigInteger.valueOf(1)) < 1)
            return false; // 1

        for (BigInteger i = BigInteger.valueOf(2); i.compareTo(n.divide(BigInteger.valueOf(2))) < 1; i = i
                .add(BigInteger.valueOf(1))) { // n
            if (n.mod(i).compareTo(BigInteger.valueOf(0)) == 0)
                return false; // 1
        }

        return true;
    }

}
