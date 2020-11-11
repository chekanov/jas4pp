package org.freehep.jas.extensions.text.core;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;
import junit.framework.*;
import org.freehep.jas.extensions.text.core.BufferedLineSource;
import org.freehep.jas.extensions.text.core.LineSource;
import org.freehep.jas.extensions.text.core.PatternTokenizer;
import org.freehep.jas.extensions.text.core.TokenSource;
import org.freehep.jas.extensions.text.core.Tokenizer;

/**
 *
 * @author Tony Johnson
 */
public class TokenSourceTest extends TestCase
{
   private File file;
   private LineSource source;
   private Tokenizer tokenizer;
   private static final int MAXROWS=1000;
   private static final int MAXCOLS=100;
   
   public TokenSourceTest(java.lang.String testName)
   {
      super(testName);
   }
   
   public static Test suite()
   {
      TestSuite suite = new TestSuite(TokenSourceTest.class);
      return suite;
   }
   
   /**
    * Test of rows method, of class org.freehep.jas.extensions.text.core.TokenSource.
    */
   public void testRows()
   { 
      TokenSource ts = new TokenSource(source,tokenizer);
      assertEquals(ts.rows(true),MAXROWS);
   }
   
   /**
    * Test of columns method, of class org.freehep.jas.extensions.text.core.TokenSource.
    */
   public void testColumns()
   {
      TokenSource ts = new TokenSource(source,tokenizer);
      assertEquals(ts.columns(true),MAXCOLS);
   }
   
   /**
    * Test of setRow method, of class org.freehep.jas.extensions.text.core.TokenSource.
    */
   public void testSetRow()
   {
      TokenSource ts = new TokenSource(source,tokenizer);
      assertTrue(ts.setRow(MAXROWS/2));
      assertTrue(ts.setRow(MAXROWS-1));
      assertFalse(ts.setRow(MAXROWS));
      assertTrue(ts.setRow(MAXROWS/2));
      assertTrue(ts.setRow(MAXROWS-1));
      assertTrue(ts.setRow(0));
      assertFalse(ts.setRow(-1));
   }
   /**
    * Test of getToken method, of class org.freehep.jas.extensions.text.core.TokenSource.
    */
   public void testGetToken()
   {
      TokenSource ts = new TokenSource(source,tokenizer);
      Random random = new Random();
      for (int i=0; i<MAXROWS; i++)
      {
         int row = random.nextInt(MAXROWS);
         assertTrue(ts.setRow(row));
         for (int j=0; j<MAXCOLS; j++)
         {
            int col = random.nextInt(MAXCOLS);
            assertEquals(Integer.parseInt(ts.getToken(col)),row+col);
         }
      }
   }
   protected void tearDown() throws java.lang.Exception
   {
      source.close();
      file.delete();
   }
   
   protected void setUp() throws java.lang.Exception
   {
      Random random = new Random();
      file = File.createTempFile("xxx","dat");
      PrintWriter writer = new PrintWriter(new FileWriter(file));
      for (int i=0; i<MAXROWS; i++)
      {
         for (int j=0; j<MAXCOLS; j++)
         {
            writer.print(i+j);
            writer.print('\t');
         }
         writer.println();
      }
      writer.close();
      source = new BufferedLineSource(file,false);
      tokenizer = new PatternTokenizer(Pattern.compile("(\\S+)"));
   }   
}
