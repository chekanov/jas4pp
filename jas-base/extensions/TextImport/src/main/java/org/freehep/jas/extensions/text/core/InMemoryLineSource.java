package org.freehep.jas.extensions.text.core;

/**
 *
 * @author Tony Johnson
 */
public class InMemoryLineSource implements LineSource
{
   private String[] buffer;
   private int offset;
   private String comment;
   private int linesCopied = 0;
   private int linesRead = 0;
   private LineSource source;
   private int rows = UNKNOWN; // Rows in the source!
   private int target = -1;
   
   public InMemoryLineSource(LineSource source, int maxLines)
   {
      this.source = source;
      int sourceRows = source.rows(false);
      if (sourceRows != UNKNOWN) 
      {
         if (sourceRows < maxLines) maxLines = sourceRows;
         rows = sourceRows;
      }
      buffer = new String[maxLines];
      linesCopied = 0;
   }
   private void gotoRow(int row)
   {
      while (row >= linesCopied)
      {
         if (linesCopied >= buffer.length) return;
         if (!source.setRow(linesRead+offset))
         {
            rows = linesRead+offset;
            return;
         }
         linesRead++;
         String line = source.getLine();
         if (comment != null && line.startsWith(comment)) continue;
         buffer[linesCopied++] = line;
      }
   }
   public void close()
   {
      buffer = null;
      source = null;
   }
   public String getLine()
   {
      if (target >= linesCopied) gotoRow(target);
      return buffer[target];  
   }
   
   public int rows(boolean forceCalculation)
   {
      if (rows == UNKNOWN && forceCalculation)
      {
         gotoRow(Integer.MAX_VALUE);
      }
     if (rows == UNKNOWN) return buffer.length;
     else return rows-offset;
   }
   
   public boolean setRow(int row)
   {
      if (row < 0 || row >= buffer.length) return false;
      if (rows == UNKNOWN) gotoRow(row);
      if (rows!= UNKNOWN && row+offset >= rows) return false;
      target = row;
      return true;
   }
   
   public void setLineComment(String prefix)
   {
      if (prefix != null && prefix.length()==0) prefix = null;
      if (comment == null)
      {
         if (prefix == null) return;
         int nRemoved = 0;
         for (int i=0; i<linesCopied; i++)
         {
            if (buffer[i].startsWith(prefix)) nRemoved++;
            else if (nRemoved > 0) buffer[i-nRemoved] = buffer[i]; 
         }
         linesCopied -= nRemoved;
      }
      else
      {
         if (comment.equals(prefix)) return;
         linesCopied = 0;
         linesRead = 0;
      }
      comment = prefix;
   }
   
   public void setStartLine(int offset)
   {
      int diff = offset - this.offset;
      this.offset = offset;
      if (diff == 0) return;
      else if (diff > 0)
      {
         System.arraycopy(buffer,diff,buffer,0,linesCopied-diff);
         linesCopied -= diff;
         linesRead -= diff;
         if (linesCopied < 0) linesCopied = 0;
      }
      else
      {
         System.arraycopy(buffer,0,buffer,-diff,Math.min(linesCopied,buffer.length+diff));
         linesCopied -= diff;
         linesRead -= diff;
         if (linesCopied > buffer.length) linesCopied = buffer.length;
         for (int i=0, j=0; j<-diff; i++)
         {
            source.setRow(i+offset);
            String line = source.getLine();
            if (comment == null || !line.startsWith(comment)) buffer[j++] = line;
         }
      }
   }
}
