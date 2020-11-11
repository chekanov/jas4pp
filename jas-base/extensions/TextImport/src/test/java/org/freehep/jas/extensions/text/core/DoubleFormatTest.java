package org.freehep.jas.extensions.text.core;

import java.util.Random;
import junit.framework.*;
import org.freehep.jas.extensions.text.core.DoubleFormat;
import org.freehep.util.Value;

/**
 *
 * @author Tony Johnson
 */
public class DoubleFormatTest extends TestCase
{
   
   public DoubleFormatTest(java.lang.String testName)
   {
      super(testName);
   }
   
   public static Test suite()
   {
      TestSuite suite = new TestSuite(DoubleFormatTest.class);
      return suite;
   }
   
   /**
    * Test of check method, of class org.freehep.jas.extensions.text.core.DoubleFormat.
    */
   public void testCheck()
   {
      DoubleFormat format = new DoubleFormat();
      assertTrue(format.check("12345"));
      assertTrue(format.check("  12345   "));
      assertTrue(format.check(" -123 "));
      assertTrue(format.check("1.23"));
      assertTrue(format.check("1.56"));
      assertTrue(format.check("1.e-56"));
      assertTrue(format.check(".1e-56"));
      assertTrue(format.check("-1.e-56"));
      assertTrue(format.check("-1.234e56"));
      assertTrue(format.check("  -1.234e56  "));

      assertFalse(format.check(""));
      assertFalse(format.check(" "));
      assertFalse(format.check(".e-56"));
      assertFalse(format.check("-1e-5.6"));
      assertFalse(format.check("1.2.3"));
      assertFalse(format.check("abc"));
      assertFalse(format.check("-1.234e56d"));
      assertFalse(format.check(null));
      assertFalse(format.check("1-34"));
      
      Random r = new Random();
      for (int i=0; i<10000; i++)
      {
         assertTrue(format.check(String.valueOf(1e20*r.nextGaussian())));
      }
   }
   
   /**
    * Test of getName method, of class org.freehep.jas.extensions.text.core.DoubleFormat.
    */
   public void testGetName()
   {
      DoubleFormat format = new DoubleFormat();
      assertEquals("Double",format.getName());
   }
   
   /**
    * Test of parse method, of class org.freehep.jas.extensions.text.core.DoubleFormat.
    */
   public void testParse()
   {
      DoubleFormat format = new DoubleFormat();
      Value v = new Value();
      format.parse(v,"12345");
      assertEquals(v.getDouble(),12345,1e-10);
      format.parse(v," -123 ");
      assertEquals(v.getDouble(),-123,1e-10);
      format.parse(v,"  12345   ");
      assertEquals(v.getDouble(),12345,1e-10);
      format.parse(v,"1.23");
      assertEquals(v.getDouble(),1.23,1e-10);
      format.parse(v,"1.56");
      assertEquals(v.getDouble(),1.56,1e-10);
      format.parse(v,"1.e-56");
      assertEquals(v.getDouble(),1.e-56,1e-66);
      format.parse(v,".1e-56");
      assertEquals(v.getDouble(),.1e-56,1e-66);
      format.parse(v,"-1.e-56");
      assertEquals(v.getDouble(),-1.e-56,1e-66);
      format.parse(v,"-1.234e56");
      assertEquals(v.getDouble(),-1.234e56,1e46);

      format.parse(v,"");
      assertTrue(Double.isNaN(v.getDouble())); 
      format.parse(v," ");
      assertTrue(Double.isNaN(v.getDouble())); 
      format.parse(v,".e-56");
      assertTrue(Double.isNaN(v.getDouble())); 
      format.parse(v,"-1e-5.6");
      assertTrue(Double.isNaN(v.getDouble())); 
      format.parse(v,"1.2.3");
      assertTrue(Double.isNaN(v.getDouble())); 
      format.parse(v,"1-34");
      assertTrue(Double.isNaN(v.getDouble())); 
      format.parse(v,"abc");
      assertTrue(Double.isNaN(v.getDouble())); 
      format.parse(v,"-1.234e56x");
      assertTrue(Double.isNaN(v.getDouble()));
      format.parse(v,null);
      assertTrue(Double.isNaN(v.getDouble())); 
     
      Random r = new Random();
      for (int i=0; i<10000; i++)
      {
         double d = 1e20*r.nextGaussian();
         format.parse(v,String.valueOf(d));
         assertEquals(v.getDouble(),d,Math.abs(d)*1e-10);
      }
   }
   
   /**
    * Test of getJavaClass method, of class org.freehep.jas.extensions.text.core.DoubleFormat.
    */
   public void testGetJavaClass()
   {
      DoubleFormat format = new DoubleFormat();
      assertEquals(Double.TYPE,format.getJavaClass());
   }
}
