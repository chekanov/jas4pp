package org.freehep.jas.extensions.text.core;

import java.util.Random;
import junit.framework.*;
import org.freehep.jas.extensions.text.core.IntegerFormat;
import org.freehep.util.Value;

/**
 *
 * @author Tony Johnson
 */
public class IntegerFormatTest extends TestCase
{
   
   public IntegerFormatTest(java.lang.String testName)
   {
      super(testName);
   }
   
   public static Test suite()
   {
      TestSuite suite = new TestSuite(IntegerFormatTest.class);
      return suite;
   }
   
   /**
    * Test of check method, of class org.freehep.jas.extensions.text.core.IntegerFormat.
    */
   public void testCheck()
   {
      IntegerFormat format = new IntegerFormat();
      assertTrue(format.check("12345"));
      assertTrue(format.check("  12345   "));
      assertTrue(format.check(" -123 "));
      assertFalse(format.check(""));
      assertFalse(format.check(" "));
      assertFalse(format.check("1.23"));
      assertFalse(format.check("1-34"));
      assertFalse(format.check("1.56"));
      assertFalse(format.check("abc"));
      assertFalse(format.check("123x"));
      assertFalse(format.check(null));
      
      Random r = new Random();
      for (int i=0; i<10000; i++)
      {
         assertTrue(format.check(String.valueOf(r.nextInt())));
      }
   }
   
   /**
    * Test of getName method, of class org.freehep.jas.extensions.text.core.IntegerFormat.
    */
   public void testGetName()
   {
      IntegerFormat format = new IntegerFormat();
      assertEquals("Integer",format.getName());
   }
   
   /**
    * Test of parse method, of class org.freehep.jas.extensions.text.core.IntegerFormat.
    */
   public void testParse()
   {
      IntegerFormat format = new IntegerFormat();
      Value v = new Value();
      format.parse(v,"12345");
      assertEquals(v.getInt(),12345);
      format.parse(v," -123 ");
      assertEquals(v.getInt(),-123);
      format.parse(v,"  12345   ");
      assertEquals(v.getInt(),12345);     

      format.parse(v,"");
      assertEquals(v.getInt(),-999); 
      format.parse(v," ");
      assertEquals(v.getInt(),-999);
      format.parse(v,"1.23");
      assertEquals(v.getInt(),-999);
      format.parse(v,"1-34");
      assertEquals(v.getInt(),-999);
      format.parse(v,"1.56");
      assertEquals(v.getInt(),-999);
      format.parse(v,"abc");
      assertEquals(v.getInt(),-999);       
      format.parse(v,"123x");
      assertEquals(v.getInt(),-999);  
      format.parse(v,null);
      assertEquals(v.getInt(),-999);       
      
      Random r = new Random();
      for (int i=0; i<10000; i++)
      {
         int n = r.nextInt();
         format.parse(v,String.valueOf(n));
         assertEquals(v.getInt(),n);
      }
   }
   
   /**
    * Test of getJavaClass method, of class org.freehep.jas.extensions.text.core.IntegerFormat.
    */
   public void testGetJavaClass()
   {
      IntegerFormat format = new IntegerFormat();
      assertEquals(Integer.TYPE,format.getJavaClass());
   }
}
