package org.freehep.jas.extensions.text.core;

import org.freehep.util.Value;

/**
 *
 * @author Tony Johnson
 */
public class ValueSource extends TokenSource
{
   private ColumnFormat[] formats;
   private Value value = new Value();
   
   public ValueSource(LineSource source, Tokenizer tokenizer, ColumnFormat[] formats)
   {
      super(source,tokenizer);
      this.formats = formats;
   }
   public int columns(boolean forceCalculation)
   {
      return formats.length;
   }
   public Value getValue(int column)
   {
      formats[column].parse(value,getToken(column));
      return value;
   }
}
