import static org.junit.Assert.*;

import java.math.BigInteger;

import junit.framework.Assert;

import org.junit.Test;

/**
 * 
 */

/**
 * @author Vincent
 *
 */
public class PolyTests 
{

	/**
	 * Test method for {@link Poly#Poly()}.
	 */
	@Test
	public void testPoly() 
	{
		Poly p = new Poly();
		assertEquals(p.degree(),0);
		assertEquals(p.coefficient(0).compareTo(BigInteger.ZERO),0);
	}

	/**
	 * Test method for {@link Poly#Poly(int)}.
	 */
	@Test
	public void testPolyInt() 
	{
		Poly p = new Poly(10);
		assertEquals(p.degree(),0);
		assertEquals(p.coefficient(0).compareTo(BigInteger.ZERO),0);
		assertEquals(p.monos.length,11);
	}

	/**
	 * Test method for {@link Poly#Poly(Poly)}.
	 */
	@Test
	public void testPolyPoly() 
	{
		Poly q = new Poly(BigInteger.ONE, 10);
		Poly p = new Poly(q);
		assertEquals(p.degree(),10);
		assertEquals(p.coefficient(10).compareTo(BigInteger.ONE),0);
	}

	/**
	 * Test method for {@link Poly#Poly(java.math.BigInteger, int)}.
	 */
	@Test
	public void testPolyBigIntegerInt() 
	{
		Poly p = new Poly(BigInteger.ONE, 10);
		assertEquals(p.degree(),10);
		assertEquals(p.coefficient(10).compareTo(BigInteger.ONE),0);
	}

	/**
	 * Test method for {@link Poly#degree()}.
	 */
	@Test
	public void testDegree() 
	{
		Poly p = new Poly(BigInteger.ONE, 10);
		assertEquals(p.degree(),10);

		Poly q = p.times(p);
		System.out.println("testDegree: " + q);
		assertEquals(q.degree(),20);
	}

	/**
	 * Test method for {@link Poly#toString()}.
	 */
	@Test
	public void testToString() 
	{
		Poly p = new Poly(BigInteger.ONE, 10);
		// System.out.println(p.toString());
		assertEquals(p.toString(),"x^10");
	}

	/**
	 * Test method for {@link Poly#equals(Poly)}.
	 */
	@Test
	public void testEqualsPoly() 
	{
		Poly p = new Poly(BigInteger.ONE, 10);
		Poly q = p.times(p).times(new BigInteger("5"));
		Poly r = new Poly(BigInteger.ONE, 5);
		Poly s = r.plus(p).times(q).plus(p.times(new BigInteger("3")));
		
		System.out.println("testEqualsPoly: s = " + s);
		
		Poly t = s.mod(new BigInteger("3"));
		System.out.println("testEqualsPoly: t = " + t);
		
		assertEquals(true,p.equals(p));
		assertEquals(true,q.equals(q));
		assertEquals(true,r.equals(r));
		assertEquals(true,s.equals(s));
		assertEquals(false,p.equals(q));
		assertEquals(false,p.equals(r));
		assertEquals(false,p.equals(s));
		assertEquals(false,q.equals(p));
		assertEquals(false,q.equals(r));
		assertEquals(false,q.equals(s));
		assertEquals(false,r.equals(p));
		assertEquals(false,r.equals(q));
		assertEquals(false,r.equals(s));
		assertEquals(false,s.equals(p));
		assertEquals(false,s.equals(q));
		assertEquals(false,s.equals(r));

	}

	/**
	 * Test method for {@link Poly#plus(Poly)}.
	 */
	@Test
	public void testPlus() 
	{
		Poly p = new Poly(BigInteger.ONE, 10);

		Poly q = p.plus(p);
		System.out.println("testPlus: q = " + q);
		
		assertEquals(q.degree(),10);
		assertEquals(q.coefficient(10),new BigInteger("2"));
		
		Poly r = new Poly(BigInteger.ONE, 20);
		Poly s = q.plus(r);
		System.out.println("testPlus: s = " + s);

		assertEquals(s.coefficient(10),new BigInteger("2"));
		assertEquals(s.coefficient(20),BigInteger.ONE);
		assertEquals(s.degree(),20);
	}

