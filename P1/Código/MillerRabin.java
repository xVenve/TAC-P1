import java.math.BigInteger;
import java.util.Random;
import java.lang.Thread;

/**
 * 
 */

/**
 * @author cfahim
 *
 */
public class MillerRabin extends Thread {
  
  public static boolean verbose = false;
  
  
  private BigInteger m_prime;
  private long m_2factor;
  private BigInteger m_oddfactor;
  private boolean m_isprime;  
  private long m_steps;
  private final BigInteger TWO = BigInteger.valueOf(2);
  
  //taken from:
  /*
   * Miller-Rabin wikipedia
   * Pomerance, C.; Selfridge, J. L. & Wagstaff, S. S., Jr. (1980), "The pseudoprimes to 25á109", Mathematics of Computation 35 (151): 1003Ð1026, doi:10.2307/2006210
   * Jaeschke, Gerhard (1993), "On strong pseudoprimes to several bases", Mathematics of Computation 61 (204): 915Ð926, doi:10.2307/2153262
   */
  private final BigInteger FASTMAX = new BigInteger("341550071728321");
	
  public MillerRabin(BigInteger bi, long steps)
	{		
    this.setprime(bi);	
    this.m_steps = steps;//bi.min(BigInteger.valueOf(steps-1)).longValue();
    //this.m_steps = steps;
    //m_isprime = PrimalityTest();
	}
  
  /***
   * For Threading
   */
  public void run()
  {
    if(verbose)
      System.out.println("Running MR with " + m_prime.toString() + " as a thread");
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
        
    GetFactorization(m_prime.subtract(BigInteger.ONE));    
    
    if(m_prime.compareTo(FASTMAX) < 0)
      return FastPrimalityTest();
    else
    {
      if(!FastPrimalityTest())
        return false;
      else
        return ThreadPrimalityTest();
    }
      //return NormalPrimalityTest();        
  }
  

  private boolean ThreadPrimalityTest() {
    long newsteps = m_steps / 4;
    BigInteger portion = m_prime.divide(BigInteger.valueOf(4));
    
    MillerRabinThread MRT = new MillerRabinThread(m_prime,m_2factor,m_oddfactor,newsteps,BigInteger.ONE,portion);
    MillerRabinThread MRT1 = new MillerRabinThread(m_prime,m_2factor,m_oddfactor,newsteps,portion,portion.multiply(TWO));
    MillerRabinThread MRT2 = new MillerRabinThread(m_prime,m_2factor,m_oddfactor,newsteps,portion.multiply(TWO),portion.multiply(new BigInteger("3")));
    MillerRabinThread MRT3 = new MillerRabinThread(m_prime,m_2factor,m_oddfactor,newsteps,portion.multiply(new BigInteger("3")),m_prime);
    MRT.start();
    MRT1.start();
    MRT2.start();
    MRT3.start();
    
    do
    {
      if(!MRT.isAlive() && !MRT.GetIsPrime() ||
         !MRT1.isAlive() && !MRT1.GetIsPrime() ||
         !MRT2.isAlive() && !MRT2.GetIsPrime() ||
         !MRT3.isAlive() && !MRT3.GetIsPrime())
      {
        MRT.stop();
        MRT1.stop();
        MRT2.stop();
        MRT3.stop();
        return false;
      }
    }while(MRT.isAlive() || MRT1.isAlive() || MRT2.isAlive() || MRT3.isAlive());
    
    return (MRT.GetIsPrime() && MRT1.GetIsPrime() && MRT2.GetIsPrime() && MRT3.GetIsPrime());
  }

  private boolean FastPrimalityTest() {
    if(verbose)
      System.out.println("Running FastPrimalityTest");
    int a_list[] = {2,3,5,7,11,13,17};
    
    for(int i = 0; i < a_list.length; i++)
    {
      BigInteger rand = BigInteger.valueOf(a_list[i]);
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

  private boolean NormalPrimalityTest() {
    if(verbose)
      System.out.println("Prime too big, using normalprimalitytest");
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
    
    while(rtn.compareTo(BigInteger.ZERO) == 0)
      rtn = new BigInteger(m_prime.bitLength()-1,new Random()); 
    
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
   * Sets m_2factor and m_oddfactor such that divider = 2^m_2factor*m_oddfactor
   * @param divider
   * 
   */
  private void GetFactorization(BigInteger divider) {
    // TODO Auto-generated method stub
    if(verbose)
      System.out.println("MR getting 2^s * d");
    int rtn = 0;
    while(!divider.testBit(rtn))
    {
      rtn++;
      //divider = divider.divide(TWO);
    }
    this.m_2factor = rtn;
    this.m_oddfactor = divider.divide(BigInteger.valueOf((long)Math.pow(2,rtn)));
    if(verbose)
      System.out.println("MR s: " + rtn + " d:" + this.m_oddfactor.toString());
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
