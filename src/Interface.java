/**
 * 
 */
import java.math.BigInteger;

/**
 * @author cfahim
 *
 */
public class Interface {
  public static boolean verbose = false;
  
  private boolean m_printcert;
  public boolean m_isprime;
  
  private BigInteger toTest;
  private AKS AKSThread;
  private MillerRabin MRThread;
  
  private final BigInteger MRBOUND = new BigInteger("341550071728321");
  private final BigInteger AKSBOUND = BigInteger.valueOf(33554432); // = 2^25
  
  // Does this method create the BigInteger?? 
  public Interface(BigInteger n, boolean cert)
  {
    //MillerRabin.verbose = Interface.verbose;
    //AKS.verbose = Interface.verbose;
    this.m_isprime = false;
    this.m_printcert = cert;
    this.toTest = n; // number(as a BigInteger) to test, 
  }
  
  public boolean isPrime()
  {
    
    // AKS only
    if(this.toTest.compareTo(AKSBOUND) < 0)
    {
      VerbosePrint("n is small, running AKS only");
      if(m_printcert)
        AKS.verbose = true;
      AKSThread = new AKS(this.toTest);
      m_isprime = AKSThread.isPrime();
      VerbosePrint("AKS says " + m_isprime);
    }
    // Miller-Robin only
    else if(this.toTest.compareTo(MRBOUND) < 0)
    {
      VerbosePrint("n is in MRBOUND, running MR");
      MRThread = new MillerRabin(this.toTest,1); //steps don't matter in this case MR will choose best
      m_isprime = MRThread.isPrime();
      VerbosePrint("MR says " + m_isprime);
    }
    else
      // Both (Miller-Robin and AKS)
    {
      VerbosePrint("n is large, starting threads");
      m_isprime = StartThreads();
      VerbosePrint("Threads says " + m_isprime);
    }
    
    if(!m_isprime && m_printcert)
    {
      TryFactorization(toTest);
    }
    return m_isprime;
  }
  
  private void TryFactorization(BigInteger toTest2) {
    if(toTest2.mod(BigInteger.valueOf(2)) == BigInteger.ZERO)
    {
      System.out.println("2 * " + toTest2.divide(BigInteger.valueOf(2)));
      return;
    }
    
    BigInteger TWO = BigInteger.valueOf(2);
    for(BigInteger i = BigInteger.valueOf(3); i.compareTo(toTest2) < 0; i = i.add(TWO))
    {
      //BigIntSqrt(toTest2);
      if(toTest2.mod(i) == BigInteger.ZERO)
      {
        System.out.println(i.toString() + " * " + toTest2.divide(i).toString());
        return;
      }
    }
    
  }
  
  /*
  private BigInteger BigIntSqrt()
  {
    BigInteger TWO = BigInteger.valueOf(2);
    BigInteger rtn = toTest.divide(TWO);

    BigInteger curr = (n.divide(rtn).add(rtn)).divide(TWO);
    BigInteger next = (n.divide(curr).add(curr)).divide(TWO);
    while(curr.subtract(next).abs().compareTo(BigInteger.ONE) != 0)
    {
      curr = next;
      next = (n.divide(curr).add(curr)).divide(TWO);
    } 
    //Compute result = ((n/g) + g)/2. Let g be the result just computed.
    //Repeat step 2 until the last two results obtained are the same. 
    
    return curr;
  }*/

  private boolean StartThreads()
  {
    AKSThread = new AKS(this.toTest);
    if(verbose)
      System.out.println("Starting AKS thread with: " + this.toTest.toString());
    AKSThread.start();
    
    MRThread = new MillerRabin(toTest, (2*(long)Math.pow(toTest.bitLength(),2)));
    if(verbose)
      System.out.println("Starting MR thread with: " + this.toTest.toString());
    MRThread.start();
    
    boolean aksaliveprint = false;
    boolean mraliveprint = false;
    while(true)
    {
      if(!AKSThread.isAlive() && !aksaliveprint)
      {
        VerbosePrint("AKS says " + this.toTest.toString() + " is " + AKSThread.n_isprime);
        m_isprime = AKSThread.n_isprime;
        MRThread.stop();
        return m_isprime;
        //break;
      }
      if(!MRThread.isAlive() && !mraliveprint)
      {
        VerbosePrint("MR says " + this.toTest.toString() + " is " + MRThread.GetIsPrime());
        m_isprime = MRThread.GetIsPrime();
        //if(!MRThread.GetIsPrime())
        AKSThread.stop();
        return m_isprime;
        //mraliveprint = true;
        //break;
      }
      if(mraliveprint && aksaliveprint)
        break;
      
    }
    return false;
  }
  
  private void VerbosePrint(String msg)
  {
    if(verbose)
      System.out.println(msg);
  }

}