	/**
	 * Test method for {@link Poly#minus(Poly)}.
	 */
	@Test
	public void testMinus() 
	{
		Poly p = new Poly(BigInteger.ONE, 10);

		Poly q = p.minus(p);
		System.out.println("testMinus: q = " + q);
		
		assertEquals(q.degree(),0);
		assertEquals(q.coefficient(10),BigInteger.ZERO);
		
		Poly r = new Poly(BigInteger.ONE, 20);
		Poly s = q.minus(r);
		System.out.println("testMinus: s = " + s);

		assertEquals(s.coefficient(10),BigInteger.ZERO);
		assertEquals(s.coefficient(20),BigInteger.ONE.negate());
		assertEquals(s.degree(),20);
	}

	/**
	 * Test method for {@link Poly#updateDegree(Poly)}.
	 */
	@Test
	public void testUpdateDegree() 
	{
		Poly p = new Poly(BigInteger.ONE, 10);
		Poly q = p.times(p);

		q.degree = 0;
		Poly.updateDegree(q);
		assertEquals(20,q.degree());
		
		Poly r = new Poly(BigInteger.ONE, 5);
		Poly s = r.plus(p).times(q);

		s.degree = 99;
		Poly.updateDegree(s);
		assertEquals(30,s.degree());
	}

	/**
	 * Test method for {@link Poly#times(java.math.BigInteger)}.
	 */
	@Test
	public void testTimesBigInteger() 
	{
		Poly p = new Poly(BigInteger.ONE, 10);

		Poly q = p.times(new BigInteger("10"));
		System.out.println("testTimesBigInteger: q = " + q);
		
		assertEquals(10,q.degree());
		assertEquals(new BigInteger("10"),q.coefficient(10));
		
		Poly r = new Poly(BigInteger.ONE, 20);
		Poly s = r.times(new BigInteger("9"));
		System.out.println("testTimesBigInteger: s = " + s);

		assertEquals(BigInteger.ZERO,s.coefficient(10));
		assertEquals(new BigInteger("9"),s.coefficient(20));
		assertEquals(20,s.degree());
	}

	/**
	 * Test method for {@link Poly#times(Poly)}.
	 */
	@Test
	public void testTimesPoly() 
	{
		Poly p = new Poly(BigInteger.ONE, 10);

		Poly q = p.times(p);
		System.out.println("testTimesPoly: q = " + q);
		
		assertEquals(20,q.degree());
		assertEquals(BigInteger.ONE,q.coefficient(20));
		
		Poly r = new Poly(BigInteger.ONE, 5);
		Poly s = r.plus(p).times(q);
		System.out.println("testTimesPoly: s = " + s);

		assertEquals(BigInteger.ZERO,s.coefficient(10));
		assertEquals(BigInteger.ONE,s.coefficient(25));
		assertEquals(BigInteger.ONE,s.coefficient(30));
		assertEquals(30,s.degree());
	}

	/**
	 * Test method for {@link Poly#mod(java.math.BigInteger)}.
	 */
	@Test
	public void testModBigInteger() 
	{
		Poly p = new Poly(BigInteger.ONE, 10);
		Poly q = p.times(p).times(new BigInteger("5"));
		Poly r = new Poly(BigInteger.ONE, 5);
		Poly s = r.plus(p).times(q).plus(p.times(new BigInteger("3")));
		
		System.out.println("testModBigInteger: s = " + s);
		
		Poly t = s.mod(new BigInteger("3"));
		System.out.println("testModBigInteger: t = " + t);
		
		assertEquals(30,t.degree());
		assertEquals(BigInteger.valueOf(2), t.coefficient(30));
		assertEquals(BigInteger.valueOf(2), t.coefficient(25));
		assertEquals(BigInteger.ZERO, t.coefficient(10));
	}

