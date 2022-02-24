/**
 * 
 */
import java.lang.Thread;
import java.math.BigInteger;
import java.util.Random;

/**
 * @author cfahim
 *
 */
public class MillerRabinThread extends Thread {
  
 public static boolean verbose = false;
  
  private BigInteger m_min;
  private BigInteger m_max;
  private BigInteger m_prime;
  private long m_2factor;
  private BigInteger m_oddfactor;
  private boolean m_isprime;  
  private long m_steps;
  private final BigInteger TWO = BigInteger.valueOf(2);

  
  public MillerRabinThread(BigInteger bi, long factor, BigInteger oddfactor, long steps, BigInteger min, BigInteger max)
  {   
    this.setprime(bi);  
    this.m_steps = steps;
    this.m_2factor = factor;
    this.m_oddfactor = oddfactor;
    m_min = min;
    m_max = max;
    //this.m_steps = steps;
    //m_isprime = PrimalityTest();
  }
  
  /***
   * For Threading
   */
  public void run()
  {
    if(verbose)
      System.out.println("Running MRThread with " + m_prime.toString() + " as a thread");
    this.isPrime();
  }

  /***
   * Runs Miller Rabin Algorithm to test if m_prime is prime 
   * @return true if m_prime is prime
   */
  public boolean isPrime()
  {
    this.m_isprime = PrimalityTest();
    return m_isprime;
  }
  
  /***
   * Miller Rabin algorithm to test m_prime is prime or not
   * @return true if prime
   */
  private boolean PrimalityTest() {
    if(!m_prime.testBit(0))
      return false;
    if(m_prime.compareTo(TWO) == 0 || m_prime.compareTo(new BigInteger("3")) == 0)
      return true;
        
    //GetFactorization(m_prime.subtract(BigInteger.ONE));    
    
    return NormalPrimalityTest();        
  }
  

  private boolean NormalPrimalityTest() {
    for(long i = m_steps; i > 0; i--)
    {
      BigInteger rand = GetRandBigInt();
      if(verbose)
        System.out.println("Testing a=" + rand.toString() + " as a witness");
      BigInteger tester = rand.modPow(m_oddfactor, m_prime);
      
      if(tester.equals(BigInteger.ONE) || tester.equals(m_prime.subtract(BigInteger.ONE)))
      {
        continue;
      }
      else
      {
        boolean shouldrtn = true;
        for(long r = 1; r <= m_2factor - 1; r++)
        {
          tester = tester.modPow(TWO, m_prime);
          if(tester.equals(BigInteger.ONE))
          {
            if(verbose)
              System.out.println(rand.toString() + " is a witness to our prime");
            return false;
          }
          else if(tester.equals(m_prime.subtract(BigInteger.ONE)))
          {
            shouldrtn = false;
            break;
          }
            
        }
        if(shouldrtn) 
        {
          if(verbose)
            System.out.println(rand.toString() + " is a witness our number is composite");
          return false;
        }
      }
    }
    
    if(verbose)
      System.out.println("MR Could not find any witnesses, probably prime");
    return true;
  }

  /***
   * Get a random BigInteger less than m_prime
   * @return
   */
  private BigInteger GetRandBigInt(){
    BigInteger rtn = BigInteger.ZERO;
    
    do
    {
      rtn = new BigInteger(m_max.bitLength(),new Random());
    } while(rtn.compareTo(m_min) <= 0 || rtn.compareTo(m_max) >= 0);
    
    
    return rtn;
    /*
    String randBigIntstr = "";   
    
    Random rand = new Random();
    int numdigits = rand.nextInt(m_prime.bitLength()/8);    
    for(int i = 0; i < numdigits; i++)
      randBigIntstr += rand.nextInt(10);  
    
    
    BigInteger rtn = new BigInteger(randBigIntstr);   
    return rtn;
    */
  }
  
  /***
   * Sets the prime number to be tested
   * @param m_prime
   */
  public void setprime(BigInteger m_prime) {
    this.m_prime = m_prime;
  }

  /***
   * Gets the prime number to be tested
   * @return
   */
  public BigInteger getprime() {
    return m_prime;
  }

  public boolean GetIsPrime() {
    return m_isprime;
  }
}
