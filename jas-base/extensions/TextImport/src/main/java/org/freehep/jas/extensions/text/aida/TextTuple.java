package org.freehep.jas.extensions.text.aida;

import hep.aida.ITuple;
import hep.aida.ref.tuple.ReadOnlyAbstractTuple;
import java.io.IOException;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import org.freehep.jas.extensions.text.core.ColumnFormat;
import org.freehep.jas.extensions.text.core.ValueSource;
import org.freehep.util.Value;

/**
 * An AIDA tuple based on a text file.
 * This implementation does not buffer the file in memory, since there is
 * already a way within AIDA of creating an in-memory copy of a tuple.
 * @author Tony Johnson
 */
public class TextTuple extends ReadOnlyAbstractTuple
{
   private ValueSource data;
   private ColumnFormat[] format;
   private String[] names;
   private Map nameMap;
   private int curRow;
   private int nColumns;
   private int[] columnMap;
   
   /** Creates a new instance of TextTuple */
   public TextTuple(String name, ValueSource data, String[] names, ColumnFormat[] format, BitSet skip) throws IOException
   {
      super(name,"");
      this.data = data;
      this.format = format;
      this.names = names;
      
      nameMap = new HashMap();
      columnMap = new int[names.length];
      for (int i=0; i<names.length; i++)
      {
         if (!skip.get(i))
         { 
            nameMap.put(names[i],new Integer(nColumns));
            columnMap[nColumns++] = i;
         }
      }
      start();
   }
   
   public double columnMax(int index)
   {
      return Double.NaN;
   }
   
   public double columnMean(int index)
   {
      return Double.NaN;
   }
   
   public double columnMin(int index)
   {
      return Double.NaN;
   }
   
   public double columnRms(int index) throws java.lang.IllegalArgumentException
   {
      return Double.NaN;
   }
   
   public String columnName(int index)
   {
      return names[columnMap[index]];
   }
   
   public Class columnType(int index) throws java.lang.IllegalArgumentException
   {
      return format[columnMap[index]].getJavaClass();
   }
   
   public int columns()
   {
      return nColumns;
   }
   
   public int findColumn(String name) throws java.lang.IllegalArgumentException
   {
      Number n = (Number) nameMap.get(name);
      if (n == null) throw new IllegalArgumentException("Unknow column: "+name);
      return n.intValue();
   }
   
   public void columnValue(int param, Value value) {
       Class type = columnType(param);
       Value dataValue = data.getValue(columnMap[param]);
       if ( type == Integer.TYPE ) value.set( dataValue.getInt() );
       else if ( type == Short.TYPE ) value.set( dataValue.getShort() );
       else if ( type == Long.TYPE ) value.set( dataValue.getLong() );
       else if ( type == Float.TYPE ) value.set( dataValue.getFloat() );
       else if ( type == Double.TYPE ) value.set( dataValue.getDouble() );
       else if ( type == Boolean.TYPE ) value.set( dataValue.getBoolean() );
       else if ( type == Byte.TYPE ) value.set( dataValue.getByte() );
       else if ( type == Character.TYPE ) value.set( dataValue.getChar() );
       else if ( type == String.class ) value.set( dataValue.getString() );
       else value.set( dataValue.getObject() );
   }
    
   public String columnDefaultString( int column ) {
       return null;
   }
   
   public ITuple findTuple(int index)
   {
      return null; // nested tuples not supported
   }
   
   public boolean next()
   {
      curRow++;
      return data.setRow(curRow);
   }
   
   public int rows()
   {
      // FIXME: arg should be false, workaround for JAS-308
      return data.rows(true);
   }
   
   public void setRow(int row)
   {
      curRow = row;
      if (!data.setRow(curRow)) throw new IllegalArgumentException("Bad row: "+curRow);
   }
   
   public void skip(int rows)
   {
      curRow += rows;
      if (!data.setRow(curRow)) throw new IllegalArgumentException("Bad row: "+curRow);
   }
   
   public void start()
   {
      curRow = -1;
   }
}
