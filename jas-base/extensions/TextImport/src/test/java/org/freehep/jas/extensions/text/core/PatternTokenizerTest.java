package org.freehep.jas.extensions.text.core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import junit.framework.*;
import org.freehep.jas.extensions.text.core.PatternTokenizer;

/**
 *
 * @author Tony Johnson
 */
public class PatternTokenizerTest extends TestCase
{
   
   public PatternTokenizerTest(java.lang.String testName)
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
      for (int i=0; i<pattern.length; i++)
      {
         PatternTokenizer tokenizer = new PatternTokenizer(Pattern.compile(pattern[i]));
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
   
   private String[] pattern = { "(\\S+)(?:\\s|$)" };
   private String[][] input = {{ "123 abc def" } , {""}};
   private String[][][] output = { { {"123","abc","def"}, {} } };
}
