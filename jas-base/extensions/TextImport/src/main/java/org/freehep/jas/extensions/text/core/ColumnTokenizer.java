package org.freehep.jas.extensions.text.core;

/**
 * 
 * @author Tony Johnson
 */
public class ColumnTokenizer implements Tokenizer
{
   private int[] bounds;
   private int column;
   private String line;
   
   public ColumnTokenizer(int[] bounds)
   {
      this.bounds = bounds;
   }
   
   public String nextToken()
   {
      if (column > bounds.length) return null;
      else if (column == bounds.length) return line.substring(bounds[column++]);
      else return line.substring(bounds[column++], bounds[column]);
   }
   
   public void setLine(CharSequence in)
   {
      line = in.toString();
      column = 0;
   }
}
