package org.freehep.jas.extensions.text.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

/**
 *
 * @author Tony Johnson
 */
public class BufferedLineSource implements LineSource
{
   private File file;
   private boolean gzip;
   private int rows = UNKNOWN;
   private int currRow;
   private int targetRow;
   private BufferedReader reader;
   private String currLine;
   private String comment;
   private int offset = 0;
   
   public BufferedLineSource(File file, boolean gzip) throws IOException
   {
      //TODO: How to deal with comments
      this.file = file;
      this.gzip = gzip;
      start();
   }
   public void close() throws IOException
   {
      if (reader != null)
      {
         reader.close();
         reader = null;
      }
   }
   public void finalize()
   {
      try
      {
         close();
      }
      catch (IOException x)
      {}
   }
   private void start() throws IOException
   {
      currRow = -1;
      targetRow = -1;
      close();
      reader = gzip ? new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(file))))
                    : new BufferedReader(new FileReader(file));
   } 
   public String getLine()
   {
      try
      {
         if (currRow != targetRow)
         {
            gotoRow(targetRow);
         }
         return currLine;
      }
      catch (IOException x)
      {
         throw new RuntimeException("Unexpected IO exception",x);
      }
   }
   
   public int rows(boolean forceCalculation)
   {
      try
      {
         if (rows == UNKNOWN && forceCalculation)
         {
            gotoRow(Integer.MAX_VALUE);
         }
         if (rows == UNKNOWN) return rows;
         else
         {
            int result = rows - offset;
            if (result<0) result = 0;
            return result;
         }
      }
      catch (IOException x)
      {
         throw new RuntimeException("Unexpected IO exception",x);
      }
   }
   
   public boolean setRow(int row)
   {
      try
      {
         if (row < 0) return false;
         row += offset;
         if (rows < 0)
         {
            boolean rc = gotoRow(row);
            if (rc) targetRow = row;
            return rc;
         }
         else if (row >= rows)
         {
            return false;
         }
         else
         {
            targetRow = row;
            return true;
         }
      }
      catch (IOException x)
      {
         throw new RuntimeException("Unexpected IO exception",x);
      }
   }
   private boolean gotoRow(int row) throws IOException
   {
      if (row < currRow) start();
      while (row > currRow)
      {
         String line = reader.readLine();
         if (line == null)
         {
            if (rows < 0) rows = currRow + 1;
            return false;
         }
         else if (comment == null || !line.startsWith(comment))
         {
            currRow++;
            currLine = line;
         }
      }
      return true;
   }
   
   public void setLineComment(String prefix)
   {
      if (prefix != null && prefix.length()==0) prefix = null;
      this.comment = prefix;
      try
      {
         start();
      }
      catch (IOException x)
      {
         throw new RuntimeException("Unexpected IO exception",x);
      }
   }
   
   public void setStartLine(int offset)
   {
      if (offset < 0) throw new IllegalArgumentException();
      this.offset = offset;
   }
}