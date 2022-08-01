import java.math.BigInteger;


public class Main {

  /**
   * @param args
   */
  public static void main(String[] args) {
    // TODO Auto-generated method stub    
    boolean cert = false;
    
    for(int i = 0; i < args.length; i++)
    {
      if(args[i].trim().equals("-c"))
        cert=true;
      if(args[i].trim().equals("-v"))
        Interface.verbose = true;
      if(args[i].trim().equals("-vs"))
      {
        Interface.verbose = true;
        MillerRabin.verbose = true;
        AKS.verbose = true;
        MillerRabinThread.verbose = true;
      }
    }
    
    try {
      java.io.BufferedReader stdin = new java.io.BufferedReader(new java.io.InputStreamReader(System.in));
      System.out.println("Type a new number:");
      String line = stdin.readLine();
      while(line != null)
      {
        //Interface.verbose = false;
        Interface i = new Interface(new BigInteger(line),cert);
        
        // System.out.println((i.isPrime() ? "1" : "0"));
        System.out.println((i.isPrime() ? "1 - PRIME" : "0 - NOT PRIME"));
        
        System.out.println("Type a new number:");
        line = stdin.readLine();
      }
    }
    catch(Exception e)
    {
      System.out.println(e.getMessage());
    }
    
    /*
    BigInteger totest = new BigInteger("1519380").pow(32768).add(BigInteger.ONE);
    MillerRabin mr = new MillerRabin(totest,10000); 
    if(mr.isPrime())
      System.out.println(totest.toString() + " is prime");
    else
      System.out.println(totest.toString() + " is NOT prime");
    */
    

  }

}

