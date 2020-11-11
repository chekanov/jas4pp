package org.freehep.jas.extensions.text.core;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;
import junit.framework.*;
import org.freehep.jas.extensions.text.core.BufferedLineSource;
import org.freehep.jas.extensions.text.core.ColumnFormat;
import org.freehep.jas.extensions.text.core.FormatManager;
import org.freehep.jas.extensions.text.core.LineSource;
import org.freehep.jas.extensions.text.core.PatternTokenizer;
import org.freehep.jas.extensions.text.core.Tokenizer;
import org.freehep.jas.extensions.text.core.TypeScanner;

/**
 *
 * @author Tony Johnson
 */
public class TypeScannerTest extends TestCase
{
   private File file;
   private LineSource source;
   private Tokenizer tokenizer;
   private ColumnFormat[] formats;
   private final static int MAXROWS = 1000;
   
   public TypeScannerTest(java.lang.String testName)
   {
      super(testName);
   }
   
   public static Test suite()
   {
      TestSuite suite = new TestSuite(TypeScannerTest.class);
      return suite;
   }
   
   /**
    * Test of scan method, of class org.freehep.jas.extensions.text.core.TypeScanner.
    */
   public void testScan()
   {
      TypeScanner scanner = new TypeScanner(formats);
      scanner.scan(source,tokenizer);

      assertEquals(scanner.getFormat(0).getJavaClass(),Double.TYPE);
      assertEquals(scanner.getFormat(1).getJavaClass(),Integer.TYPE);
      assertEquals(scanner.getFormat(2).getJavaClass(),Integer.TYPE);
      assertEquals(scanner.getFormat(3).getJavaClass(),Double.TYPE);
      assertEquals(scanner.getFormat(4).getJavaClass(),String.class);

      scanner = new TypeScanner(formats);
      scanner.scan(source,tokenizer,5);
      
      assertEquals(scanner.getFormat(0).getJavaClass(),Double.TYPE);
      assertEquals(scanner.getFormat(1).getJavaClass(),Integer.TYPE);
      assertEquals(scanner.getFormat(2).getJavaClass(),Integer.TYPE);
      assertEquals(scanner.getFormat(3).getJavaClass(),Double.TYPE);
      assertEquals(scanner.getFormat(4).getJavaClass(),String.class);
   }
   
   /**
    * Test of getColumnCount method, of class org.freehep.jas.extensions.text.core.TypeScanner.
    */
   public void testGetColumnCount()
   {
      TypeScanner scanner = new TypeScanner(formats);
      scanner.scan(source,tokenizer);
      assertEquals(scanner.getColumnCount(),5);
      
      scanner = new TypeScanner(formats);
      scanner.scan(source,tokenizer,5);
      assertEquals(scanner.getColumnCount(),5);
   }
   
   protected void tearDown() throws java.lang.Exception
   {
      source.close();
      file.delete();
   }
   
   protected void setUp() throws java.lang.Exception
   {
      file = File.createTempFile("xxx","dat");
      PrintWriter writer = new PrintWriter(new FileWriter(file));
      Random r = new Random();
      for (int i=0; i<MAXROWS; i++)
      {
         writer.print(r.nextGaussian());
         writer.print(' ');
         writer.print(r.nextInt());
         writer.print(' ');
         writer.print(r.nextInt());
         writer.print(' ');
         writer.print(r.nextDouble());
         writer.print(' ');
         writer.print("Hello");
         writer.println();
      }
      writer.close();
      source = new BufferedLineSource(file,false);
      tokenizer = new PatternTokenizer(Pattern.compile("(\\S+)"));
      formats = FormatManager.getAvailableFormats();
   }    
}
