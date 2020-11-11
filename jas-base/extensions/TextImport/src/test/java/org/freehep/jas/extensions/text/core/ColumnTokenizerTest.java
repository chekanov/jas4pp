package org.freehep.jas.extensions.text.core;

import junit.framework.*;
import org.freehep.jas.extensions.text.core.ColumnTokenizer;

/**
 *
 * @author Tony Johnson
 */
public class ColumnTokenizerTest extends TestCase
{
   
   public ColumnTokenizerTest(java.lang.String testName)
   {
      super(testName);
   }
   
   public static Test suite()
   {
      TestSuite suite = new TestSuite(PatternTokenizerTest.class);
      return suite;
   }
   
   /**
    * Test of nextToken method, of class org.freehep.jas.extensions.text.aida.PatternTokenizer.
    */
   public void testNextToken()
   {
      for (int i=0; i<bounds.length; i++)
      {
         ColumnTokenizer tokenizer = new ColumnTokenizer(bounds[i]);
         for (int j=0; j<input[i].length; j++)
         {
            tokenizer.setLine(input[i][j]);
            for (int k=0; k<output[i][j].length; k++)
            {
               assertEquals(output[i][j][k],tokenizer.nextToken());
            }
            assertNull(tokenizer.nextToken());
            assertNull(tokenizer.nextToken());
         }
      }
   }
   
   private int[][] bounds = { {0,4,7} };
   private String[][] input = {{ "123 abc def" } , {""}};
   private String[][][] output = { { {"123"," abc"," def"}, {} } };
}
