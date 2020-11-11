package org.freehep.jas.extensions.text.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Scans a LineSource to decide what type should be used for each column
 * @author Tony Johnson
 */
public class TypeScanner
{  
   private List formats;
   private ArrayList types = new ArrayList();
   
   public TypeScanner(ColumnFormat[] formats)
   {
      this.formats = Arrays.asList(formats);
   }
   public void scan(LineSource source, Tokenizer tokenizer, int columns)
   {
      types.ensureCapacity(columns);
      for (int i=0; i<columns; i++) addColumn();
      scan(source,tokenizer);
   }
   public void scan(LineSource source, Tokenizer tokenizer)
   {
      for (int i=0; source.setRow(i); i++)
      {
         tokenizer.setLine(source.getLine());
         for (int j=0; j<types.size(); j++)
         {          
            String token = tokenizer.nextToken();
            FormatIterator iter = (FormatIterator) types.get(j);
            while (!iter.current().check(token)) iter.next();
         }
         for (;;)
         {
            String token = tokenizer.nextToken();
            if (token == null) break;
            FormatIterator iter = addColumn();
            if (i>0) while(!iter.current().check(null)) iter.next();
            while (!iter.current().check(token)) iter.next();
         }
      }
   }
   private FormatIterator addColumn()
   {
      FormatIterator i = new FormatIterator();
      types.add(i);
      return i;
   }
   public int getColumnCount()
   {
      return types.size();
   }
   public ColumnFormat getFormat(int columnIndex)
   {
      return ((FormatIterator) types.get(columnIndex)).current();
   }
   public ColumnFormat[] getFormats()
   {
      ColumnFormat[] result = new ColumnFormat[types.size()];
      for (int i=0; i<result.length; i++) result[i] = getFormat(i);
      return result;
   }
   private class FormatIterator
   {
      private int index;
      FormatIterator()
      {
         index = 0;
      }
      ColumnFormat current()
      {
         return (ColumnFormat) formats.get(index);
      }
      void next()
      {
         index++;
      }
   }
}
