package org.freehep.jas.extensions.text.core;

import java.io.*;
import java.util.Random;
import java.util.zip.GZIPOutputStream;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.freehep.jas.extensions.text.core.BufferedLineSource;
import org.freehep.jas.extensions.text.core.LineSource;

/**
 *
 * @author Tony Johnson
 */
public class BufferedLineSourceTest extends TestCase
{
   private File file;
   private File cfile;
   private File gzFile;
   private static final int MAXROWS = 1000;
   
   public BufferedLineSourceTest(java.lang.String testName)
   {
      super(testName);
   }
   
   public static Test suite()
   {
      TestSuite suite = new TestSuite(BufferedLineSourceTest.class);
      return suite;
   }
   
   /**
    * Test of close method, of class org.freehep.jas.extensions.text.aida.BufferedLineSource.
    */
   public void testClose() throws IOException
   {
      testClose(new BufferedLineSource(file,false));
   }
   
   public void testCommentClose() throws IOException
   {
      LineSource source = new BufferedLineSource(cfile,false);
      source.setLineComment("#");
      testClose(source);
   }  
   
   public void testGZClose() throws IOException
   {
      testClose(new BufferedLineSource(gzFile,true));
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
      testGetLine(new BufferedLineSource(file,false));
   }
   public void testCommentGetLine() throws IOException
   {
      LineSource source = new BufferedLineSource(cfile,false);
      source.setLineComment("#");
      testGetLine(source);
   }
   public void testGZGetLine() throws IOException
   {
      testGetLine(new BufferedLineSource(gzFile,true));
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
         int row = random.nextInt(MAXROWS-offset);
         assertTrue(source.setRow(row));
         assertEquals(String.valueOf(row+offset),source.getLine());
      }      

      offset = 15;
      source.setStartLine(offset);
      for (int i=0; i<1000; i++)
      {
         int row = random.nextInt(MAXROWS-offset);
         assertTrue(source.setRow(row));
         assertEquals(String.valueOf(row+offset),source.getLine());
      } 
      
      source.close();
   }
   /**
    * Test of rows method, of class org.freehep.jas.extensions.text.aida.BufferedLineSource.
    */
   public void testRows() throws IOException
   {
      testRows(new BufferedLineSource(file,false));
   }
   public void testCommentRows() throws IOException
   {
      LineSource source = new BufferedLineSource(cfile,false);
      source.setLineComment("#");
      testRows(source);
   }
   public void testGZRows() throws IOException
   {
      testRows(new BufferedLineSource(gzFile,true));
   }
   private void testRows(LineSource source) throws IOException
   {
      assertEquals(source.rows(false),-1);
      assertTrue(source.setRow(MAXROWS/2));
      assertEquals(source.rows(false),-1);
      assertTrue(source.setRow(MAXROWS-1));
      assertFalse(source.setRow(MAXROWS));
      assertEquals(source.rows(false),MAXROWS);
      assertTrue(source.setRow(MAXROWS/2));
      assertEquals(source.rows(false),MAXROWS);
      assertTrue(source.setRow(MAXROWS-1));
      assertFalse(source.setRow(MAXROWS));
      source.close();
   }
   
   protected void tearDown() throws java.lang.Exception
   {
      file.delete();
      cfile.delete();
      gzFile.delete();
   }
   
   protected void setUp() throws java.lang.Exception
   {
      Random r = new Random();
      file = File.createTempFile("xxx","dat");
      cfile = File.createTempFile("xxx","dcc");
      gzFile = File.createTempFile("xxx","dgz");
      PrintWriter writer = new PrintWriter(new FileWriter(file));
      PrintWriter cWriter = new PrintWriter(new FileWriter(cfile));
      PrintWriter gzWriter = new PrintWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(gzFile))));
      for (int i=0; i<MAXROWS; i++)
      {
         while (r.nextDouble()<0.5) cWriter.println("# comment");
         writer.println(i);
         cWriter.println(i);
         gzWriter.println(i);
      }
      writer.close();
      cWriter.close();
      gzWriter.close();
   }
    
}