	/**
	 * Test method for {@link Poly#mod(Poly)}.
	 * 
	 * http://www.usna.edu/Users/math/wdj/book/node74.html
	 */
	@Test
	public void testModPoly() 
	{
		Poly p = new Poly(BigInteger.ONE, 10);
		Poly q = p.times(p).times(new BigInteger("5"));
		Poly r = new Poly(BigInteger.ONE, 5);
		Poly s = r.plus(p).times(q).plus(p.times(new BigInteger("3")));
		Poly t = new Poly(BigInteger.ONE, 1);
		Poly u = new Poly(BigInteger.ONE, 2);
		
		System.out.println("testModPoly: s = " + s);
		System.out.println("testModPoly: t = " + t);
		System.out.println("testModPoly: u = " + u);

		Poly v = s.mod(t);
		Poly w = s.mod(u);

		System.out.println("testModPoly: v = " + v);
		System.out.println("testModPoly: w = " + w);
		
		assertEquals(0,v.degree());
		assertEquals(0,w.degree());
		
		Poly m = new Poly(BigInteger.ONE,2).minus(new Poly(BigInteger.ONE,0));
		System.out.println("testModPoly: m = " + m);
		
		Poly a = new Poly(new BigInteger("2"),2).mod(m);
		System.out.println("testModPoly: a = " + a);
		assertEquals(0,a.degree());
		assertEquals(new BigInteger("2"),a.coefficient(0));
		
		Poly b = new Poly(BigInteger.ONE,4).mod(m);
		System.out.println("testModPoly: b = " + b);
		assertEquals(0,b.degree());
		assertEquals(BigInteger.ONE,b.coefficient(0));
		
		Poly c = new Poly(BigInteger.ONE,3).mod(m);
		System.out.println("testModPoly: c = " + c);
		assertEquals(1,c.degree());
		assertEquals(BigInteger.ONE,c.coefficient(1));
		
		Poly d = new Poly(new BigInteger("7"),8).
				plus(new Poly(new BigInteger("3"),7)).
				minus(new Poly(new BigInteger("5"),6)).
				plus(new Poly(new BigInteger("2"),5)).
				minus(new Poly(new BigInteger("2"),4)).
				plus(new Poly(BigInteger.ONE,3)).
				minus(new Poly(new BigInteger("4"),2)).
				minus(new Poly(BigInteger.ONE,1)).
				plus(new Poly(new BigInteger("9"),0)).mod(m);
		System.out.println("testModPoly: d = " + d);
		assertEquals(1,d.degree());
		assertEquals(new BigInteger("5"),d.coefficient(1));
		assertEquals(new BigInteger("5"),d.coefficient(0));
	
		m = new Poly(BigInteger.ONE,2).plus(new Poly(BigInteger.ONE,0));
		System.out.println("testModPoly: m = " + m);
		
		a = new Poly(BigInteger.ONE,1).mod(m);
		assertEquals(1,a.degree());
		assertEquals(BigInteger.ONE,a.coefficient(1));
		System.out.println("testModPoly: a = " + a);
		
		a = new Poly(BigInteger.ONE,2).mod(m);
		assertEquals(0,a.degree());
		assertEquals(BigInteger.ONE.negate(),a.coefficient(0));
		System.out.println("testModPoly: a = " + a);

		a = new Poly(BigInteger.ONE,3).mod(m);
		assertEquals(1,a.degree());
		assertEquals(BigInteger.ONE.negate(),a.coefficient(1));
		System.out.println("testModPoly: a = " + a);
		
		a = new Poly(BigInteger.ONE,4).mod(m);
		assertEquals(0,a.degree());
		assertEquals(BigInteger.ONE,a.coefficient(0));
		System.out.println("testModPoly: a = " + a);

		a = new Poly(BigInteger.ONE,5).mod(m);
		assertEquals(1,a.degree());
		assertEquals(BigInteger.ONE,a.coefficient(1));
		System.out.println("testModPoly: a = " + a);
	}

	/**
	 * Test method for {@link Poly#mod(Poly)}.
	 */
	@Test
	public void testModBigPoly2() 
	{
		Poly p = new Poly(BigInteger.ONE, 13).plus(new Poly(BigInteger.ONE,0));
		Poly q = new Poly(BigInteger.ONE, 11).minus(new Poly(BigInteger.ONE,0));
		Poly r = new Poly(BigInteger.valueOf(13), 0);
		
		Poly t = p.mod(q);
		System.out.println("testModBigPoly2: t = " + t);
		
		assertEquals(2,t.degree());
		assertEquals(BigInteger.ONE, t.coefficient(2));
		assertEquals(BigInteger.ONE, t.coefficient(0));
		assertEquals(BigInteger.ZERO, t.coefficient(1));
	}

	@Test
	public void testModPow()
	{
		Poly p = new Poly(BigInteger.ONE, 13).plus(new Poly(BigInteger.ONE,0));
		Poly q = new Poly(BigInteger.ONE, 11).minus(new Poly(BigInteger.ONE,0));
		Poly r = new Poly(BigInteger.valueOf(13), 0);
		Poly s = p.mod(q);

		Poly t = new Poly(BigInteger.ONE, 1).plus(new Poly(BigInteger.ONE,0));
		Poly u = t.modPow(BigInteger.valueOf(13), q, BigInteger.valueOf(13));
		
		System.out.println("testModPow: s = " + s);
		System.out.println("testModPow: u = " + u);
		System.out.println("testModPow: s.degree = " + s.degree());
		System.out.println("testModPow: u.degree = " + u.degree());
		
		s.printMonos();
		u.printMonos();
		
		assertEquals(s,u);
	}
	
}
