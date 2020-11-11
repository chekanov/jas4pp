package org.freehep.jas.extensions.text.core;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/**
 *
 * @author Tony Johnson
 */
public class TokenSource
{
   private LineSource source;
   private Tokenizer tokenizer;
   private List tokens = new ArrayList();
   private int currRow = LineSource.UNKNOWN;
   private int columns = LineSource.UNKNOWN;
   
   public TokenSource(LineSource source, Tokenizer tokenizer)
   {
      this.source = source;
      this.tokenizer = tokenizer;
   }
   public int rows(boolean forceCalculation)
   {
      return source.rows(forceCalculation);
   }
   public int columns(boolean forceCalculation)
   {
      if (columns == LineSource.UNKNOWN && forceCalculation)
      {
         columns = 0;
         for (int i=0; source.setRow(i); i++)
         {
            tokenizer.setLine(source.getLine());
            int n = 0;
            while (tokenizer.nextToken() != null) n++;
            if (n > columns) columns = n;
         }
         if (currRow != LineSource.UNKNOWN) setRow(currRow);
      }
      return columns;
   }
   public boolean setRow(int row)
   {
      if (!source.setRow(row)) return false;
      currRow = row;
      tokenizer.setLine(source.getLine());
      tokens.clear();
      return true;
   }
   public String getToken(int column)
   {
      while (tokens.size() <= column)
      {
         tokens.add(tokenizer.nextToken());
      }
      return (String) tokens.get(column);
   }   
}
