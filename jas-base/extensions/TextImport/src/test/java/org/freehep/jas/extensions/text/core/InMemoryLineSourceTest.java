package org.freehep.jas.extensions.text.core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import junit.framework.*;
import org.freehep.jas.extensions.text.core.BufferedLineSource;
import org.freehep.jas.extensions.text.core.InMemoryLineSource;
import org.freehep.jas.extensions.text.core.LineSource;

/**
 *
 * @author Tony Johnson
 */
public class InMemoryLineSourceTest extends TestCase
{
   private File file;
   private static final int MAXROWS = 1000;

   public InMemoryLineSourceTest(java.lang.String testName)
   {
      super(testName);
   }
   
   public static Test suite()
   {
      TestSuite suite = new TestSuite(InMemoryLineSourceTest.class);
      return suite;
   }
   
    /**
    * Test of close method, of class org.freehep.jas.extensions.text.aida.BufferedLineSource.
    */
   public void testClose() throws IOException
   {
      testClose(new InMemoryLineSource(new BufferedLineSource(file,false),MAXROWS));
   }
   
   private void testClose(LineSource source) throws IOException
   {
      source.close();
      source.close();      
   }
   
   /**
    * Test of getLine method, of class org.freehep.jas.extensions.text.aida.BufferedLineSource.
    */
   public void testGetLine() throws IOException
   {
      testGetLine(new InMemoryLineSource(new BufferedLineSource(file,false),MAXROWS));
   }

   private void testGetLine(LineSource source) throws IOException
   {
      Random random = new Random();
      for (int i=0; i<1000; i++)
      {
         int row = random.nextInt(MAXROWS);
         assertTrue(source.setRow(row));
         assertEquals(String.valueOf(row),source.getLine());
      }
      
      int offset = 30;
      source.setStartLine(offset);
      for (int i=0; i<1000; i++)
      {
         int row = random.nextInt(MAXROWS);
         assertTrue(source.setRow(row));
         assertEquals(String.valueOf(row+offset),source.getLine());
      }      

      offset = 15;
      source.setStartLine(offset);
      for (int i=0; i<1000; i++)
      {
         int row = random.nextInt(MAXROWS);
         assertTrue(source.setRow(row));
         assertEquals(String.valueOf(row+offset),source.getLine());
      } 
      
      source.close();
   }

   public void testRows() throws IOException
   {
      testRows(new InMemoryLineSource(new BufferedLineSource(file,false),MAXROWS));
   }

   private void testRows(LineSource source) throws IOException
   {
      int rows = source.rows(false);
      assertTrue(rows == MAXROWS || rows == source.UNKNOWN);
      assertEquals(source.rows(true),MAXROWS);
      source.close();
   }
   
   protected void tearDown() throws java.lang.Exception
   {
      file.delete();
   }
   
   protected void setUp() throws java.lang.Exception
   {
      file = File.createTempFile("xxx","dat");
      PrintWriter writer = new PrintWriter(new FileWriter(file));
      for (int i=0; i<MAXROWS*2; i++)
      {
         writer.println(i);
      }
      writer.close();
   }  
}
