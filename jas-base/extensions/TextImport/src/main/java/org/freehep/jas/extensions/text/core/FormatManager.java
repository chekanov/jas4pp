package org.freehep.jas.extensions.text.core;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author  Tony Johnson
 */
public class FormatManager
{
   private static ColumnFormat[] availableFormats = 
   {
      new IntegerFormat(),
      new DateTimeFormat(),
      new DoubleFormat(), 
      new StringFormat()
   };
   private static Map map = new HashMap();
   static
   {
      for (int i=0; i<availableFormats.length; i++)
      {
         ColumnFormat format = availableFormats[i];
         map.put(format.getName(),format);
      }
   }
   public static ColumnFormat[] getAvailableFormats()
   {
      return availableFormats;
   }
   public static ColumnFormat formatForName(String name)
   {
      return (ColumnFormat) map.get(name);
   }
}
